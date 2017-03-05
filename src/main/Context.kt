package protoactor

interface Context : ActorContext, MessageContext

interface ActorContext {
  val activeBehavior: Behavior
  val self: PID
  val parent: PID?
  val children: List<PID>?
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
  override var children: MutableList<PID>? = null

  override var activeBehavior = producer()
  override val sender = null

  lateinit var process: Process

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
        is Started -> invokeUserMessage(msg)
        is Stop -> handleStop()
        is Terminated -> handleTerminated()
        is Watch -> handleWatch()
        is Unwatch -> handleUnwatch()
        is Failure -> handleFailure()
        is Restart -> handleRestart()
        is SuspendMailbox -> {
        }
        is ResumeMailbox -> {
        }
        else -> {
        }
      }
      return
    } catch (ex: Exception) {
      logError("invokeSystemMessage", ex)
    }
  }

  override suspend fun escalateFailure(msg: Message, reason: Exception) {
  }

  private var restarting = false
  private var stopping = true
  private suspend fun handleStop() {
    restarting = false
    stopping = true
    invokeUserMessage(Stopping)
    children?.forEach { stop(it) }
    tryRestartOrTerminateAsync()
  }

  private suspend fun tryRestartOrTerminateAsync() {
    if (children?.isNotEmpty() == true) return
    if (restarting) restart()
    if (stopping) stop()
  }

  private suspend fun restart() {
    activeBehavior = producer()
    tell(self, ResumeMailbox)
    invokeUserMessage(Started)
  }

  private suspend fun stop() {
    ProcessRegistry.unregister(self)
    invokeUserMessage(Stopped)
  }

  private suspend fun handleTerminated() {
  }

  private suspend fun handleWatch() {
  }

  private suspend fun handleUnwatch() {
  }

  private suspend fun handleFailure() {
  }

  private suspend fun handleRestart() {
  }
}

