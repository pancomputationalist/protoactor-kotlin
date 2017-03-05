package protoactor;

import org.junit.*
import kotlin.concurrent.*

public class Concurrency {
  @After @Before fun stopAllThreads() {
    waitForIdle()
  }

  @Test fun multiThreadedProducer() {
    var n = 0
    val actor = spawn {
      if (it is String && it == "increment") n++
    }

    val nThreads = 0
    val nMessages = 1

    repeat(nThreads) {
      thread {
        repeat(nMessages) {
          tell(actor, "increment")
        }
      }
    }

    waitForIdle()
    assert(n == nThreads * nMessages)
  }
}
