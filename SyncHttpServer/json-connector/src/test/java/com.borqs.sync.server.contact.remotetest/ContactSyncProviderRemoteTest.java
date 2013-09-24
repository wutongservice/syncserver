/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.contact.remotetest;

import com.borqs.json.JSONObject;
import com.borqs.pim.jcontact.*;
import com.borqs.sync.avro.IContactSyncMLProvider;
import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.contact.BCSConfig;
import com.borqs.sync.server.contact.convert.JContactConverter;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;
import com.borqs.sync.server.rpc.base.naming.NotFoundException;
import com.borqs.sync.server.rpc.base.naming.RemoteService;
import com.funambol.json.converter.KeyConverter;
import com.funambol.json.domain.JsonItem;
import junit.framework.Assert;
import org.apache.avro.AvroRemoteException;
import org.junit.Test;

import java.util.List;

/**
 *  test for ContactProvider
 */
public final class ContactSyncProviderRemoteTest {

    @Test
    public void testAddItem(){
        try {
            IContactSyncMLProvider contactSyncMLProvider = createAccountSyncService();
            XResponse response = contactSyncMLProvider.addItem("10215",createContactJson(),System.currentTimeMillis());
            Assert.assertNotNull(response.content);
            long key = parseAddResponse(response.content.toString());
            Assert.assertTrue(key > 0);

            response = contactSyncMLProvider.getItem("10215",String.valueOf(key));
            String contactJson = response.content.toString();

            JContactConverter converter = new JContactConverter();
            JsonItem<JContact> item = converter.fromJSON(contactJson);
            JContact jContactReaded = item.getItem();

             item = converter.fromJSON(createContactJson());
            JContact provided = item.getItem();

            checkContact(provided,jContactReaded);

        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (AvroRemoteException e) {
            e.printStackTrace();
        }
    }

    private void checkContact(JContact provided,JContact readed){
        Assert.assertEquals(provided.getFirstName(),readed.getFirstName());
        Assert.assertEquals(provided.getMiddleName(),readed.getMiddleName());
        Assert.assertEquals(provided.getLastName(),readed.getLastName());
        Assert.assertEquals(provided.getBirthday(),readed.getBirthday());
        Assert.assertEquals(provided.getFirstNamePinyin(),readed.getFirstNamePinyin());
        Assert.assertEquals(provided.getMiddleNamePinyin(),readed.getMiddleNamePinyin());
        Assert.assertEquals(provided.getLastNamePinyin(),readed.getLastNamePinyin());
        Assert.assertEquals(provided.getNamePrefix(),readed.getNamePrefix());
        Assert.assertEquals(provided.getNamePostfix(),readed.getNamePostfix());
        Assert.assertEquals(provided.getNickName(),readed.getNickName());
        Assert.assertEquals(provided.getNote(),readed.getNote());
        Assert.assertEquals(provided.getPhoto(),readed.getPhoto());

        compareList(provided.getAddressList(),readed.getAddressList());

        compareList(provided.getEmailList(),readed.getEmailList());

        compareList(provided.getIMList(),readed.getIMList());

        compareList(provided.getOrgList(),readed.getOrgList());

        comparePhoneList(provided.getPhoneList(),readed.getPhoneList());

        compareWebList(provided.getWebpageList(),readed.getWebpageList());

        compareList(provided.getXTags(),readed.getXTags());
    }

    private void comparePhoneList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
        if(providerdList == null){
            Assert.assertNull(readedList);
        }else{
            Assert.assertNotNull(readedList);
            Assert.assertEquals(providerdList.size(),readedList.size());
            for(JContact.TypedEntity provided:providerdList){
                String type = provided.getType();
                boolean found = false;
                for(JContact.TypedEntity readed:readedList){
                    if(String.valueOf(provided.getValue()).equals(String.valueOf(readed.getValue()))){
                        found = true;
                    }
                }
                Assert.assertTrue(found);
            }
        }
    }
    private void compareList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
        if(providerdList == null){
            Assert.assertNull(readedList);
        }else{
            Assert.assertNotNull(readedList);
            Assert.assertEquals(providerdList.size(),readedList.size());
            for(JContact.TypedEntity provided:providerdList){
                String type = provided.getType();
                boolean found = false;
                for(JContact.TypedEntity readed:readedList){
                    if(readed.getType().equals(type)){
                        found = true;
                        Assert.assertEquals(provided.getValue().toString(),readed.getValue().toString());
                    }
                }
                Assert.assertTrue(found);
            }
        }
    }

    private void compareWebList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
        if(providerdList == null){
            Assert.assertNull(readedList);
        }else{
            Assert.assertNotNull(readedList);
            Assert.assertEquals(providerdList.size(),readedList.size());
            for(JContact.TypedEntity provided:providerdList){
                String type = provided.getType();
                boolean found = false;
                for(JContact.TypedEntity readed:readedList){
                    //readed:"URL":[{"HOMEPAGE":"homepage-web"},{"HOMEPAGE":"other-web"},{"HOME":"home-web"}],
                    //provided:"URL":[{"HOME":"home-web"},{"HOMEPAGE":"homepage-web"},{"OTHER":"other-web"}]

                    if(provided.getValue().toString().equals(readed.getValue().toString())){
                        if(provided.getType().equals(readed.getType()) || readed.getType().equals(JWebpage.HOMEPAGE)){
                            found  = true;
                        }
                    }
                }
                Assert.assertTrue(found);
            }
        }
    }

//    private void compareAddressList(List<JContact.TypedEntity> providerdList,List<JContact.TypedEntity> readedList){
//        if(providerdList == null){
//            Assert.assertNull(readedList);
//        }else{
//            Assert.assertNotNull(readedList);
//            Assert.assertEquals(providerdList.size(),readedList.size());
//            for(JContact.TypedEntity provided:providerdList){
//                String type = provided.getType();
//                boolean found = false;
//                for(JContact.TypedEntity readed:readedList){
//                    if(readed.getType().equals(type)){
//                        found = true;
//                        Assert.assertEquals(JAddress.city(provided.getValue()),JAddress.city(readed.getValue()));
//                        Assert.assertEquals(JAddress.province(provided.getValue()),JAddress.province(readed.getValue()));
//                        Assert.assertEquals(JAddress.street(provided.getValue()),JAddress.street(readed.getValue()));
//                        Assert.assertEquals(JAddress.zipcode(provided.getValue()),JAddress.zipcode(readed.getValue()));
//                    }
//                }
//                Assert.assertTrue(found);
//            }
//        }
//    }

    private IContactSyncMLProvider createAccountSyncService() throws RPCException, NotFoundException {
        String naming_host = /*BCSConfig.getConfigString(BCSConfig.NAMING_HOST)*/"127.0.0.1";
        int naming_port = /*BCSConfig.getConfigInt(BCSConfig.NAMING_PORT)*/8999;

        NamingServiceProxy ns = new NamingServiceProxy(naming_host, naming_port);
        RemoteService rs = new RemoteService(IContactSyncMLProvider.class,ns);
        return rs.asInterface();
    }

    private long parseAddResponse(String responseContent){
        //{"data":{"key":"3833883"}}
        KeyConverter converter = new KeyConverter();
        String id = converter.fromJSON(responseContent);
        System.out.print("contact id is :" + id);
        return Long.parseLong(id);
    }
    
    private String createContactJson(){
        JContactBuilder jContactBuilder = new JContactBuilder();
        jContactBuilder.setFirstName("firstname","firstnamepinyin ");
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

        return createContact;


    }



}
