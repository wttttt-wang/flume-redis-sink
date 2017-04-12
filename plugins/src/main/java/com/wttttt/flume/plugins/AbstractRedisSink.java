package com.wttttt.flume.plugins;

import org.apache.flume.Context;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.wttttt.flume.plugins.Serializer.AbstractSerializer;

import redis.clients.jedis.BinaryJedis;

public abstract class AbstractRedisSink extends AbstractSink implements Configurable{
	protected static final Logger logger = LoggerFactory.getLogger(AbstractRedisSink.class);

    protected BinaryJedis jedis;
    private String redisHost;
    private int redisPort;
    private int redisTimeout;
    private String redisPwd;
    protected AbstractSerializer serializer;

    public synchronized void start(){
    		jedis = new BinaryJedis(redisHost, redisPort, redisTimeout);
    		if (!"".equals(redisPwd)){
    			jedis.auth(redisPwd);
    		}
    		try{
    			jedis.connect();
    		} catch(Exception ex){
    			logger.error("Unable to connect to Redis, host: " + redisHost + ", port: " + redisPort, ex);
    			return;
    		}
        super.start();
        
        logger.info("Already connect to Redis " + redisHost + " port " + String.valueOf(redisPort));
    }

    @Override
    public synchronized void stop(){
    		jedis.disconnect();
        super.stop();
    }

    public void configure(Context context){
    		// basic configuration for redis
    		redisHost = context.getString("redisHost", "localhost");
    		redisPort = context.getInteger("redisPort", 6379);  // default port is 6379
    		redisTimeout = context.getInteger("redisTimeout", 2000);
    		redisPwd = context.getString("redisPwd", "");

    		// configure Serializer
    		String serializerName = context.getString("serializer", "com.wttttt.flume.plugins.Serializer.RawSerializer");
    		try{
    			@SuppressWarnings("unchecked")
				Class<? extends AbstractSerializer> clazz = (Class<? extends AbstractSerializer>) Class.forName(serializerName);
            serializer = clazz.newInstance();
    		} catch(ClassNotFoundException ex){
    			logger.error("Cannot instantiate event serializer");
    		} catch (InstantiationException e) {
                logger.error("Cannot not instantiate event serializer", e);
                Throwables.propagate(e);
        } catch (IllegalAccessException e) {
                logger.error("Cannot not instantiate event serializer", e);
                Throwables.propagate(e);
        }
    }



}
