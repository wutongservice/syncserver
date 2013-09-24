/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework;

import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Date: 9/21/11
 * Time: 5:35 PM
 */
public class MockContext implements Context {
    @Override
    public Connection getSqlConnection() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Connection getSqlConnection(String dataSource) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessagePublisherFactory getMessagePublisherFactory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ConfigurationBase getConfig() {
        return new MockConfig();
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("test");
    }

    @Override
    public Logger getLogger(String tag) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isDebug() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private class MockConfig implements ConfigurationBase {

        @Override
        public int getNamingPort() {
            return 10001;
        }

        @Override
        public String getNamingHost() {
            return "localhost";
        }

        @Override
        public String getSetting(String settingKey) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getDBSettings() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getJMSServiceConfig() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getInstalledServices() {
            return "[{service:RPCService, enable:true, priority:1, desc:rpc_contact_provider, interface:com.borqs.sync.avro.ISyncDataProvider, impl:SyncDataProviderSkeleton, schema:avro}]";
        }

        @Override
        public Properties getRPCServiceConfig() {
            Properties p = new Properties();
            p.setProperty("rpc#com.borqs.sync.avro.ISyncDataProvider", "avro://SyncDataProviderSkeleton");
            return p;
        }

        @Override
        public Properties getHTTPServiceConfig() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getPushSettings() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
        
        @Override
        public Properties getWebAgentSettings() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getTaskSettings() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

		@Override
		public Properties getRedisSettings() {
			// TODO Auto-generated method stub
			return null;
		}

        @Deprecated
        @Override
        public InputStream getConfigFile(String fileName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public InputStream getStaticConfigFile(String fileName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
