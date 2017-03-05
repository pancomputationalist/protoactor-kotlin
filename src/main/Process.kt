package protoactor

import java.util.concurrent.*
import java.util.concurrent.atomic.*

interface Process : MessageReceiver {
  fun stop()
}

class LocalProcess(val mailbox: Mailbox) : Process {
  override fun postSystemMessage(msg: SystemMessage) = mailbox.postSystemMessage(msg)
  override fun postUserMessage(msg: Message) = mailbox.postUserMessage(msg)
  var isDead = AtomicBoolean(false)
  override fun stop() {
    postSystemMessage(Stop())
    isDead.set(true)
  }
}

internal object ProcessRegistry {
  fun get(id: String) = processes[id]

  fun register(process: Process) = register(generateId(), process)
  fun register(id: String, process: Process): ActorRef {
    processes.put(id, process)
    return ActorRef(id, "localhost")
  }

  private val processes = ConcurrentHashMap<String, Process>()
  private val nextId = AtomicInteger(0)
  fun generateId() = nextId.getAndIncrement().toString()
}

internal fun resolveProcess(ref: ActorRef): Process {
  if (ref.process == "localhost") {
    return ProcessRegistry.get(ref.id)!!
  } else {
    throw NotImplementedError()
  }
}




