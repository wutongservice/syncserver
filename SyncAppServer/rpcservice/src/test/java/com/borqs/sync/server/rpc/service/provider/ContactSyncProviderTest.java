/*
* Copyright (C) 2007-2012 Borqs Ltd.
*  All rights reserved.
*/
package com.borqs.sync.server.rpc.service.provider;

import com.borqs.pim.jcontact.*;
import com.borqs.sync.avro.IContactSyncMLProvider;
import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.NotFoundException;
import com.borqs.sync.server.rpc.service.datasync.syncML.ConactSyncProvider;
import com.borqs.sync.server.rpc.service.datasync.syncML.Protocols;
import com.borqs.sync.server.syncml.converter.JContactConverter;
import junit.framework.Assert;
import org.apache.avro.AvroRemoteException;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
*  test for ContactProvider
*/
public final class ContactSyncProviderTest {

//    @Test
//    public void testAddItem(){
//        try {
//            long currentTime = System.currentTimeMillis();
//            IContactSyncMLProvider contactSyncMLProvider = createAccountSyncService();
//            XResponse response = contactSyncMLProvider.addItem("10215",createContactJson(currentTime),currentTime);
//            Assert.assertNotNull(response.content);
//            long key = parseAddResponse(response.content.toString());
//            Assert.assertTrue(key > 0);
//
//            response = contactSyncMLProvider.getItem("10215",String.valueOf(key));
//            String contactJson = response.content.toString();
//
//            JContact jContactReaded = JContact.fromJsonString(contactJson);
//
//            JContact provided = JContact.fromJsonString(createContactJson(currentTime));
//
//            checkContact(provided,jContactReaded);
//
//        } catch (RPCException e) {
//            e.printStackTrace();
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (AvroRemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
    @Test
    public void testUpdateItem() {
//        try {
//            long currentTime = System.currentTimeMillis();
//            IContactSyncMLProvider contactSyncMLProvider = createAccountSyncService();
////            XResponse response = contactSyncMLProvider.addItem("14203", createContactJson(currentTime), currentTime);
////            Assert.assertNotNull(response.content);
////            long key = parseAddResponse(response.content.toString());
////            Assert.assertTrue(key > 0);
//            long updatedTime = System.currentTimeMillis();
//            XResponse response = contactSyncMLProvider.updateItem("14203",String.valueOf(10877),createContactJson(updatedTime),updatedTime);
//            Assert.assertTrue(response.status_code == Protocols.StatusCode.OK);
//
//            response = contactSyncMLProvider.getItem("14203",String.valueOf(10877));
//            String contactJson = response.content.toString();
//
//            JContact jContactReaded = JContact.fromJsonString(contactJson);
//            Assert.assertEquals(String.valueOf(updatedTime),jContactReaded.getFirstName());
//        } catch (RPCException e) {
//            e.printStackTrace();
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (AvroRemoteException e) {
//            e.printStackTrace();
//        }

    }
//
//    @Test
//    public void testGetItem(){
//        try {
//            IContactSyncMLProvider contactSyncMLProvider = createAccountSyncService();
//            XResponse response = contactSyncMLProvider.getItem("10215","328");
//            System.out.println(response.content.toString());
//            Contact contact = JContactConverter.toContact(response.content.toString());
//            Assert.assertEquals(3,contact.getEmails().size());
//        } catch (RPCException e) {
//            e.printStackTrace();
//        } catch (NotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (AvroRemoteException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void checkContact(JContact provided,JContact readed){
//        Assert.assertEquals(provided.getFirstName(), readed.getFirstName());
//        Assert.assertEquals(provided.getMiddleName(), readed.getMiddleName());
//        Assert.assertEquals(provided.getLastName(), readed.getLastName());
//        Assert.assertEquals(provided.getBirthday(), readed.getBirthday());
//        Assert.assertEquals(provided.getFirstNamePinyin(), readed.getFirstNamePinyin());
//        Assert.assertEquals(provided.getMiddleNamePinyin(), readed.getMiddleNamePinyin());
//        Assert.assertEquals(provided.getLastNamePinyin(), readed.getLastNamePinyin());
//        Assert.assertEquals(provided.getNamePrefix(), readed.getNamePrefix());
//        Assert.assertEquals(provided.getNamePostfix(), readed.getNamePostfix());
//        Assert.assertEquals(provided.getNickName(), readed.getNickName());
//        Assert.assertEquals(provided.getNote(), readed.getNote());
//        Assert.assertEquals(provided.getPhoto(), readed.getPhoto());
//
//        compareList(provided.getAddressList(),readed.getAddressList());
//
//        compareList(provided.getEmailList(),readed.getEmailList());
//
//        compareList(provided.getIMList(),readed.getIMList());
//
//        compareList(provided.getOrgList(),readed.getOrgList());
//
//        comparePhoneList(provided.getPhoneList(),readed.getPhoneList());
//
//        compareWebList(provided.getWebpageList(),readed.getWebpageList());
//
//        compareList(provided.getXTags(),readed.getXTags());
//    }
//
//    private void comparePhoneList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
//        if(providerdList == null){
//            Assert.assertNull(readedList);
//        }else{
//            Assert.assertNotNull(readedList);
//            Assert.assertEquals(providerdList.size(), readedList.size());
//            for(JContact.TypedEntity provided:providerdList){
//                String type = provided.getType();
//                boolean found = false;
//                for(JContact.TypedEntity readed:readedList){
//                    if(String.valueOf(provided.getValue()).equals(String.valueOf(readed.getValue()))){
//                        found = true;
//                    }
//                }
//                Assert.assertTrue(found);
//            }
//        }
//    }
//    private void compareList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
//        if(providerdList == null){
//            Assert.assertNull(readedList);
//        }else{
//            Assert.assertNotNull(readedList);
//            Assert.assertEquals(providerdList.size(), readedList.size());
//            for(JContact.TypedEntity provided:providerdList){
//                String type = provided.getType();
//                boolean found = false;
//                for(JContact.TypedEntity readed:readedList){
//                    if(readed.getType().equals(type)){
//                        found = true;
//                        Assert.assertEquals(provided.getValue().toString(), readed.getValue().toString());
//                    }
//                }
//                Assert.assertTrue(found);
//            }
//        }
//    }
//
//    private void compareWebList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
//        if(providerdList == null){
//            Assert.assertNull(readedList);
//        }else{
//            Assert.assertNotNull(readedList);
//            Assert.assertEquals(providerdList.size(), readedList.size());
//            for(JContact.TypedEntity provided:providerdList){
//                String type = provided.getType();
//                boolean found = false;
//                for(JContact.TypedEntity readed:readedList){
//                    //readed:"URL":[{"HOMEPAGE":"homepage-web"},{"HOMEPAGE":"other-web"},{"HOME":"home-web"}],
//                    //provided:"URL":[{"HOME":"home-web"},{"HOMEPAGE":"homepage-web"},{"OTHER":"other-web"}]
//
//                    if(provided.getValue().toString().equals(readed.getValue().toString())){
//                        if(provided.getType().equals(readed.getType()) || readed.getType().equals(JWebpage.HOMEPAGE)){
//                            found  = true;
//                        }
//                    }
//                }
//                Assert.assertTrue(found);
//            }
//        }
//    }
//
////    private void compareAddressList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
////        if(providerdList == null){
////            Assert.assertNull(readedList);
////        }else{
////            Assert.assertNotNull(readedList);
////            Assert.assertEquals(providerdList.size(),readedList.size());
////            for(JContact.TypedEntity provided:providerdList){
////                String type = provided.getType();
////                boolean found = false;
////                for(JContact.TypedEntity readed:readedList){
////                    if(readed.getType().equals(type)){
////                        found = true;
////                        Assert.assertEquals(JAddress.city(provided.getValue()),JAddress.city(readed.getValue()));
////                        Assert.assertEquals(JAddress.province(provided.getValue()),JAddress.province(readed.getValue()));
////                        Assert.assertEquals(JAddress.street(provided.getValue()),JAddress.street(readed.getValue()));
////                        Assert.assertEquals(JAddress.zipcode(provided.getValue()),JAddress.zipcode(readed.getValue()));
////                    }
////                }
////                Assert.assertTrue(found);
////            }
////        }
////    }
//
    private IContactSyncMLProvider createAccountSyncService() throws RPCException, NotFoundException {
//        String naming_host = /*BCSConfig.getConfigString(BCSConfig.NAMING_HOST)*/"127.0.0.1";
//        int naming_port = /*BCSConfig.getConfigInt(BCSConfig.NAMING_PORT)*/8999;
//
//        NamingServiceProxy ns = new NamingServiceProxy(naming_host, naming_port);
//        RemoteService rs = new RemoteService(IContactSyncMLProvider.class,ns);
//        return rs.asInterface();
        return new ConactSyncProvider(new MockContext());
    }
//
//    private long parseAddResponse(String responseContent){
//        //{"data":{"key":"3833883"}}
//        try {
//            JSONObject rootJson = new JSONObject(responseContent);
//            JSONObject dataJson = rootJson.getJSONObject("data");
//            return Long.parseLong(dataJson.getString("key"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
    private String createContactJson(long time){
        JContactBuilder jContactBuilder = new JContactBuilder();
        jContactBuilder.setFirstName(String.valueOf(time),"firstnamepinyin");
        jContactBuilder.setMiddleName("middlename", "middlenamepinyin ");
        jContactBuilder.setLastName("lastname","lastnamepinyin");
        jContactBuilder.setBirthday("2012-03-30");
        jContactBuilder.setNamePostfix("namepostfix");
        jContactBuilder.setNamePrefix("nameprefix");
        jContactBuilder.setNickName("nickname");
        jContactBuilder.setNote("note");
        // type,  street, city,  province,  zipcode
        jContactBuilder.addAddress(JAddress.WORK,"work-street","work-city","work-province","100001");
        jContactBuilder.addAddress(JAddress.HOME,"home-street","home-city","home-province","100002");
        jContactBuilder.addAddress(JAddress.OTHER,"other-street","other-city","other-province","100003");
        // mailType, address, isPrimary
        jContactBuilder.addEmail(JEMail.WORK,"work-email@email.com",true);
        jContactBuilder.addEmail(JEMail.HOME,"home-email@email.com",false);
        jContactBuilder.addEmail(JEMail.OTHER,"other-email@email.com",false);
        //imType,  imvalue
        jContactBuilder.addIM(JIM.AIM,"aim");
        jContactBuilder.addIM(JIM.GOOGLE_TALK,"google_talk");
        jContactBuilder.addIM(JIM.ICQ,"icq");
        jContactBuilder.addIM(JIM.JABBER,"jabber");
        jContactBuilder.addIM(JIM.MSN,"msn");
        jContactBuilder.addIM(JIM.NETMEETING,"netmeeting");
        jContactBuilder.addIM(JIM.QQ,"qq");
        jContactBuilder.addIM(JIM.SKYPE,"skype");
        jContactBuilder.addIM(JIM.WIN_LIVE,"winlive");
        jContactBuilder.addIM(JIM.YAHOO,"yahoo");
        //java.lang.String type, java.lang.String company, java.lang.String title
        jContactBuilder.addOrg(JORG.WORK,"work-company","work-title");
//        jContactBuilder.addOrg(JORG.OTHER,"other-company","other-title");
        //java.lang.String type, java.lang.String number, boolean isPrimary
        jContactBuilder.addPhone(JPhone.OTHER,"100001",false);
        jContactBuilder.addPhone(JPhone.WORK,"100002",false);
        jContactBuilder.addPhone(JPhone.ASSISTANT,"100003",false);
        jContactBuilder.addPhone(JPhone.CALLBACK,"100004",false);
        jContactBuilder.addPhone(JPhone.CAR,"100005",false);
        jContactBuilder.addPhone(JPhone.COMPANY_MAIN,"100006",false);
        jContactBuilder.addPhone(JPhone.HOME,"100007",false);
        jContactBuilder.addPhone(JPhone.HOME_FAX,"100008",false);
        jContactBuilder.addPhone(JPhone.HOME_MOBILE,"100009",false);
        jContactBuilder.addPhone(JPhone.ISDN,"100010",false);
        jContactBuilder.addPhone(JPhone.MAIN,"100011",false);
        jContactBuilder.addPhone(JPhone.MMS,"100012",false);
        jContactBuilder.addPhone(JPhone.MOBILE,"100013",false);
        jContactBuilder.addPhone(JPhone.RADIO,"100014",false);
        jContactBuilder.addPhone(JPhone.OTHER_FAX,"100015",false);
        jContactBuilder.addPhone(JPhone.PAGE,"100016",false);
        jContactBuilder.addPhone(JPhone.TELEGRAPH,"100017",false);
        jContactBuilder.addPhone(JPhone.TTY_TDD,"100018",false);
        jContactBuilder.addPhone(JPhone.WORK_FAX,"100019",false);
        jContactBuilder.addPhone(JPhone.WORK_MOBILE,"100020",false);
        jContactBuilder.addPhone(JPhone.WORK_PAGE,"100021",false);
        jContactBuilder.addPhone(JPhone.COMPANY_MAIN,"100022",false);
        //java.lang.String type, java.lang.String webpage
        jContactBuilder.addWebpage(JWebpage.HOME,"home-web");
        jContactBuilder.addWebpage(JWebpage.HOMEPAGE,"homepage-web");
        jContactBuilder.addWebpage(JWebpage.OTHER,"other-web");
        //xtags
        //java.lang.String xTag_name, java.lang.String xTag_value
        jContactBuilder.addXTag(JXTag.X_ACCOUNT_TYPE,"10215");
        jContactBuilder.addXTag(JXTag.X_BLOCK,"0");
        jContactBuilder.addXTag(JXTag.X_RINGTONG,"ringtong");
        jContactBuilder.addXTag("X-BORQS-UID","100000000");
        String createContact = jContactBuilder.createJson();

        System.out.println("\r\ncreated contact json: " + createContact);
        
        createContact = "{\"IM\":[],\"TEL\":[],\"BDAY\":\"\",\"EMAIL\":[{\"WORK\":\"iits@cllmttmd.aawocrhao\"}],\"N\":{\"PRE\":\"2012-08-13T11:51:11.380SU1prefix\",\"LN\":\"aaa\"},\"URL\":[],\"ADDR\":[],\"X\":[{\"X-STARRED\":\"false\"},{\"X-BLOCK\":\"false\"},{\"X-BORQS-UID\":\"14219\"}],\"ORG\":[],\"PHOTO\":\"R0lGODlhUABQAPYAAE1XcE5ac1FddVJeeFVifVlkfl5pfGBrfVZkgFhmgltohF1riGJsg2FuimVw\\nhWRximhyhWl0i2Rzk2h1lGh3mGt5lGx7m25+oHB\\/oHaCmnGCo3WHqXWIq3qHp3mHrHyKpHqKrHyN\\nsn6QtICProaRrYKUu4iWsYqYs4iYvJGgvYaZwomcxYueyZCfwIygypWjwpOlzJaozpqqzZGl0ZSo\\n1JWr2p2u0Zir2Z6x0puw26Oz1KS12am21qm22Km41qy72bG+26y+4rTB3LrF3rzI3rLC5rvG4L3J\\n4MPO48bR5cfS6MrS5s3V6M\\/Y6dDX6dPb69je7dvh7t3j8OHm8eTp8+js9ezv+O3w9wAAAAAAAAAA\\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5\\nBAAAAAAALAAAAABQAFAAAAf+gFeCVVVUhlGIT09Ni0tNS0tISERHREKXQD8\\/PT08PDo2NjExMDAu\\np6ipqqsuLK6vsK6pV1aEhlKIuVCKTJBLSUmUlEJDQJmZnzY6MqI2paas0aozsa4yrtSohNtUUt6J\\nT05NTExJv5NIQ+rGmpo6PDg4y6Sk0NL31dWp21Pd31FPADpxQu7cpCPqhAAR0u6Hjh3ybMgYFWPG\\nvYsurr3SCCtVv1veoABUFG5cEiVJkBw5YgQhJk08dnySJ2NiRYsYXbxgBYsjC47QqEwZOkUXQEYF\\nIUlaOeQIpkyaduiYqmwUDJw5V6xYxYJavo4wvP2LAqWsoiZNnJgDtnIlsZf+Pn54UlaznkUWrS7K\\nUvU1H4wpIcnugsKIYBOUSo6oZOlUIVQfnXTEm0fKhVe8+DCn+tk3BgsYrnKRfVK28BInv34laZuQ\\n4Q+GnqSCEmXVBWjNqPZmjnU5VqKyuwY+IdcrkuKVRoZcuqQp7sPZtGF4boUbt95rvT\\/DoraLZLjh\\n48oBW8x0+WsfPjxNpWpzVF6+OU\\/11X6bxUgnigiSU4Iy5eKWywWYnkx01WRDDjncYBtuK1i320au\\n1KeddwMRBwxKSxEhjHnNyfVOCjHI8AIJFhwgAAAbhFACXx21Ut9msdR3223hoEYOapDwh8RqlTQV\\n4A9xOfSDDBlEcMABDCT+yYACCiSgQAEJhFADdbJo9oo9VEr41RLEEQSJOZIchJBy5sEmUwUGRABB\\nAxFEkCSTTTaZAAFR0lADblqCJp9X2VXjJROSLKFEJClpSMQQlpQJ5A5CtOlmA0omuYACB8jpZAIJ\\nSCABTq9Q+cpOr1wmY32pfSkJj6w19lRDKTjgqAOwSorprAkgQCsBJWSjl3ZX+qVamCm1xdpbxrim\\nyQuOQvBArG8msECttN4qwZTURQOLlry6AuyOwiKUnBBGrCqXD0UkuyyzS1oa7a0cYIStXywE2y1C\\nbj0FVVQ8fPBABA48cK6kCjy77sCYYmXttRFWo5iGbVFC5nILtcODQzr+uKqmm5EyKTC0HK9LQAi7\\ngbZTlrBomE6Prb1k7A7p9UCCmv0yAAEEbzJJ8M2bSmNKp3hlGyFL6gS9EHPFNhRTTAfw+wAEGNes\\n7s2zEkBAztbKl1s+ra2jXNGuofdJTB8o7QADTcOJqa2zoj1wkwRgVF1XCbNwycMBGgtkeu\\/o8IMB\\nyiZZttmzPr02piJw5eA9xRoTcUOb4O0JTQZA2nS6NgdOJ9RyKhCAwdXC50IKqyj+GuN3\\/yCbZFN1\\nIPPMSsKpAAGWNql2tGzTqgGMh0tzzL3toCeVepJJ5C8DD7QOuOBnQ40AAu3CuMoLW7HC+A5yyfXJ\\nc6EsY0MLfmdcOcH+swee\\/KwNxid9Oz2YnndM60mU\\/QiRUi448lCT\\/17u0lgv09fvTKZMKKEoRaQw\\nFTAnQal+ylueCDplPlSs5xOyiUdEAEibGKAASZP6nvIQaCsEEEAD1NJNfNYzFQlKMAYnpEgMOFCp\\nDCIQfFDjADYYOMJlSCYeNjDhRKRjlRiIwElMutwLh4ipC1xGV\\/GRwQ1xkEOJ4ECF9LgKDRqQOSGu\\nK3xEREAJunKZBgKQiTJ4IhTrUYpTaMAA4rsUEetHgXw0sCbtqQsZZ1BGVKwAWvRbY7QQEIA76cN8\\nKpTBM54RAxqcgnMS0GPysEgrPqoAbi2KTymsMkgYjCwaHLBiHhX+iYBqIBF0F6kkNOporRVI4Gmb\\nHJ\\/y\\/AhJ\\/F0yFdI5xW3wgYoaEACNtNPjxmrVRzc2sDp6QQUNdqnIgS2PAKzs4j2wxKIGZrKYytNA\\nn\\/DnAhEY0nMNPMUKrLjHYkpgO6GKBg0KxyLdWGkzrOAADFX5QiR6Khp0XMWLqAlPGqAtlY0cmAbe\\nBrdf0jCbXckAN9nJztlp0XAiZAU55YOZhh7uoTMQATfVxsiBDeAu9+unNGZwTYbG7aP\\/hA8xdZmA\\nfT4yoTnBAU5mVA0JMZMvGugmB9Omgndmc6F9wRZGAnDFjkFtAejMJotY2lKUrkIFFUXgAIQaymuB\\n5l0azY3VRDD+gIqirYO0EgGnzIeZhV4tp7xiaFAPGYJiYsWoG40BQp9aVD1JQzNlzaKzOMdUVUDV\\nlxgRQVJ9iqkF0LWuX41RqGKBUK\\/ENW0vpBZgV+ETT\\/5RM9n5oTFvhgCvWC0+uctHn\\/gkQnCqE7EI\\nJEA2esZVnkAIYY\\/FHTYSWb\\/wIWCBUs0JWvvEEY009is14MBeK+rX2C52PsCNRQ1UcDasElFqt\\/ur\\nUDXLGXBuFqk9lSnUCMAB5frztvPxynAlsNchym5OCqiub7nKG+uQtlMqaMBAoWlMDojgpB2Z4Qpm\\nEL3cUKM3q7jLDGpQggaAj5FXBXBxaZUBaU7TFfNV7QzdeYr+GoiAAxrgIz6hiUUC8FQDC6wBfvHS\\noCr50iIl0AABqsreEi9vVjzlgApmgMS78PMqLqDA6y6ngBOvk6AlNubyFqCB+XZYwTDYAOwmm+PW\\ndneRCQhAdbeCm8uUYAC5LDKRi4yA2zXUoyxQY3S3DFq+Stmgyzvpe1ARgvWyF4tHhqYGOpqKGox0\\njWmW8sAIAACezEAAM82nnEtMgNceDs9U5vKeG\\/naU1zTFdD1bl\\/nvIBdvrnExAzvfMnpihAgYMLF\\nNPOXndSuiObms6hUJOxe5yTYabrITTLwWUsARNe57lnPcrXr+qqxWgcM1nDaWKOd1VdY83rRsnaS\\nCBbIgkABAAA7\\n\"}";

        return createContact;


    }
//
    class MockContext implements Context {
        private static final String CONFIG_BASE_FILE = "config/server.properties";

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
//
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

        @Override
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
