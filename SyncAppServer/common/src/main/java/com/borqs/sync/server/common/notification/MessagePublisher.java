package com.borqs.sync.server.common.notification;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ContextHolder;

/**
 * Date: 9/8/11 Time: 6:11 PM
 */
public abstract class MessagePublisher extends ContextHolder{
    protected MessagePublisher(Context context){
        super(context);
    }

    public abstract void close();

    public abstract void send(String message);
}
