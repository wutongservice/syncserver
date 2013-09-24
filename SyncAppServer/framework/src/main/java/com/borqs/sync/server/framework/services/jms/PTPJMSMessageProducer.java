/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.jms;

import com.borqs.sync.server.common.notification.MessagePublisher;
import com.borqs.sync.server.common.runtime.Context;

import javax.jms.*;

/**
 * Date: 9/8/11 Time: 6:38 PM Queue Producer
 */
public class PTPJMSMessageProducer extends MessagePublisher {

	private Connection mConnection;
	private Session mSession;
	private MessageProducer mProducer;

	public PTPJMSMessageProducer(Context context, String identifier) {
        super(context);
		mConnection = JmsUtil.createConnection(getContext());
		mSession = JmsUtil.createSession(mConnection, false,
				Session.AUTO_ACKNOWLEDGE);
		try {
			Destination destination = mSession.createQueue(identifier);
			// Create a MessageProducer from the Session to theQueue
			mProducer = mSession.createProducer(destination);
			mProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            mConnection.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void close(){
      JmsUtil.close(mConnection,mSession,mProducer);
    }

	@Override
	public void send(String message) {
		try {
			TextMessage msg = mSession.createTextMessage(message);

            mProducer.send(msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
