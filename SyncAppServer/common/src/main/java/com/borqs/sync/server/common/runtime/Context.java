/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.runtime;

import java.sql.Connection;
import java.util.logging.Logger;

import com.borqs.sync.server.common.notification.MessagePublisherFactory;

/**
 * Date: 9/8/11
 * Time: 6:51 PM
 */
public interface Context {
	public abstract Connection getSqlConnection();
	public abstract Connection getSqlConnection(String dataSource);
    public abstract MessagePublisherFactory getMessagePublisherFactory();
    public abstract ConfigurationBase getConfig();
    @Deprecated
    public abstract Logger getLogger();
    public abstract Logger getLogger(String tag);
    public abstract boolean isDebug();
}
