package protoactor

import org.junit.*


class ActorTests {
  @Test fun actorSpawnsWithAssignedId() {
    val name = PID("test_address", "test_id")
    val props = Props(
      producer = { { msg -> {} } },
      spawner = { id, p, parent -> name })
    val pid = spawn(props)

    assert(pid == name)
  }

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

    waitForIdle()
    assert(received)
  }
}