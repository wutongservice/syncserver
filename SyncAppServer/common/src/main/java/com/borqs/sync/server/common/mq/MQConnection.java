package com.borqs.sync.server.common.mq;

import java.util.Properties;
import java.util.logging.Logger;

import com.borqs.sync.server.common.runtime.Context;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class MQConnection {
	private Logger log;
	
	private String host = "127.0.0.1";
	private int port = 6379;
	
	private static Jedis jedis;

	public static MQConnection getInstance(Context context) {
		return new MQConnection(context);
	}

	private MQConnection(Context context) {
		log = context.getLogger();
		Properties redisSettings = context.getConfig().getRedisSettings();
		this.host = redisSettings.getProperty("host","127.0.0.1");
		this.port = Integer.valueOf(redisSettings.getProperty("port","6379"));
		
		log.info("redis host is " + this.host+", port is"+this.port);
		
		jedis = new Jedis(this.host, this.port);
	}

	/**
	 * subscribe queues or topics
	 * @param listener
	 * @param channels
	 */
	public synchronized void doSubscribe(MQListener listener, String... channels) {
		try {
			log.info("doSubscribe " + channels);
			jedis.subscribe(listener, channels);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			log.info("doSubscribe" + e.getMessage());
		}
	}
	
	public synchronized void publish(String channel, String message) {
		jedis.publish(channel, message);
	}

	public void disconnect() {
		jedis.disconnect();
	}
}
