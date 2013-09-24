package com.borqs.sync.server.framework.services.mq;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import com.borqs.sync.server.common.mq.MQConnection;
import com.borqs.sync.server.common.mq.MQListener;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.framework.BaseService;
import com.borqs.sync.server.framework.ServiceDescriptor;

public class RedisMQService extends BaseService{
	
	private static final String IDENTIFIER = "RedisMQService_service";
	
	private MQConnection mqConn;
	private MQListener mListener;

	public RedisMQService(Context context) {
		super(context);
	}
	
	public boolean init(ServiceDescriptor descriptor) {
        if( !super.init(descriptor) ){
            return false;
        }

        Object instance = newInstance(String.valueOf(descriptor.impl()));
	    if (instance != null && instance instanceof MQListener) {
	    	mListener = (MQListener)instance;
        }
        return true;
	}
	
	private Object newInstance(String consumerClassName) {
		try {
			return Class.forName(consumerClassName)
                    .getConstructor(new Class[]{Context.class})
                    .newInstance(getContext()) ;
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

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public void stop() {
		mListener.cancel();
		mqConn.disconnect();
	}

	@Override
	protected void runSynchronized(final Context context) {
		Properties redisSettings = context.getConfig().getRedisSettings();
		String topic = redisSettings.getProperty("topic");
		final String[] topics = topic.split(",");
		new Thread("MQServer") {
			@Override
			public void run() {
				
				context.getLogger().info("MQServer" + Thread.currentThread().getId());
				mqConn = MQConnection.getInstance(mContext);
				
				mqConn.doSubscribe(mListener, topics);
				context.getLogger().info("MQServer" + "shutdown");
			}
		}.start();
		
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
	protected String getIdentifier() {
		return IDENTIFIER;
	}
	
}
