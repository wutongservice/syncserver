package com.borqs.sync.server.syncml.util;

import com.borqs.sync.server.common.runtime.Context;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 4/1/12
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContactLogger {
    private static final String TAG = "contact";

    public static Logger getLogger(Context context){
        return context.getLogger(TAG);
    }

}
