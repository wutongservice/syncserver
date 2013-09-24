package com.borqs.sync.server.common.profilesuggestion;

import com.borqs.sync.server.common.datamining.*;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
* Created by IntelliJ IDEA.
* User: b211
* Date: 5/25/12
* Time: 1:45 PM
* To change this template use File | Settings | File Templates.
*/
public class SuggestionGeneratorTest {

    @Test
    public void testChangeGenerator(){
        String integrationProfile = "{\n" +
                "   \"_id\":{\n" +
                "      \"$id\":\"4fb5b58e5f85c4c605a737ca\"\n" +
                "   },\n" +
                "   \"userId\":[\n" +
                "      \"10043\",\n" +
                "      \"10046\",\n" +
                "      \"17\",\n" +
                "      \"10226\",\n" +
                "      \"10364\",\n" +
                "      \"10362\",\n" +
                "      \"10408\",\n" +
                "      \"10222\",\n" +
                "      \"10425\",\n" +
                "      \"10014\",\n" +
                "      \"10015\",\n" +
                "      \"10392\",\n" +
                "      \"10016\",\n" +
                "      \"10010\",\n" +
                "      \"199\",\n" +
                "      \"10012\",\n" +
                "      \"10178\",\n" +
                "      \"10040\",\n" +
                "      \"10041\",\n" +
                "      \"10056\",\n" +
                "      \"10259\",\n" +
                "      \"10055\",\n" +
                "      \"10033\",\n" +
                "      \"10255\",\n" +
                "      \"230\",\n" +
                "      \"10212\",\n" +
                "      \"10358\",\n" +
                "      \"10027\",\n" +
                "      \"10001\",\n" +
                "      \"10498\",\n" +
                "      \"10025\",\n" +
                "      \"10439\",\n" +
                "      \"10000\",\n" +
                "      \"10005\",\n" +
                "      \"10006\",\n" +
                "      \"5\",\n" +
                "      \"10288\",\n" +
                "      \"10004\",\n" +
                "      \"10009\",\n" +
                "      \"10008\",\n" +
                "      \"233\",\n" +
                "      \"235\"\n" +
                "   ],\n" +
                "   \"borqsId\":\"b10010\",\n" +
                "\"allnames\":[{\"count\":1,\"firstName\":\"啊啊啊\",\"bfirstName\":\"余雪亭二\"}," +
                "{\"count\":1,\"firstName\":\"雪亭二\",\"lastName\":\"余\",\"bfirstName\":\"雪亭二\",\"blastName\":\"余\"}," +
                "{\"count\":5,\"firstName\":\"余雪亭二\",\"bfirstName\":\"余雪亭三\"}," +
                "{\"count\":5,\"firstName\":\"余雪亭一\",\"bfirstName\":\"余雪亭三\"}," +
                "{\"count\":51,\"firstName\":\"亭二\",\"middleName\":\"雪\",\"lastName\":\"余\",\"bfirstName\":\"亭一\",\"bmiddleName\":\"雪\",\"blastName\":\"余\"}],\n" +
                "   \"nickName\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"birthday\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"anniversary\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"hobbies\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"title\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"assistant\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"company\":{\n" +
                "      \"播思\":47\n" +
                "   },\n" +
                "   \"department\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"jobTitle\":{\n" +
                "      \"资深软件工程师\":3\n" +
                "   },\n" +
                "   \"officeLocation\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"profession\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"gender\":{\n" +
                "      \"m\":42\n" +
                "   },\n" +
                "   \"lastUpdate\":1337308559223,\n" +
                "   \"phones\":[\n" +
                "      {\n" +
                "         \"type\":3,\n" +
                "         \"value\":\"+8613910912321\",\n" +
                "         \"count\":47,\n" +
                "         \"private\":1,\n" +
                "         \"lastUpdate\":12321\n" +
                "      }\n" +
                "   ],\n" +
                "   \"mails\":[\n" +
                "      {\n" +
                "         \"type\":4,\n" +
                "         \"value\":\"xuetong.chen@borqs.com\",\n" +
                "         \"count\":48,\n" +
                "         \"private\":0,\n" +
                "         \"lastUpdate\":0\n" +
                "      },\n" +
                "      {\n" +
                "         \"type\":16,\n" +
                "         \"value\":\"chenxt.borqs@gmail.com\",\n" +
                "         \"count\":48,\n" +
                "         \"private\":0,\n" +
                "         \"lastUpdate\":0\n" +
                "      },\n" +
                "      {\n" +
                "         \"type\":23,\n" +
                "         \"value\":\"xuetong3.chen@borqs.com\",\n" +
                "         \"count\":1,\n" +
                "         \"private\":1,\n" +
                "         \"lastUpdate\":1337244550778\n" +
                "      },\n" +
                "      {\n" +
                "         \"type\":16,\n" +
                "         \"value\":\"chenxt.cn@g.c\",\n" +
                "         \"count\":1,\n" +
                "         \"private\":1,\n" +
                "         \"lastUpdate\":123\n" +
                "      },\n" +
                "      {\n" +
                "         \"type\":23,\n" +
                "         \"value\":\"xuetongchen@yahoo.com\",\n" +
                "         \"count\":1,\n" +
                "         \"private\":0,\n" +
                "         \"lastUpdate\":0\n" +
                "      },\n" +
                "      {\n" +
                "         \"type\":23,\n" +
                "         \"value\":\"xuetong5.chen@borqs.com\",\n" +
                "         \"count\":1,\n" +
                "         \"private\":1,\n" +
                "         \"lastUpdate\":1337244550778\n" +
                "      }\n" +
                "   ],\n" +
                "   \"ims\":[\n" +
                "\n" +
                "   ],\n" +
                "   \"addresses\":[{\"type\":3,\"street\":\"天通苑\",\"city\":\"\",\"state\":\"\",\"postcode\":\"\",\"pobox\":\"\",\"extAddr\":\"\",\"country\":\"\",\"count\":1,\"private\":1,\"lastUpdate\":123}]\n" +
                "}";
//        ProfileSuggestionGenerator generator = new ProfileSuggestionGenerator(new MockContext());
//        generator.generateChangeRequest("10040");

        Context context = new MockContext();
        ContactDataMiningAdapter adapter = new ContactDataMiningAdapter(context);
        StringReader sr  = new StringReader(integrationProfile);
        try {
            JsonReader jsonReader = new JsonReader(sr);
            jsonReader.setLenient(true);
            //test parse integration profile
            IntegrationProfileOperation operation = adapter.parseJsonReader(jsonReader);
            IntegrationProfile profile = operation.getProfile();
            List<IntegrationProfileName> parsedNames = profile.getNames();
//            assertEquals("播思", contact.getCompany());
//            assertEquals("资深软件工程师",contact.getJobTitle());

            //name
            List<String> names = new ArrayList<String>();
            for(IntegrationProfileName nameItem:parsedNames){
                names.add(generateDisplayName(nameItem.getFirstName(), nameItem.getMiddleName(), nameItem.getLastName()));
            }
            assertEquals(5,names.size());
            List<String> realNames = new ArrayList<String>();
            realNames.add("余雪亭二");
            realNames.add("余雪亭一");
            realNames.add("啊啊啊");
            for(String name:realNames){
                assertTrue(names.contains(name));
            }

            //email
            List<String> emails = new ArrayList<String>();
            for(IntegrationProfileItem emailItem:profile.getEmails()){
                emails.add(emailItem.getValue());
            }
            assertEquals(6,emails.size());
            List<String> realEmails = new ArrayList<String>();
            realEmails.add("xuetongchen@yahoo.com");
            realEmails.add("xuetong5.chen@borqs.com");
            realEmails.add("chenxt.cn@g.c");
            realEmails.add("xuetong3.chen@borqs.com");
            realEmails.add("chenxt.borqs@gmail.com");
            realEmails.add("xuetong.chen@borqs.com");
            for(String email:realEmails){
                assertTrue(emails.contains(email));
            }
            //phone
            List<String> phones = new ArrayList<String>();
            for(IntegrationProfileItem phoneItem:profile.getPhones()){
                phones.add(phoneItem.getValue());
            }
            assertEquals(1,phones.size());
            List<String> realPhones = new ArrayList<String>();
            realPhones.add("+8613910912321");
            for(String phone:realPhones){
                assertTrue(phones.contains(phone));
            }

            //address
            List<String> addresses = new ArrayList<String>();
            for(IntegrationProfileAddress address:profile.getAddresses()){
                addresses.add(address.getStreet());
            }
            assertEquals(1,addresses.size());
            List<String> realAddress = new ArrayList<String>();
            realAddress.add("天通苑");
            for(String addr:realAddress){
                assertTrue(addresses.contains(addr));
            }

            Contact account = new Contact();
            account.setBFirstName("亭一");
            account.setBMiddleName("雪");
            account.setBLastName("余");

//            account.addTelephone(new ContactItem("13910912321", 3,false,123));

            account.addEmail(new ContactItem("xuetongchen@yahoo.com", 23, false, 123));
            account.addEmail(new ContactItem("xuetong5.chen@borqs.com", 23,false,123));
            account.addEmail(new ContactItem("chenxt1.cn@gmail.com", 16,false,123));
            account.addEmail(new ContactItem("xuetong3.chen@borqs.com", 23,false,123));
            account.addEmail(new ContactItem("chenxt.borqs@gmail.com", 3,false,123));
            account.addEmail(new ContactItem("xuetong.chen@borqs.com", 4,false,123));

            //test profile suggestion generator
            ProfileSuggestionGenerator generator = new ProfileSuggestionGenerator(context);
            generator.setLogger(context.getLogger());
            String chanreququest = generator.collectChangeRequest(profile,account).compose();
            context.getLogger().info(chanreququest);
            ProfileSuggestionParser changeStruct = ProfileSuggestionParser.parse(chanreququest);

            List<IntegrationProfileName> newNames = changeStruct.getNewNames();
            assertEquals(2,newNames.size());
            assertEquals("余雪亭二",generateDisplayName(newNames.get(0).getFirstName(),newNames.get(0).getMiddleName(),newNames.get(0).getLastName()));
            assertEquals("啊啊啊",generateDisplayName(newNames.get(1).getFirstName(),newNames.get(1).getMiddleName(),newNames.get(1).getLastName()));

            assertEquals(0,changeStruct.getNewEmails().size());
//            assertEquals("chenxt.cn@g.c",changeStruct.getNewEmails().get(0));
            assertEquals(1,changeStruct.getNewAddresses().size());
            assertEquals(1,changeStruct.getNewPhones().size());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  //  @Test
   // public void testParseIntegrationProfile(){
   //     Context context = new MockContext();
    //    ProfileSuggestionGenerator generator = new ProfileSuggestionGenerator(context);
    //    generator.setLogger(context.getLogger());
    //    String changeRequest = generator.generateChangeRequest("10040");
    //    ProfileSuggestionParser parser = ProfileSuggestionParser.parse(changeRequest);
    //    assertTrue(parser.getNewNames().size() == 0);
    //    assertTrue(parser.getNewEmails().size() == 0);
    //    assertTrue(parser.getNewPhones().size() == 0);

    //}

    private String generateDisplayName(String firstName,String middleName,String lastName){
        String contactFirstName = firstName == null ? "" : firstName.trim();
        String contactMiddleName = middleName == null ? "" : middleName.trim();
        String contactLastName = lastName == null ? "" : lastName.trim();
        String displayName = contactLastName+contactMiddleName+contactFirstName;
        return displayName;
    }
    
//    @Test
//    public void testTransaction() throws SQLException {
//        Context context = new MockContext();
//        Connection conn = context.getSqlConnection();
//        PreparedStatement ps1 = null;
//        ResultSet rs = null;
//        long size = 0;
//        try {
//            conn.setAutoCommit(false);
//
//            ps1 =  conn.prepareStatement("select count(*) from results");
//            rs = ps1.executeQuery();
//            if(rs.next()){
//                size = rs.getLong(1);
//            }
//            DBUtility.close(null,ps1,rs);
//
//            ps1 = conn.prepareStatement("insert into student (name) values ('yuxuetings')",Statement.RETURN_GENERATED_KEYS);
//            ps1.executeUpdate();
//            rs = ps1.getGeneratedKeys();
//            long studentId = 0;
//            if(rs.next()){
//                studentId = rs.getLong(1);
//                context.getLogger().info("student_id:" + studentId);
//            }
//            DBUtility.close(null,ps1,rs);
//
//
//            //add wrong column
//            ps1  = conn.prepareStatement("insert into results (student_id,result) values (?,'555')");
//            ps1.setLong(1,studentId);
//            ps1.executeUpdate();
//            DBUtility.close(null,ps1,null);
//
//            conn.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            conn.rollback();
//            conn.setAutoCommit(true);
//        }finally {
//
////            ps1 =  conn.prepareStatement("select count(*) from results");
////            rs = ps1.executeQuery();
////            if(rs.next()){
////                assertEquals(size,rs.getLong(1));
////            }
////            DBUtility.close(null,ps1,rs);
//
//            DBUtility.close(conn,null,null);
//        }
//    }

//    @Test
//    public void testContactProviderTransaction(){
//        Context context = new MockContext();
//        ContactProvider provider = new ContactProvider(context);
//        provider.useLogger(context.getLogger());
//
//        long updateTime = System.currentTimeMillis();
//
//        Contact c = new Contact();
//        c.setOwnerId("224");
//        c.setFirstName("first_name5");
//        c.setMiddleName("middle_name5");
//        c.setLastName("last_name5");
//        c.setLastUpdate(updateTime);
//        c.addTelephone(new ContactItem("1008688985",ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER,true,updateTime));
//        c.addEmail(new ContactItem("email1@email.com",ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS,true,updateTime));
//        long contactId = provider.insertItem(c);
//
//        c = new Contact();
//        c.setOwnerId("224");
//        c.setFirstName("first_name6");
//        c.setMiddleName("middle_name6");
//        c.setLastName("last_name6");
//        c.setLastUpdate(updateTime);
//        c.addTelephone(new ContactItem("1008688987",ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER,true,updateTime));
//        c.addEmail(new ContactItem("email4@email.com",ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS,true,updateTime));
//        boolean updated = provider.updateItem(contactId,c);
//        assertTrue(updated);
//
//        Contact newContact = provider.getItem(String.valueOf(contactId));
//        assertEquals(newContact.getFirstName(),c.getFirstName());
//        assertEquals(newContact.getMiddleName(),c.getMiddleName());
//        assertEquals(newContact.getLastName(),c.getLastName());
//
//    }

//    @Test
//    public void testDeleteBorqsFriend(){
//        Context context = new MockContext();
//        ContactProvider provider = new ContactProvider(context);
//        provider.useLogger(context.getLogger());
//        long updateTime = System.currentTimeMillis();
//
//        assertTrue(provider.updateItemAsPrivate(2333,updateTime));
//    }
    
//    @Test
//    public void testIntegration(){
//        Context context = new MockContext();
////        RealTimeIntegrationOperation operation = new RealTimeIntegrationOperation(context);
////        operation.setLogger(context.getLogger());
////        IntegrationProfile profile = operation.generateIntegrationProfile("224");
//        ProfileSuggestionGenerator generator = new ProfileSuggestionGenerator(context);
//        generator.setLogger(context.getLogger());
//        System.out.print(generator.generateChangeRequest("224"));
//
//    }

    @Test
    public void testTime(){
        System.out.print(System.currentTimeMillis());
    }

        class MockContext implements Context {

        @Override
        public Connection getSqlConnection() {
            return getConnection();
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
//            String sync_app_home = System.getProperty("sync.app.home");
//            File configFile = new File(sync_app_home + File.separator + CONFIG_BASE_FILE);


            return new DebugConfiguration();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Logger getLogger() {
            return Logger.getLogger("");  //To change body of implemented methods use File | Settings | File Templates.
        }

         @Override
         public Logger getLogger(String tag) {
             return Logger.getLogger(tag);  //To change body of implemented methods use File | Settings | File Templates.
         }

         @Override
        public boolean isDebug() {
            return true;
        }
    }

     private Connection getConnection(){
        Properties mDbProperties = new Properties();
        mDbProperties.setProperty("driverClassName","com.mysql.jdbc.Driver");
        mDbProperties.setProperty("url","jdbc:mysql://192.168.5.208:3306/borqs_sync?characterEncoding=UTF-8");
        mDbProperties.setProperty("password","borqs_sync");
        mDbProperties.setProperty("username","borqs_sync");
        mDbProperties.setProperty("maxActive","8");
        mDbProperties.setProperty("maxIdle","5");
        mDbProperties.setProperty("minIdle","4");
        mDbProperties.setProperty("maxWait","1000");
        mDbProperties.setProperty("removeAbandoned","true");
        mDbProperties.setProperty("removeAbandonedTimeout","120");
        mDbProperties.setProperty("testOnBorrow","false");
        mDbProperties.setProperty("logAbandoned", "false");
        try {
            DataSource dataSource = BasicDataSourceFactory.createDataSource(mDbProperties);
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
        private class DebugConfiguration implements ConfigurationBase {
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

        public Properties getDBSettings() {
            Properties db = new Properties();
            db.setProperty("id","default");
            db.setProperty("driverClassName","com.mysql.jdbc.Driver");
            db.setProperty("url","jdbc:mysql://localhost:3306/borqs_sync?characterEncoding=UTF-8");
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
				// TODO Auto-generated method stub
				return null;
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

}
