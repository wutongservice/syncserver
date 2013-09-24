/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.sql.SqlConnectionFactory;

/**
 * Date: 9/8/11
 * Time: 6:56 PM
 */
public class AppContext implements Context{
    private MessagePublisherFactory mMessageHandlerManager;
    private ConfigurationBase mConfig;
    private LoggerManager mLogger;
    private AccountManager accountManager;
    private boolean mIsDebugEnabled;
    
    private Map<String, SqlConnectionFactory> sqlConnectionFactories;
	
    private static final String DATASOURCE_ID_DEFAULT = "default";
    
	@Override
	public Connection getSqlConnection() {
		if(sqlConnectionFactories == null) {
			return null;
		}
		
		SqlConnectionFactory scf = sqlConnectionFactories.get(DATASOURCE_ID_DEFAULT);
		return scf == null ? null : scf.getConnection();
	}

    @Override
    public Connection getSqlConnection(String dataSource) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessagePublisherFactory getMessagePublisherFactory() {
        return mMessageHandlerManager;
    }

    @Override
    public ConfigurationBase getConfig() {
        return mConfig;
    }

    @Override
    public Logger getLogger() {
        final String LOGGER_NAME = "SyncServer";
        return mLogger.getLogger(LOGGER_NAME);
    }

    @Override
    public Logger getLogger(String tag) {
        return mLogger.getLogger(tag);
    }

    void setAccountManager(AccountManager accountManager) {
    	this.accountManager = accountManager;
    }

    @Override
    public boolean isDebug() {
        return mIsDebugEnabled;
    }

    void setDebug(boolean debugEnabled){
        mIsDebugEnabled = debugEnabled;
    }

    void setMessagePublisherFactory(MessagePublisherFactory mhMgr){
        mMessageHandlerManager = mhMgr;
    }

    void setLogger(LoggerManager loggerManager){
        mLogger = loggerManager;
    }

    void setConfig(ConfigurationBase config){
        mConfig = config;
    }

    void addSqlConnectionFactory(String id, SqlConnectionFactory sqlConnectionFactory) {
		if(sqlConnectionFactories == null) {
			sqlConnectionFactories = new HashMap<String, SqlConnectionFactory>();
		}
		
		sqlConnectionFactories.put(id, sqlConnectionFactory);
	}
}
