package protoactor

typealias Behavior = suspend Context.(msg: Message) -> Unit