# flume-redis-sink
Flume-Redis sink, for populating message into Redis.

## Current Version & Function

* Version:
  * 0.0.1
* Function:
  * Sink for publishing message to Redis Channel
* TBD:
  * Sink for `set` ,`hset`,`lpush`,`sadd`
  * Batch event processing

## Getting Started

1. Clone the repository to build OR Download jar
2. Copy the jar to your library path(For simplify, you can just copy the jar to $FLUME_HOME/lib)
3. write configuration file(for sample you can find in ./conf)
4. run flume