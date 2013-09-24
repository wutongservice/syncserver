/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.jms;

import com.borqs.sync.server.framework.BaseService;
import com.borqs.sync.server.framework.ServiceDescriptor;
import com.borqs.sync.server.common.notification.MessageConsumer;
import com.borqs.sync.server.common.runtime.Context;

import java.lang.reflect.InvocationTargetException;

/**
 * Date: 9/8/11 Time: 6:47 PM
 */
public final class JMSService extends BaseService {
	private static final String IDENTIFIER = "JMS_service";
    private static final String JMS_PTP_CONSUMER_PREFIX = "jms.ptp.consumer.identifier";

    private MessageConsumer mConsumer;

    public JMSService(Context context){
        super(context);
    }

	public void runSynchronized(Context context) {
		PTPJMSMessageConsumer pjConsumer = new PTPJMSMessageConsumer(context);
        context.getLogger().info("==========register " + pjConsumer);
		boolean successful = pjConsumer.register(mConsumer);

        if(!successful){
            context.getLogger().info("==========register " + pjConsumer + " failed!");
            return;
        }

        while(isRunning()){
            synchronized (this){
                try {
                    wait(3000);
                } catch (InterruptedException e) {
                }
            }
        }
	}

	@Override
	public void stop() {
        //TODO need to stop
        mConsumer = null;
	}

	@Override
	public boolean isRunning() {
		return mConsumer != null;
	}

	@Override
	protected String getIdentifier() {
		return IDENTIFIER; // To change body of implemented methods use File |
							// Settings | File Templates.
	}

	public boolean init(ServiceDescriptor descriptor) {
        if( !super.init(descriptor) ){
            return false;
        }

        Object instance = newInstance(String.valueOf(descriptor.impl()), descriptor.desc());
	    if (instance != null && instance instanceof MessageConsumer) {
            mConsumer = (MessageConsumer)instance;
        }
        return true;
	}

	private Object newInstance(String consumerClassName, String id) {
		try {
			return Class.forName(consumerClassName)
                    .getConstructor(new Class[]{Context.class, String.class})
                    .newInstance(getContext(), id) ;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        }
		return null;
	}

}
