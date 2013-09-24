/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.notification;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ContextHolder;

/**
 * Date: 9/8/11
 * Time: 6:12 PM
 */
public abstract class MessageConsumer extends ContextHolder {
    private String mIdentifier;
    protected MessageConsumer(Context context, String identifier){
        super(context);
        mIdentifier = identifier;
    }

    public abstract void runOnMessage(String message);
    public String getIndentifier(){
        return mIdentifier;
    }

}
