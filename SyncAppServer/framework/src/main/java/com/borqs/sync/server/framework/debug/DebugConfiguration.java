/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.debug;

import com.borqs.sync.server.common.runtime.ConfigurationBase;

import java.io.InputStream;
import java.util.Properties;

/**
 * Date: 9/26/11
 * Time: 2:00 PM
 */
public class DebugConfiguration implements ConfigurationBase {
    private static final String NAMING_HOST = "127.0.0.1";
    private static final int NAMING_PORT = 9899;
    private boolean mHasJMSConsumer = true;
    private boolean mHasRPCServiceImpl = false;


    @Override
    public int getNamingPort() {
        return NAMING_PORT;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getNamingHost() {
        return NAMING_HOST;
    }

    @Override
    public String getSetting(String settingKey) {
        return "http://apitest.borqs.com";
    }

    @Override
    public Properties getDBSettings() {
        Properties db = new Properties();
        db.setProperty("id","default");
        db.setProperty("driverClassName","com.mysql.jdbc.Driver");
        db.setProperty("url","jdbc:mysql://localhost:3306/borqs_sync_test?characterEncoding=UTF-8");
        db.setProperty("password","root");
        db.setProperty("username","root");
        db.setProperty("maxActive","8");
        db.setProperty("maxIdle","5");
        db.setProperty("minIdle","4");
        db.setProperty("maxWait","1000");
        db.setProperty("removeAbandoned","true");
        db.setProperty("removeAbandonedTimeout","120");
        db.setProperty("testOnBorrow","false");
        db.setProperty("logAbandoned", "false");
        return db;
    }

    @Override
    public Properties getJMSServiceConfig() {
        Properties jms = new Properties();
        if(mHasJMSConsumer){
            //base service
           //jms.setProperty("jms.ptp.consumer.identifier.synccontact.change","com.borqs.sync.contactchange.jms.SyncContactChangeConsumer");
            jms.setProperty("jms.ptp.consumer.identifier.ass.ChangeProfile", "AccountChangeListener");
        }

        //jms auth
        jms.setProperty("username","syncserver");
        jms.setProperty("password", "borqs.com");
        //PEASE change below url for your testing/debug
        jms.setProperty("url", "tcp://localhost:61616");
        return jms;
    }

    @Override
    public String getInstalledServices() {
        return "[\\\n" +
                "  {service:com.borqs.sync.server.services.naming.NamingServic, enable:false, priority:0, desc:naming},\\\n" +
                "  {service:RPCService, enable:true, priority:1, desc:rpc_contact_provider, interface:com.borqs.sync.avro.ISyncDataProvider, impl:SyncDataProviderSkeleton, schema:avro},\\\n" +
                "  {service:RPCService, enable:false, priority:2, desc:rpc_contact_change, interface:com.borqs.sync.avro.contactchange.IContactChangeProvider, impl:ContactChangeServiceImpl, schema:avro},\\\n" +
                "  {service:JMSService, enable:false, priority:3, desc:jms_contact_change, impl:com.borqs.sync.contactchange.jms.SyncContactChangeConsumer},\\\n" +
                "  {service:JMSService, enable:flase, priority:4, desc:jms_acccount_sync, impl:AccountChangeListener}\\\n" +
                "  ]";
    }

    @Override
    public Properties getRPCServiceConfig() {
        Properties rpc = new Properties();
        if(mHasRPCServiceImpl){
            rpc.setProperty("rpc#com.borqs.sync.avro.ISyncDataProvider", "avro://SyncDataProviderSkeleton");
            rpc.setProperty("rpc#com.borqs.sync.avro.contactchange.IContactChangeProvider", "avro://ContactChangeServiceImpl");
        }
        return rpc;
    }

    @Override
    public Properties getHTTPServiceConfig() {
        return null;
    }

    @Override
    public Properties getPushSettings() {
		Properties service = new Properties();
		service.setProperty("appid", "102");
		service.setProperty("address", "http://app1.borqs.com:9090/plugins/xDevice/send");
		return service;
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
		Properties service = new Properties();
		service.setProperty("host", "127.0.0.1");
		service.setProperty("port", "6379");
		service.setProperty("topic", "PlatformHook.onUserCreated,PlatformHook.onUserProfileChanged,PlatformHook.onUserDestroyed,PlatformHook.onFriendshipChange");
		return service;
	}

    @Override
    public InputStream getConfigFile(String fileName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Deprecated
    @Override
    public InputStream getStaticConfigFile(String fileName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
