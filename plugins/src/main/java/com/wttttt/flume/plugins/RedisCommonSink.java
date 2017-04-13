package com.wttttt.flume.plugins;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.Sink.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import redis.clients.jedis.exceptions.JedisException;

public class RedisCommonSink extends AbstractRedisSink{
	private static final Logger logger = LoggerFactory.getLogger(RedisCommonSink.class);

	private int redisDatabase;
	private String redisDataType;
	private String redisFuncType;
	private byte[] redisKey;
	private Integer redisBatchSize;

	public Status process() throws EventDeliveryException {
		Status status = Status.READY;
		Channel channel = getChannel();
		Transaction txn = null;
		Event event = null;
		List<byte[]> batchEvents = new ArrayList<byte[]>(redisBatchSize);
		try{
			long processedEvents = 0;
			txn = channel.getTransaction();
			txn.begin();
			for (; processedEvents < redisBatchSize; processedEvents += 1){
				event = channel.take();
				if (event == null){
					// no events available in channel
					if (processedEvents == 0){
						txn.commit();
						status = Status.BACKOFF;
					}
					break;
				}
				logger.debug("Adding event, event body: " + new String(event.getBody()) + " to batch.");
				batchEvents.add(serializer.serialize(event));
			}
			
			// publish batch and commit
			if (batchEvents.size() > 0) {
				logger.info("Sending " + batchEvents.size() + " events");
				
				byte[][] redisEvents = new byte[batchEvents.size()][];
				int index = 0;
				for (byte[] redisEvent : batchEvents){
					redisEvents[index] = redisEvent;
					index ++;
				}
				
				Method method = jedis.getClass().getMethod(redisFuncType, byte[].class, byte[][].class);
				if ( Long.valueOf(String.valueOf(method.invoke(jedis, redisKey, redisEvents))).longValue() > 0 ){
					txn.commit();
				} else{
					throw new EventDeliveryException("Cannot push event to " + redisDataType + ", key:" + redisKey);
				}
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
		redisBatchSize = context.getInteger("redisBatchSize", 100);
		Preconditions.checkState(redisBatchSize > 0, "BatchSize must be greater than 1");
		redisDatabase = context.getInteger("redisDatabase", 0);
		redisDataType = context.getString("redisDataType", "redisList");
		redisKey = context.getString("redisKey").getBytes();
		if("redisSet".equals(redisDataType)){
			redisFuncType = "sadd";
		} else{
			redisFuncType = "lpush";
		}
		
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
			logger.info("DataBase: " + String.valueOf(redisDatabase) + "selected");
		}
	}
	
	@Override
	public void stop(){
		// jedis.disconnect();
        super.stop();
	}

	

}
