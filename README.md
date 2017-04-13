# flume-redis-sink
Flume-Redis sink, `publish` `lpush` `sadd` supported.

## Current Version & Function

* Version:
  * 0.0.3
* Function:
  * Sink for **`publish`** message to Redis Channel.
  * Sink for **`lpush`  `sadd `** message to Redis.
  * Providing **`json` serializer** and raw serializer.
  * Add **batch event processing** for `lpush` and `sadd`.
* TBD:
  * Exception handling. more...



## Getting Started

1. Clone the repository to build **OR** Download jar
2. Copy the jar to your library path
   * For simplify, you can just copy the jar to $FLUME_HOME/lib
   * Use `plugins-xxx-SNAPSHOT-jar-with-dependencies.jar` for simplify.
3. write configuration file(for sample you can find in ./conf)
4. run flume



## Update Log

* V0.0.1:
  * Sink for publishing message to Redis Channel.
  * Raw serializer for serializing the event body.
* V0.0.2:
  * Add Sink for `lpush` `append` `sAdd ` message to Redis.
  * Add **json serializer** for serializing headers and body.
* V0.0.3:
  * Add **batch event processing** for `lpush` and `sadd`.
  * Remove `append` for redis type String.
  * Better **log** information.

## Contact Me

* For any comments or suggestions,  **do not hesitate to contact me** on ting.wang77777@gmail.com.