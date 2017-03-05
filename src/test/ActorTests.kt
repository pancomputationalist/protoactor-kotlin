package protoactor

import org.junit.*
import java.util.concurrent.*

fun waitForIdle() = ForkJoinPool.commonPool().awaitTermination(5, TimeUnit.SECONDS)

class ActorTests {
//  @Test fun actorStarts() {
//    var started = false
//    spawn { msg -> if (msg is Started) started = true }
//    waitForIdle()
//    assert(started)
//  }

  class MyUserMessage

  @Test fun actorReceivesUserMessage() {
    var received = false
    val actor = spawn { msg ->
      when (msg) {
        is MyUserMessage -> received = true
      }
    }
    tell(actor, MyUserMessage())
    Thread.sleep(5000)
    assert(received)
  }
}