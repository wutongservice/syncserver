/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync;

import com.borqs.sync.server.common.runtime.Context;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date: 2/23/12
 * Time: 5:22 PM
 * Borqs project
 */
public class DSLog {
    private final static String TAG = "Borqs_Contact_sync";
    private Logger mLogger;
    private static DSLog sLog;

    public static synchronized DSLog getInstnace(Context context){
        if(sLog == null){
            sLog = new DSLog(context);
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
    
    public void error(String msg,Throwable e){
        if(mLogger!=null){
            mLogger.log(Level.WARNING, msg,e);
        }
    }
    
    private DSLog(Context context){
        mLogger = context.getLogger(TAG);
    }
}
