/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.service;

import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;

import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Date: 5/30/12
 * Time: 3:11 PM
 * Borqs project
 */
public class MockContext implements Context {
    @Override
    public Connection getSqlConnection() {
        return null;
    }

    @Override
    public Connection getSqlConnection(String dataSource) {
        return null;
    }

    @Override
    public MessagePublisherFactory getMessagePublisherFactory() {
        return null;
    }

    @Override
    public ConfigurationBase getConfig() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public Logger getLogger(String tag) {
        return Logger.getLogger("test");
    }

    @Override
    public boolean isDebug() {
        return false;
    }
}
