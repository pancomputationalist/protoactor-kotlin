# Mailbox

## Message processing
- ~~Unbounded queue~~
- ~~Bounded queue~~
- Escalate failure on invoker errors
- Limit throughput per each mailbox schedule

## Statistics
- User messages
- System messages
- Started event
- Empty event

## Concurrency
- ~~Message posting can be done concurrently~~
- ~~Message receive is done sequentially~~
- Yield control on I/O calls

# Actor

## Features
- Create actor from function/method
- Create actor from object factory
- Spawn actor with automatic/prefixed/specific name

# Props

## Settings
- Actor producer
- Mailbox producer
- Supervisor strategy
- Dispatcher
- Actor spawner
- Middleware

# Context

## Data
- Parent PID
- Self PID
- Sender PID
- Children PIDs
- Current message
- Current Actor

## Features
- Respond to sender
- Stash current message pending restart
- Spawn child actor with automatic/prefixed/specific name
- Stop/restart/resume children
- Set/push/pop actor behaviors (become/unbecome)
- Watch/unwatch actors
- Receive timeout 

# ProcessRegistry
- Get Process by PID
- Add local Process with ID
- Remove Process by PID
- Generate next Process ID

# Process
- Send user message to Process
- Send system message to Process
- Stop Process

# Supervision

## Directives
- Resume
- Restart
- Stop
- Escalate

## Strategies
- OneForOneStrategy applies directive to failed child

# PID

## Features
- Holds address (nonhost or remote address) and ID
- Send user message
- Send system message
- Request
- Request future 
- Stop

# Future process

- Auxiliary process used to provide an awaitable future containing the response to a request

# Dead letter process

- Auxiliary process used to collect messages sent to non-existing processes