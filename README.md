# Asynchronous Actors for Kotlin

This work is in very early stages of development. It should absolutely not be used for anything serious.

## What is an Actor?
Actors are self-contained workers that communicate via messages.  
When an actor receives a message, it can react by performing any kind of computation, sending messages to other actors, creating other actors, manipulate its internal state or produce any kind of side-effects, like updating UI, moving a robot arm or talking to a database.

Actors are single-threaded, so every internal processing in the actor is inherently threadsafe. In this way, they are comparable to coroutines, but provide a more comprehensive framework to work with largely distributed and scalable systems.

## Creating an Actor

The simplest way to create an actor is the `spawn` function.

```kotlin
val actor = spawn { message ->
    when (message) {
        
        // An example of this actor sending a message to another actor
        is Juliet -> tell(romeo, SecretMessage("Meet me at the balcony!"))

        // An example of this actor spawning another actor as its child
        is Faust -> spawnChild(mephisto)

        // An example of this actor changing its behavior
        // (Ungeziefer would be a behavior function just like this one)
        is GeorgSamsa -> replaceBehavior(Ungeziefer)
    }
}
``` 

The return value of `spawn` is a `PID` (Process identifier). This is a lightweight data object that describes how to find an actor:
```kotlin
data class PID(val address: String, val id: String)
```

Each actor has an id (which can be automatically generated or chosen beforehand) and an address where it is hosted. This is usually an IP Address. We can send messages to the actor, knowing nothing more than the `PID`, by delegating all the networking concerns to the routing system of ProtoActor.

## Sending messages

We have already seen a use of `tell`, the main function used to send a message to an actor.
```kotlin
fun tell(receiver: PID, message: Any)
```
Message can be any type of object. However, **it is very important** to just use immutable data objects (often referred to as DTO, POCO or POJO), otherwise our threadsafeness-guarantees are voided. This is also required when sending messages across the network, where the messages must be serialized (via [Protobuf](https://github.com/google/protobuf)). More about this later.

# Configuring Actors

## The Mailbox

When we sent a message to an actor, it first gets delivered to its mailbox. The mailbox orders messages that might come from many different threads and puts them into a queue for the actor to process. The default Mailbox uses a simple FIFO queue (first in, first out), but other implementations, such as a priority queue are available as well.

## Behavior

Each actor has exactly one behavior (a function which acts on the current context) at a time. But this behavior can change over time. An actor can be built like a state machine, assuming a new behavior in reaction to some message, maybe reverting back to the old behavior at a later stage.

```kotlin
// Behavior is defined as an extension method to Context, so we can directly 
// access the context from inside the funtion.
typealias Behavior = Context.() -> Unit

// Replaces the whole behavior stack with a single behavior and makes it active.
fun Context.replaceBehavior(behavior: (Context) -> Unit)

// Adds the given behavior to the stack and makes it active.
fun Context.pushBehavior(behavior: (Context) -> Unit)

// Removes the active behavior from the stack and makes the previous behavior active.
fun Context.popBehavior()
```

## Supervision

Failures happen. This realisation is built into the actor model. When an actor crashes, its supervisor is activated to handle the error.  
**TODO**

