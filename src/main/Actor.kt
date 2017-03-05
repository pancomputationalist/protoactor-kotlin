package protoactor

data class ActorRef(val id: String, val process: String)
typealias Behavior = suspend Context.(msg: Message) -> Unit