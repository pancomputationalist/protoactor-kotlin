package protoactor

import kotlinx.coroutines.experimental.*

typealias Task = suspend CoroutineScope.() -> Unit

interface Dispatcher {
  val throughput: Int
  fun schedule(task: Task) // todo async
}

class ThreadPoolDispatcher : Dispatcher {
  override val throughput = 300
  override fun schedule(task: Task) {
    launch(CommonPool, true, task)
  }
}

interface MessageInvoker {
  suspend fun invokeUserMessage(msg: Message)
  suspend fun invokeSystemMessage(msg: SystemMessage)
  suspend fun escalateFailure(msg: Message, reason: Exception)
}