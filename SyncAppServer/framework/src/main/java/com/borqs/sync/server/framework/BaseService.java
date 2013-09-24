package com.borqs.sync.server.framework;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ReflectibleService;

/**
 * A container service to run a group services
 * Date: 9/8/11
 * Time: 6:59 PM
 */
public abstract class BaseService extends ReflectibleService implements Runnable, Comparable<BaseService>{
    private Thread mThread = null;
    private StatusListener mListener;
    //like "service.naming#1"
    protected ServiceDescriptor mDescriptor;

    public interface StatusListener{
        public abstract void onStart(Context context);
        public abstract void onStop(Context context);
    }

    public BaseService(Context context){
        super(context);
        mThread = new Thread(this);
    }
    /**
     * get the description string of the service
     * @return  null if no description
     */
    public String getDescriptor(){
        return mDescriptor.desc();
    }

    /**
     * get the priority while launching, the value is set in config file
     * @return string to compare the priority
     */
    public String getPriority(){
        return mDescriptor.priority();
    }

    /**
     * run the service with a listener
     * @param listener
     */
    public void start(StatusListener listener){
        mListener = listener;
        mThread.setName(getIdentifier());
        mThread.start();

        //waiting for ms for the service ready
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * check if the service is enabled to launch or not
     * @return
     */
    public boolean isEnabled(){
        return true;
    }

    /**
     * controller of the server implementation
     */
    @Override
    public void run() {
        if(mListener != null){
           mListener.onStart(mContext);
        }
        getContext().getLogger().info("Server:" + getDescriptor() +" start up!");
        runSynchronized(mContext);
        getContext().getLogger().info("Server:" + getDescriptor() +" end!");
        if(mListener != null){
            mListener.onStop(mContext);
        }
    }

    /**
     * join with service, to wait it finish
     */
    public void join(){
        try{
            mThread.join();
        } catch (InterruptedException e) {
             e.printStackTrace();
        }
    }

    /**
     * compare two service if they are same one
     * @param baseService
     * @return
     */
    @Override
    public int compareTo(BaseService baseService) {
        return getPriority().compareTo(baseService.getPriority());
    }

    /**
     * call this to init service before start
     * @param descriptor
     * @return
     */
    public boolean init(ServiceDescriptor descriptor){
        mDescriptor = descriptor;
        return mDescriptor!=null;
    }

    /**
     * check if the service is running or not
     * @return
     */
    public abstract boolean isRunning();

    /**
     * stop the service
     */
    public abstract void stop();

    /**
     * implement to make the service is available
     * @param context
     */
    protected abstract void runSynchronized(Context context);

    /**
     * the ID of the service
     * @return
     */
    protected abstract String getIdentifier();
}
