package protoactor

sealed class SystemMessage : Message()

class MessageEnvelope(val message: Any, val sender: ActorRef) : SystemMessage()
class PoisonPill : SystemMessage()
class Started : SystemMessage()
class Stop : SystemMessage()
class Stopped : SystemMessage()
class Restarted : SystemMessage()
class Terminated : SystemMessage()
class Watch : SystemMessage()
class Unwatch : SystemMessage()
class Failure : SystemMessage()
class Restart : SystemMessage()
class SuspendMailbox : SystemMessage()
class ResumeMailbox : SystemMessage()
