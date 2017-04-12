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

public class RedisCommonSink extends AbstractRedisSink{
	private static final Logger logger = LoggerFactory.getLogger(RedisCommonSink.class);

	private int redisDatabase;
	private String redisDataType;
	private byte[] redisKey;

	public Status process() throws EventDeliveryException {
		Status status = null;
		Channel ch = getChannel();
		Transaction txn = ch.getTransaction();
		txn.begin();
		try{
			Event event = ch.take();
			if(event == null){
				status = Status.BACKOFF;
				txn.commit();
			} else{
				byte[] serialized = serializer.serialize(event);
				// TODO: add code reuse here
				if("redisList".equals(redisDataType)){
					if (jedis.lpush(redisKey, serialized) > 0){
						txn.commit();
					} else{
						throw new EventDeliveryException("Cannot push event to list: " + redisKey);
					}
				} else if ("redisSet".equals(redisDataType)){
					if (jedis.sadd(redisKey, serialized) > 0){
						txn.commit();
					} else{
						throw new EventDeliveryException("Cannot add event to set: " + redisKey);
					}
				} else{  // "redisString"
					if (jedis.append(redisKey, serialized) > 0){
						txn.commit();
					} else{
						throw new EventDeliveryException("Cannot append event to String: " + redisKey);
					}
				}
				status = Status.READY;
			}
		} catch(Throwable t){
			logger.error("Falied to push event", t);
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
	public void configure(Context context){
		redisDatabase = context.getInteger("redisDatabase", 0);
		redisDataType = context.getString("redisDataType", "redisList");
		redisKey = context.getString("redisKey").getBytes();
		
		Preconditions.checkNotNull(redisKey, "Redis key cannot be null.");
		super.configure(context);
    		logger.info("Redis Common Sink configured");
	}
	
	@Override
	public void start(){
		super.start();
		
		if(redisDatabase != 0){
			if (!"OK".equals(jedis.select(redisDatabase))){
				throw new RuntimeException("Cannot select database " + redisDatabase);	
			}
		}
	}
	
	@Override
	public void stop(){
		jedis.disconnect();
        super.stop();
	}

	

}
