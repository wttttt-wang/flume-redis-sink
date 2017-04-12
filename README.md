# flume-redis-sink
Flume-Redis sink, `publish` `lpush` `append` `sadd` supported.



## Current Version & Function

* Version:
  * 0.0.2
* Function:
  * Sink for `publish` message to Redis Channel.
  * Sink for `lpush` `append` `sAdd ` message to Redis.
  * Providing `json` serializer and raw serializer.
* TBD:
  * Batch event processing
  * Redis connecting pool
  * Exception handling



## Getting Started

1. Clone the repository to build **OR** Download jar
2. Copy the jar to your library path(For simplify, you can just copy the jar to $FLUME_HOME/lib)
3. write configuration file(for sample you can find in ./conf)
4. run flume



## Update Log

* V0.0.1:
  * Sink for publishing message to Redis Channel.
  * Raw serializer for serializing the event body.
* V0.0.2:
  * Add Sink for `lpush` `append` `sAdd ` message to Redis.
  * Add json serializer for serializing headers and body.