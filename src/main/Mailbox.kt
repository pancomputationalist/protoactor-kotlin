package protoactor

import kotlinx.coroutines.experimental.*
import org.jctools.queues.*
import java.util.concurrent.atomic.*

typealias Message = Any

interface MessageReceiver {
  fun postUserMessage(msg: Message)
  fun postSystemMessage(msg: SystemMessage)
}

interface Mailbox : MessageReceiver {
  fun registerHandlers(invoker: MessageInvoker, dispatcher: Dispatcher)
  fun start()
}

fun boundedMailbox(size: Int) = DefaultMailbox(MpscArrayQueue<Message>(size), MpscUnboundedArrayQueue<SystemMessage>(8))
fun unboundedMailbox() = DefaultMailbox(MpscUnboundedArrayQueue<Message>(8), MpscUnboundedArrayQueue<SystemMessage>(8))

class DefaultMailbox(
  val userMessages: MessagePassingQueue<Message>,
  val systemMessages: MessagePassingQueue<SystemMessage>
) : Mailbox {
  override fun start() {
  }

  lateinit var invoker: MessageInvoker
  lateinit var dispatcher: Dispatcher

  override fun registerHandlers(invoker: MessageInvoker, dispatcher: Dispatcher) {
    this.invoker = invoker
    this.dispatcher = dispatcher
  }

  override fun postUserMessage(msg: Message) {
    logVerbose("postUserMessage $msg")
    var success = userMessages.offer(msg)
    // TODO error handling if full
    startActorIfIdle()
  }

  override fun postSystemMessage(msg: SystemMessage) {
    logVerbose("postSystemMessage $msg")
    systemMessages.offer(msg)
    // TODO error handling if full
    startActorIfIdle()
  }

  private val busy = AtomicBoolean()

  private fun startActorIfIdle() {
    if (busy.compareAndSet(false, true)) {
      logVerbose("actor is idle, scheduling to run")
      dispatcher.schedule { runActor() }
    } else {
      logVerbose("actor still busy")
    }
  }

  private suspend fun runActor() {
    val done = processMessages()
    if (!done) return

    busy.set(false)

    if (!userMessages.isEmpty || !systemMessages.isEmpty) {
      startActorIfIdle()
    }
  }

  var suspended = false
  private suspend fun processMessages(): Boolean {
    for (i in 0..dispatcher.throughput) {
      val sysmsg = systemMessages.poll()
      if (sysmsg != null) {
        if (sysmsg is SuspendMailbox) suspended = true
        if (sysmsg is ResumeMailbox) suspended = false
        val job = launch(Unconfined) { invoker.invokeSystemMessage(sysmsg) }
        if (!job.isCompleted) {
          // TODO if task didn't complete immediately, halt processing and reschedule a new run when task completes
          return false
        }
        continue
      }
      if (suspended) break
      val usermsg = userMessages.poll()
      if (usermsg != null) {
        val job = launch(Unconfined) { invoker.invokeUserMessage(usermsg) }
        if (!job.isCompleted) {
          // TODO if task didn't complete immediately, halt processing and reschedule a new run when task completes
          return false
        }
      }
      break
    }
    return true
  }
}