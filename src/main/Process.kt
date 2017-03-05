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
    postSystemMessage(Stop)
    isDead.set(true)
  }
}

internal object ProcessRegistry {
  fun get(id: String) = processes[id]

  fun register(process: Process) = register(generateId(), process)
  fun register(id: String, process: Process): PID {
    processes.put(id, process)
    return PID("localhost", id)
  }

  fun unregister(ref: PID) = processes.remove(ref.id)

  private val processes = ConcurrentHashMap<String, Process>()
  private val nextId = AtomicInteger(0)
  fun generateId() = nextId.getAndIncrement().toString()
}

internal val PID.process
  get() = resolveProcess(this)

internal fun resolveProcess(ref: PID): Process {
  if (ref.address == "localhost") {
    return ProcessRegistry.get(ref.id)!!
  } else {
    throw NotImplementedError()
  }
}




