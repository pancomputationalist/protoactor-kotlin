package protoactor

import org.junit.*

class MessageProcessing {
  @After @Before fun stopAllThreads() {
    waitForIdle()
  }

  @Test fun unboundedQueue() {
    val actor = spawn { }
    repeat(10_000) { tell(actor, 42) }
  }

  @Test fun boundedQueue() {
    var lastValue = 0
    val props = Props(
      producer = { { msg -> if (msg is Int) lastValue = msg } },
      mailboxProducer = { boundedMailbox(8) },
      dispatcher = TestDispatcher
    )
    val actor = spawn(props)
    for (i in 1..100) {
      tell(actor, i)
    }
    TestDispatcher.drain()
    assert(lastValue == 8)
  }
}