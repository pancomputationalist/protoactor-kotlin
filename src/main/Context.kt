package protoactor

interface Context : ActorContext, MessageContext

interface ActorContext {
  val activeBehavior: Behavior
  val self: PID
  val parent: PID?
  val children: Set<PID>?
}

interface MessageContext {
  val sender: PID?
  fun respond(msg: Message) = tell(sender!!, msg)
}

class LocalContext(
  val producer: () -> Behavior,
  val supervisionStrategy: () -> Unit,
  val middlewareChain: (Message) -> Message,
  override val parent: PID?
) : Context, MessageInvoker {

  override lateinit var self: PID
  override var children: HashSet<PID>? = null

  override var activeBehavior = producer()

  override val sender = null

  var watching: HashSet<PID>? = null
  var watchers: HashSet<PID>? = null

  lateinit var process: Process
  private var restarting = false
  private var stopping = true


  override suspend fun invokeUserMessage(msg: Message) {
    logVerbose("invokeUserMessage $msg")
    val msg = middlewareChain(msg)
    if (msg is PoisonPill) {
      process.stop()
    }
    activeBehavior(msg)
  }

  override suspend fun invokeSystemMessage(msg: SystemMessage) {
    logVerbose("invokeSystemMessage $msg")
    try {
      when (msg) {

        is Started -> {
          invokeUserMessage(msg)
        }

        is Stop -> {
          restarting = false
          stopping = true
          invokeUserMessage(Stopping)
          children?.forEach { stop(it) }
          tryRestartOrTerminateAsync()
        }

        is Terminated -> {
          children?.remove(msg.who)
          watching?.remove(msg.who)
          invokeUserMessage(msg)
          tryRestartOrTerminateAsync()
        }

        is Watch -> {
          watchers = watchers ?: HashSet<PID>()
          watchers!!.add(msg.watcher)
        }

        is Unwatch -> {
          watchers?.remove(msg.watcher)
        }

        is Failure -> {
          // supervision
        }

        is Restart -> {
          activeBehavior = producer()
          tell(self, ResumeMailbox)
          invokeUserMessage(Started)
        }

        is SuspendMailbox -> {
        }

        is ResumeMailbox -> {
        }

        else -> {
          logError("unknown system message $msg")
        }
      }
      return
    } catch (ex: Exception) {
      logError("invokeSystemMessage", ex)
    }
  }

  override suspend fun escalateFailure(msg: Message, reason: Exception) {
  }


  private suspend fun tryRestartOrTerminateAsync() {
    if (children?.isNotEmpty() == true) return
    if (restarting) restart()
    if (stopping) stop()
  }

  private suspend fun restart() {
  }

  private suspend fun stop() {
    ProcessRegistry.unregister(self)
    invokeUserMessage(Stopped)
  }
}

