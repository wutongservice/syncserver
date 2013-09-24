/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.notification;

import com.borqs.sync.server.common.runtime.Context;

/**
 * Date: 9/8/11
 * Time: 6:27 PM
 */
public abstract class ActionResponser extends MessageConsumer{

    protected ActionResponser(Context context, String id){
        super(context, id);
    }
    @Override
    public void runOnMessage(String message) {
        ActionMessageParser parser = new ActionMessageParser(message);
        handle(parser.parseAction(), parser.parseData());
    }

    protected abstract void handle(String action, String data);
}
