package com.borqs.sync.server.framework.services.task;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.task.ITaskListener;
import com.borqs.sync.server.common.task.ITaskManager;
import com.borqs.sync.server.common.task.TimingTask;
import com.borqs.sync.server.common.util.ReflectUtil;
import com.borqs.sync.server.framework.BaseService;
import com.borqs.sync.server.framework.ServiceDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/21/12
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaskService extends BaseService {

    private static final String IDENTIFIER = "task_service";

    private ITaskManager mTaskManager;
    private TimingTask mTask;
    private String mTaskImplName;


    public TaskService(Context context){
        super(context);
    }

    @Override
    public boolean isRunning() {
        return mTaskManager != null && mTaskManager.isRunning();
    }

    @Override
    public void stop() {
        mTaskManager.stopTask();
        mTaskManager = null;
    }

    @Override
    protected void runSynchronized(Context context) {
        if(mTaskManager != null){
            mTaskManager.registerListener(new TaskListener());
            mTaskManager.startTask(mTask,mTaskImplName);

            while(isRunning()){
                synchronized (this){
                    try {
                        wait(3000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    @Override
    protected String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean init(ServiceDescriptor descriptor) {
        if( !super.init(descriptor) ){
            return false;
        }
        mTaskImplName = descriptor.impl();
        try {
            Object instance = ReflectUtil.forName(mTaskImplName).newInstance();
            mTaskManager = (ITaskManager)ReflectUtil.newInstance(descriptor.intf(),getContext());
            if (instance != null && instance instanceof TimingTask) {
                mTask = (TimingTask)instance;
            }
            return true;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    class TaskListener implements ITaskListener {

        @Override
        public void onTaskStart() {
            getContext().getLogger().info("=======profile suggestion task start");
        }

        @Override
        public void onTaskEnd() {
            getContext().getLogger().info("=======profile suggestion task end");
        }

    }

}
