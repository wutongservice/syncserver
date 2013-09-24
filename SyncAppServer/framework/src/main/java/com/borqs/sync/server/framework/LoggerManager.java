package com.borqs.sync.server.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.*;

/**
 * Date: 9/30/11
 * Time: 2:11 PM
 */
public class LoggerManager {
    private String mSyncHome;
    private boolean mDebug;
    private int mLoggerLimit;
    private int mLoggerCount;

    public LoggerManager(String home,int loggerLimit,int loggerCount){
        mSyncHome = home;
        mLoggerCount = loggerCount;
        mLoggerLimit = loggerLimit;
    }

    public Logger getLogger(String tag){
        Logger logger = Logger.getLogger(tag);
        Handler[] handlers = logger.getHandlers();

        //we think it is a existent handler, not need to init any more
        if(handlers.length == 0 && mSyncHome != null){
            String logfile = mSyncHome + File.separator + "log" + File.separator + "SA_" + tag + ".log";
            FileHandler fileHandler = null;
            try {
                if(mLoggerCount > 0 && mLoggerLimit > 0){
                    fileHandler = new FileHandler(logfile,mLoggerLimit,mLoggerCount,true);
                }else{
                    fileHandler = new FileHandler(logfile,true);
                }
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
            } catch (IOException e) {
                logger.info("Can not create log file for " + tag);
            }
        }

        return logger;
    }

    public void setDebugLevel(){
       mDebug = true;
    }

}
