package com.borqs.sync.server.common.mq;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPubSub;

public class MQListener extends JedisPubSub {

	private static Logger log = Logger.getLogger(MQListener.class);

	@Override
	public void onMessage(String channel, String message) {
		try {
			processMessage(channel, message);
		} catch (Exception e) {
			log.error("onMessage", e);
		}
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub

	}

	public void processMessage(String channel, String msg) {

	}

	public void cancel() {
		this.unsubscribe();
	}

}
