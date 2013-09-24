/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.jms;

import com.borqs.sync.server.common.notification.MessagePublisher;
import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ContextHolder;

/**
 * Date: 9/8/11 Time: 6:39 PM
 */
public class DefaultJMSPublisherFactory extends ContextHolder implements MessagePublisherFactory {

	public DefaultJMSPublisherFactory(Context context, ConfigurationBase config) {
        super(context);
		init(config);
	}

	public MessagePublisher getPTPProducer(String identifier) {
		return new PTPJMSMessageProducer(getContext(), identifier);
	}

	private void init(ConfigurationBase config) {

	}
}
