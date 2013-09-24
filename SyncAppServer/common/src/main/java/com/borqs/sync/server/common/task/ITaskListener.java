package com.borqs.sync.server.common.task;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/31/12
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITaskListener {
    public void onTaskStart();
    public void onTaskEnd();
}
