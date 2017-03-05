package protoactor

typealias Spawner = (id: String, props: Props, parent: ActorRef?) -> ActorRef

fun defaultSpawner(id: String, props: Props, parent: ActorRef?): ActorRef {
  val ctx = LocalContext(props.producer, props.supervisionStrategy, props.middlewareChain, props.parent)
  val mailbox = props.mailboxProducer()
  val dispatcher = props.dispatcher
  val process = LocalProcess(mailbox)
  val ref = ProcessRegistry.register(id, process)
  ctx.self = ref
  ctx.process = process
  mailbox.registerHandlers(ctx, dispatcher)
  mailbox.postSystemMessage(Started())
  mailbox.start()
  return ref
}
