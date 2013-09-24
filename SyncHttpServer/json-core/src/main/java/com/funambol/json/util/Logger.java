package com.funambol.json.util;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/27/12
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Logger {
    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger("json-connector");

    public static void info(String ...msg){
        for(String str:msg){
            log.info(str);
        }
    }

}
