/**
 * @ClassName:     RedisSink.java
 * @Description:   TO publish event to the designated channel of redis 
 * @author          wttttt
 * @Github https://github.com/wttttt-wang
 * @version         V0.0.2  
 * @Date           2017.04.11
 */
package com.wttttt.flume.plugins;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import redis.clients.jedis.exceptions.JedisException;

public class RedisPublishSink extends AbstractRedisSink{
	private static final Logger logger = LoggerFactory.getLogger(RedisPublishSink.class);

    private byte[] redisChannel;

    public Status process() throws EventDeliveryException{
    	Status status = null;
   	 
   	 // Start transaction
   	 Channel ch = getChannel();
   	 Transaction txn = ch.getTransaction();
   	 txn.begin();
   	 try{
   		// TODO: batchsize
   		Event event = ch.take();
   		if (event == null){
   			status = Status.BACKOFF;
   			txn.commit();
   		}else{
   		// serialize event to String
        byte[] serialized = serializer.serialize(event);

   		if (jedis.publish(redisChannel, serialized) > 0){
            txn.commit();
        } else{
        		// throw
            throw new EventDeliveryException("Event published, but no subscriber named" + redisChannel);
        }
   		status = Status.READY;
   		}
   	 } catch (Throwable t){
   		logger.error("Failed to publish events", t);
   		 status = Status.BACKOFF;
   		if (txn != null){
			try{
				txn.rollback();
			} catch(Exception e){
				logger.error("Transaction rollback failed", e);
				throw Throwables.propagate(e);
			}
		}
   		 
   		 if (t instanceof JedisException){
   			 // TODO:how should we handle with JedisException
   			 jedis.disconnect();
   			 throw new JedisException(t);
   		 }
   		throw new EventDeliveryException("Failed to publish events", t); 
   		 
   	 } finally{
   		if (txn != null){
			txn.close();
		}
   	 }
   	 return status;
    }


    @Override
    public synchronized void stop(){
    		jedis.disconnect();
        super.stop();
    }

    /**
     * We configure the sink and generate properties for redis
     * 1. we generate a properties object with some static defaults that
     * can be overridden by Sink configuration
     * 2. We add the configuration users added for Redis (parameters starting
     * with .redis. and must be valid redis properties
     * 3. We add the sink's documented parameters which can override other
     * properties
     * @param context
     */
    public void configure(Context context){
    	redisChannel = context.getString("redisChannel").getBytes();
    	Preconditions.checkNotNull(redisChannel, "Redis Channel cannot be null");
    	
    	super.configure(context);
    	logger.info("Redis Publish Sink configured");
    }

}

