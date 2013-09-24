package com.borqs.sync.server.rpc.service.datasync;

import com.borqs.sync.server.common.runtime.Context;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/28/12
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class RpcServiceLogger {
    
    private static final String TAG = "rpcservice";
    
    public static Logger getLogger(Context context){
        return context.getLogger(TAG);
    }
}
