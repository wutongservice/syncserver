/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework;

import com.borqs.sync.server.framework.services.jms.DefaultJMSPublisherFactory;
import com.borqs.sync.server.framework.services.jms.DefaultJMSPublisherFactory;
import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.runtime.Context;

/**
 * Date: 9/16/11
 * Time: 2:27 PM
 */
public class DefaultMessagePublisherFactory {

    public static MessagePublisherFactory get(Context context){
        return new DefaultJMSPublisherFactory(context, context.getConfig());
    }
}
