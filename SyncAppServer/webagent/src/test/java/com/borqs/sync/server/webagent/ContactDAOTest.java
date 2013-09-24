//package com.borqs.sync.server.webagent;
//
//import com.borqs.sync.server.common.json.JSONException;
//import com.borqs.sync.server.common.json.JsonReader;
//import com.borqs.sync.server.common.notification.MessagePublisherFactory;
//import com.borqs.sync.server.common.profilesuggestion.ProfileSuggestionParser;
//import com.borqs.sync.server.common.runtime.ConfigurationBase;
//import com.borqs.sync.server.common.runtime.Context;
//import com.borqs.sync.server.webagent.dao.ContactDAO;
//import com.borqs.sync.server.webagent.dao.IgnoreItem;
//import org.apache.commons.dbcp.BasicDataSourceFactory;
//import org.junit.Test;
//
//import javax.sql.DataSource;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.sql.Connection;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//import java.util.logging.Logger;
//
//import static junit.framework.Assert.assertTrue;
//
///**
//* Created by IntelliJ IDEA.
//* User: b211
//* Date: 5/25/12
//* Time: 1:45 PM
//* To change this template use File | Settings | File Templates.
//*/
//public class ContactDAOTest {
//
//    @Test
//    public void testIgnoreItem(){
//        MockContext context = new MockContext();
//        ContactDAO contactDAO = new ContactDAO(context);
//        String testStr = "{\"emails\":[\"1037949594@qq.com\"]}";
//        ByteArrayInputStream stream = new ByteArrayInputStream(testStr.getBytes());
//        JsonReader reader = new JsonReader(new InputStreamReader(stream));
//        List<IgnoreItem> items = null;
//        try {
//            items = IgnoreItem.parseIgnoreItem(reader);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(items.size() ==1 );
//        IgnoreItem item = items.get(0);
//        List<String> values = item.getValues();
//        assertTrue(values.size() == 1);
//        List<String> relValues = new ArrayList<String>();
//        relValues.add("1037949594@qq.com");
//
//        for(String value:values){
//            assertTrue(relValues.remove(value));
//        }
//
//        assertTrue(item.getType() == ProfileSuggestionParser.CHANGE_REQUEST_TYPE_EMAIL);
//
//        contactDAO.ignoreItem("10040");
//    }
//
//    @Test
//    public void testComposeIgnore() throws JSONException {
//        IgnoreItem phone = new IgnoreItem();
//        List<String> phones = new ArrayList<String>();
//        phones.add("10086");
//        phones.add("10087");
//        phone.setType(ProfileSuggestionParser.CHANGE_REQUEST_TYPE_PHONE);
//        phone.setValues(phones);
//
//        IgnoreItem email = new IgnoreItem();
//        List<String> emails = new ArrayList<String>();
//        emails.add("email1@email.com");
//        emails.add("email2@email.com");
//        email.setType(ProfileSuggestionParser.CHANGE_REQUEST_TYPE_EMAIL);
//        email.setValues(emails);
//
//        List<IgnoreItem> items = new ArrayList<IgnoreItem>();
//        items.add(phone);
//        items.add(email);
//
//        String itemsStr = IgnoreItem.composeJsonStr(items);
//
//        ByteArrayInputStream stream = new ByteArrayInputStream(itemsStr.getBytes());
//        JsonReader reader = new JsonReader(new InputStreamReader(stream));
//        List<IgnoreItem> parsedItems = null;
//        try {
//            parsedItems = IgnoreItem.parseIgnoreItem(reader);
//            assertTrue(parsedItems.size()==2);
//
//            for(IgnoreItem ignoreItem:parsedItems){
//                if(ignoreItem.getType() == ProfileSuggestionParser.CHANGE_REQUEST_TYPE_PHONE){
//                    List<String> relPhones = new ArrayList<String>();
//                    relPhones.add("10086");
//                    relPhones.add("10087");
//                    for(String parsedPhone:ignoreItem.getValues()){
//                        assertTrue(relPhones.remove(parsedPhone));
//                    }
//                }else if(ignoreItem.getType() == ProfileSuggestionParser.CHANGE_REQUEST_TYPE_EMAIL){
//                    List<String> relEmails = new ArrayList<String>();
//                    relEmails.add("email1@email.com");
//                    relEmails.add("email2@email.com");
//                    for(String parsedEmail:ignoreItem.getValues()){
//                        assertTrue(relEmails.remove(parsedEmail));
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//        class MockContext implements Context {
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
//         @Override
//         public Logger getLogger(String tag) {
//             return Logger.getLogger(tag);  //To change body of implemented methods use File | Settings | File Templates.
//         }
//
//         @Override
//        public boolean isDebug() {
//            return true;
//        }
//    }
//
//     private Connection getConnection(){
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
//        private class DebugConfiguration implements ConfigurationBase {
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
//            @Override
//            public Properties getTaskSettings() {
//                return null;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        }
//
//}
