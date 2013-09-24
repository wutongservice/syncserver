package com.borqs.sync.server.common.task;

import org.quartz.Job;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/25/12
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ITaskManager {

    public void startTask(Job task,String taskImplName);
    public boolean isRunning();
    public void stopTask();
    public void registerListener(ITaskListener taskListener);


}
