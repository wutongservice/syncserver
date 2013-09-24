/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;

import java.sql.Connection;
import java.util.logging.Logger;

/**
 * User: b251
 * Date: 1/12/12
 * Time: 11:29 AM
 * Borqs project
 */
public class MockContext implements Context{
    @Override
    public Connection getSqlConnection() {
        return null;  //TODO
    }

    @Override
    public Connection getSqlConnection(String dataSource) {
        return null;  //TODO
    }

    @Override
    public MessagePublisherFactory getMessagePublisherFactory() {
        return null;  //TODO
    }

    @Override
    public ConfigurationBase getConfig() {
        return null;  //TODO
    }

    @Override
    public Logger getLogger() {
        return null;  //TODO
    }

    @Override
    public Logger getLogger(String tag) {
        return null;  //TODO
    }

    @Override
    public boolean isDebug() {
        return false;  //TODO
    }
}
