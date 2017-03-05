package protoactor

import kotlinx.coroutines.experimental.*
import java.util.concurrent.*

fun waitForIdle() = ForkJoinPool.commonPool().awaitTermination(5, TimeUnit.SECONDS)

// Singlethreaded dispatcher that can be manually triggered
// to process single or bulk messages
object TestDispatcher : Dispatcher {

  override var throughput = 1

  val queue = LinkedBlockingDeque<Task>()
  override fun schedule(task: Task) {
    queue.add(task)
  }

  fun process(n: Int) {
    repeat(n) {
      if (queue.isEmpty()) return
      runBlocking { queue.poll()() }
    }
  }

  fun drain() {
    while (queue.isNotEmpty())
      runBlocking { queue.poll()() }
  }

}