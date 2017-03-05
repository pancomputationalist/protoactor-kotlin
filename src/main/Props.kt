package protoactor

class Props(
  val producer: () -> Behavior,
  val mailboxProducer: () -> Mailbox = { unboundedMailbox() },
  val supervisionStrategy: () -> Unit = {},
  val middlewareChain: (Message) -> (Message) = { it },
  val parent: PID? = null,
  val dispatcher: Dispatcher = ThreadPoolDispatcher(),
  val spawner: Spawner = ::defaultSpawner
)