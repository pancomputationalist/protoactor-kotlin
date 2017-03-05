package protoactor

fun tell(receiver: ActorRef, message: Message)
  = resolveProcess(receiver).postUserMessage(message)

fun request(actor: ActorRef, message: Message, respondTo: ActorRef)
  = tell(actor, MessageEnvelope(message, respondTo))

fun spawn(behavior: Behavior)
  = spawn(ProcessRegistry.generateId(), Props({ behavior }))

fun spawn(id: String, props: Props)
  = props.spawner(id, props, null)