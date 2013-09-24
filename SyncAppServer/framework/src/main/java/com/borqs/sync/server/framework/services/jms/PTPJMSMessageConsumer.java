/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.jms;

import com.borqs.sync.server.common.notification.MessageConsumer;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ContextHolder;

import javax.jms.*;

/**
 * Created by IntelliJ IDEA. User: b211 Date: 9/14/11 Time: 3:36 PM To change
 * this template use File | Settings | File Templates.
 */
public class PTPJMSMessageConsumer extends ContextHolder{
	private MessageConsumer mConsumer;

	public PTPJMSMessageConsumer(Context context) {
        super(context);
	}

	public boolean register(MessageConsumer c) {
		try {
           getContext().getLogger().info("=================register consumer " + c.getIndentifier());
			mConsumer = c;
			Connection connection = JmsUtil.createConnection(getContext());
			Session mSession = JmsUtil.createSession(connection, false,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = mSession
					.createQueue(c.getIndentifier());
			// Create a MessageProducer from the Session to theQueue
			javax.jms.MessageConsumer jmsConsumer = mSession
					.createConsumer(destination);
			connection.start();
			jmsConsumer.setMessageListener(new ChangeListener());
		} catch (JMSException e) {
			e.printStackTrace();
            return false;
		}
        return true;
	}

	class ChangeListener implements MessageListener {

		@Override
		public void onMessage(Message message) {
            getContext().getLogger().info("=================onMessage:" + message);
			if (message instanceof TextMessage) {
				try {
                    String messageBody = ((TextMessage) message).getText();
                    getContext().getLogger().info("jms message:" + messageBody);
					mConsumer.runOnMessage(messageBody);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
