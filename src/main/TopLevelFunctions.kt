package protoactor

fun tell(receiver: PID, message: Message)
  = receiver.process.postUserMessage(message)

fun request(actor: PID, message: Message, respondTo: PID)
  = tell(actor, MessageEnvelope(message, respondTo))

fun spawn(behavior: Behavior)
  = spawn(ProcessRegistry.generateId(), Props({ behavior }))

fun spawn(props: Props)
  = props.spawner(ProcessRegistry.generateId(), props, null)

fun spawn(id: String, props: Props)
  = props.spawner(id, props, null)

fun stop(actor: PID)
  = actor.process.stop()