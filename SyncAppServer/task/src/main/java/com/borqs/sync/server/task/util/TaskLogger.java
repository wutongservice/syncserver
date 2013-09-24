package com.borqs.sync.server.task.util;

import com.borqs.sync.server.common.runtime.Context;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/24/12
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskLogger {

    private final static String TAG = "task";
    private Logger mLogger;
    private static TaskLogger sLog;

    public static synchronized TaskLogger getInstnace(Context context){
        if(sLog == null){
            sLog = new TaskLogger(context);
        }
        return sLog;
    }

    public Logger getLogger(){
        return mLogger;
    }


    public void info(String msg){
        if(mLogger!=null){
            mLogger.log(Level.INFO, msg);
        }
    }

    public void error(String msg){
        if(mLogger!=null){
            mLogger.log(Level.WARNING, msg);
        }
    }

    private TaskLogger(Context context){
        mLogger = context.getLogger(TAG);
    }
}
