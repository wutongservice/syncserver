/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Date: 3/27/12
 * Time: 11:57 AM
 * Borqs project
 */
public final class LogHelper {
    public static void logD(Logger logger, String msg){
        if(logger!=null){
            logger.log(Level.ALL, msg);
        }
    }

    public static void logW(Logger logger, String msg){
        if(logger!=null){
            logger.log(Level.WARNING, msg);
        }
    }

    public static void logInfo(Logger logger, String msg){
        if(logger != null){
            logger.info(msg);
        }
    }
}
