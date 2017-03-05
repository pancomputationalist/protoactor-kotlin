package protoactor

sealed class SystemMessage : Message()

class PID(val address: String, val id: String) : SystemMessage()
class Watch(val watcher: PID) : SystemMessage()
class Unwatch(val watcher: PID) : SystemMessage()
class Terminated(val who: PID, val addressTerminated: Boolean) : SystemMessage()
class MessageEnvelope(val message: Any, val sender: PID) : SystemMessage()

object PoisonPill : SystemMessage()
object Started : SystemMessage()
object Stop : SystemMessage()
object Stopping : SystemMessage()
object Stopped : SystemMessage()
object Restarted : SystemMessage()
object Failure : SystemMessage()
object Restart : SystemMessage()
object SuspendMailbox : SystemMessage()
object ResumeMailbox : SystemMessage()
