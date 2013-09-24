//package com.borqs.sync.server.common.sync;
//
//import com.borqs.sync.server.common.notification.MessagePublisherFactory;
//import com.borqs.sync.server.common.runtime.ConfigurationBase;
//import com.borqs.sync.server.common.runtime.Context;
//import org.apache.commons.dbcp.BasicDataSourceFactory;
//import org.junit.Test;
//import static junit.framework.Assert.*;
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.util.Properties;
//import java.util.logging.Logger;
//
///**
//* Created by IntelliJ IDEA.
//* User: b211
//* Date: 7/4/12
//* Time: 6:01 PM
//* To change this template use File | Settings | File Templates.
//*/
//public class SyncProviderTest {
//
//    @Test
//    public void testIsSyncing(){
//        Context context = new MockContext();
//        SyncProvider syncProvider = new SyncProvider(context);
//        syncProvider.setLogger(context.getLogger());
//        syncProvider.enterSyncBeginStatus("224", "IMEI:001022000282973");
//
//        assertFalse(syncProvider.isSyncing("224", "IMEI:001022000282973"));
//        assertTrue(syncProvider.isSyncing("224", "IMEI:001022000282974"));
//
//        syncProvider.enterSyncEndStatus("224");
//        assertFalse(syncProvider.isSyncing("224", "IMEI:001022000282973"));
//    }
//
//    class MockContext implements Context {
//
//        @Override
//        public Connection getSqlConnection() {
//            return getConnection();
//        }
//
//        @Override
//        public Connection getSqlConnection(String dataSource) {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public MessagePublisherFactory getMessagePublisherFactory() {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public ConfigurationBase getConfig() {
////            String sync_app_home = System.getProperty("sync.app.home");
////            File configFile = new File(sync_app_home + File.separator + CONFIG_BASE_FILE);
//
//
//            return new DebugConfiguration();  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public Logger getLogger() {
//            return Logger.getLogger("");  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public Logger getLogger(String tag) {
//            return Logger.getLogger(tag);  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public boolean isDebug() {
//            return true;
//        }
//    }
//
//    private Connection getConnection(){
//        Properties mDbProperties = new Properties();
//        mDbProperties.setProperty("driverClassName","com.mysql.jdbc.Driver");
//        mDbProperties.setProperty("url","jdbc:mysql://localhost:3306/syncservice?characterEncoding=UTF-8");
//        mDbProperties.setProperty("password","root");
//        mDbProperties.setProperty("username","root");
//        mDbProperties.setProperty("maxActive","8");
//        mDbProperties.setProperty("maxIdle","5");
//        mDbProperties.setProperty("minIdle","4");
//        mDbProperties.setProperty("maxWait","1000");
//        mDbProperties.setProperty("removeAbandoned","true");
//        mDbProperties.setProperty("removeAbandonedTimeout","120");
//        mDbProperties.setProperty("testOnBorrow","false");
//        mDbProperties.setProperty("logAbandoned", "false");
//        try {
//            DataSource dataSource = BasicDataSourceFactory.createDataSource(mDbProperties);
//            return dataSource.getConnection();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    private class DebugConfiguration implements ConfigurationBase {
//        private static final String NAMING_HOST = "127.0.0.1";
//        private static final int NAMING_PORT = 9899;
//        private boolean mHasJMSConsumer = true;
//        private boolean mHasRPCServiceImpl = false;
//
//
//        @Override
//        public int getNamingPort() {
//            return NAMING_PORT;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public String getNamingHost() {
//            return NAMING_HOST;
//        }
//
//        @Override
//        public String getSetting(String settingKey) {
//            return "http://apitest.borqs.com";
//        }
//
//        public Properties getDBSettings() {
//            Properties db = new Properties();
//            db.setProperty("id","default");
//            db.setProperty("driverClassName","com.mysql.jdbc.Driver");
//            db.setProperty("url","jdbc:mysql://localhost:3306/borqs_sync?characterEncoding=UTF-8");
//            db.setProperty("password","root");
//            db.setProperty("username","root");
//            db.setProperty("maxActive","8");
//            db.setProperty("maxIdle","5");
//            db.setProperty("minIdle","4");
//            db.setProperty("maxWait","1000");
//            db.setProperty("removeAbandoned","true");
//            db.setProperty("removeAbandonedTimeout","120");
//            db.setProperty("testOnBorrow","false");
//            db.setProperty("logAbandoned", "false");
//            return db;
//        }
//
//        @Override
//        public Properties getJMSServiceConfig() {
//            Properties jms = new Properties();
//            if(mHasJMSConsumer){
//                //base service
//                //jms.setProperty("jms.ptp.consumer.identifier.synccontact.change","com.borqs.sync.contactchange.jms.SyncContactChangeConsumer");
//                jms.setProperty("jms.ptp.consumer.identifier.ass.ChangeProfile", "AccountChangeListener");
//            }
//
//            //jms auth
//            jms.setProperty("username","syncserver");
//            jms.setProperty("password", "borqs.com");
//            //PEASE change below url for your testing/debug
//            jms.setProperty("url", "tcp://localhost:61616");
//            return jms;
//        }
//
//        @Override
//        public String getInstalledServices() {
//            return "[\\\n" +
//                    "  {service:com.borqs.sync.server.services.naming.NamingServic, enable:false, priority:0, desc:naming},\\\n" +
//                    "  {service:RPCService, enable:true, priority:1, desc:rpc_contact_provider, interface:com.borqs.sync.avro.ISyncDataProvider, impl:SyncDataProviderSkeleton, schema:avro},\\\n" +
//                    "  {service:RPCService, enable:false, priority:2, desc:rpc_contact_change, interface:com.borqs.sync.avro.contactchange.IContactChangeProvider, impl:ContactChangeServiceImpl, schema:avro},\\\n" +
//                    "  {service:JMSService, enable:false, priority:3, desc:jms_contact_change, impl:com.borqs.sync.contactchange.jms.SyncContactChangeConsumer},\\\n" +
//                    "  {service:JMSService, enable:flase, priority:4, desc:jms_acccount_sync, impl:AccountChangeListener}\\\n" +
//                    "  ]";
//        }
//
//        @Override
//        public Properties getRPCServiceConfig() {
//            Properties rpc = new Properties();
//            if(mHasRPCServiceImpl){
//                rpc.setProperty("rpc#com.borqs.sync.avro.ISyncDataProvider", "avro://SyncDataProviderSkeleton");
//                rpc.setProperty("rpc#com.borqs.sync.avro.contactchange.IContactChangeProvider", "avro://ContactChangeServiceImpl");
//            }
//            return rpc;
//        }
//
//        @Override
//        public Properties getHTTPServiceConfig() {
//            return null;
//        }
//
//        @Override
//        public Properties getPushSettings() {
//            Properties service = new Properties();
//            service.setProperty("appid", "102");
//            service.setProperty("address", "http://app1.borqs.com:9090/plugins/xDevice/send");
//            return service;
//        }
//
//        @Override
//        public Properties getWebAgentSettings() {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//
//        @Override
//        public Properties getTaskSettings() {
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//        }
//    }
//}
