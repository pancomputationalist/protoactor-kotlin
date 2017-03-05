package protoactor

interface Context : ActorContext, MessageContext

interface ActorContext {
  val activeBehavior: Behavior
  val self: ActorRef
}

interface MessageContext {
  val sender: ActorRef?
  fun respond(msg: Message) = tell(sender!!, msg)
}

class LocalContext(
  val producer: () -> Behavior,
  val supervisionStrategy: () -> Unit,
  val middlewareChain: (Message) -> Message,
  val parent: ActorRef?
) : Context, MessageInvoker {
  override lateinit var self: ActorRef
  override val activeBehavior = producer()
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

  private suspend fun handleStop() {
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

