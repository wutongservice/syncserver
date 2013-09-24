/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.pim.jcontact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.borqs.json.JSONArray;
import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import com.borqs.pim.jcontact.JContact.TypedEntity;


public class JContact_Test {
    //test the JContact create and to_Jason string
    @Test
    public void create_test(){
        final String firstName = "firstName";
        final String middleName = "middleName";
        final String lastName = "lastName";
        final String nickName = "nickName";
        final String mobile = "0123456789";
        final String work_number = "0108888888";
        final String work_number2 = "01088888881";
        final String home_email = "home@work.com";
        final String work_email = "work@work.com";
        final String street = "street";
        final String city = "city";
        final String province = "province";
        final String zipcode = "zipcode";
        final String street2 = "street2";
        final String city2 = "city2";
        final String province2 = "province2";
        final String zipcode2 = "zipcode2";
        final String company = "company";
        final String title = "title";
        final String company2 = "company2";
        final String title2 = "title2";
        final String qq = "111111111";
        final String msn = "test@msn.com";
        final String webpage = "webpage";
        final String birthday = "YYYY-MM-DD";
        final String note = "note";
        final String xTag_type = "xtag_name";
        final String xTag_value = "xTag_value";
        final String xTag_type2 = "xtag_name2";
        final String xTag_value2 = "xTag_value2";        
        boolean isPrimary = false;
        
        JContactBuilder builder = new JContactBuilder();
        builder .setFirstName(firstName, "pingyin")
                .setMiddleName(middleName, "pingyin")
                .setLastName(lastName, "pingyin")
                .setNickName(nickName)
                .setNamePrefix("")
                .setNamePostfix("")
                .addPhone(JPhone.MOBILE, mobile, isPrimary)
                .addPhone(JPhone.WORK, work_number, false)
                .addPhone(JPhone.WORK, work_number2, false)
                .addEmail(JEMail.WORK, work_email, isPrimary)
                .addEmail(JEMail.HOME, home_email, false)
                .addAddress(JAddress.HOME, street, city, province, zipcode)
                .addAddress(JAddress.WORK, street2, city2, province2, zipcode2)
                .addOrg(JORG.WORK, company, title)
                .addOrg(JORG.OTHER, company2, title2)
                .addIM(JIM.QQ, qq)
                .addIM(JIM.MSN, msn)
                .addWebpage(JWebpage.HOMEPAGE, webpage)
                .setBirthday(birthday)
                .setNote(note)
                .addXTag(xTag_type, xTag_value)
                .addXTag(xTag_type2, xTag_value2);
        
        String jcontent = builder.createJson();        
        
        //verify string content        
        assertTrue(jcontent.indexOf(firstName)>0);
        assertTrue(jcontent.indexOf(middleName)>0);
        assertTrue(jcontent.indexOf(lastName)>0);
        assertTrue(jcontent.indexOf(nickName)>0);
        
        assertTrue(jcontent.indexOf(JPhone.MOBILE)>0);
        assertTrue(jcontent.indexOf(mobile)>0);
        
        assertTrue(jcontent.indexOf(JEMail.WORK)>0);
        assertTrue(jcontent.indexOf(work_email)>0);
        
        assertTrue(jcontent.indexOf(JAddress.HOME)>0);
        assertTrue(jcontent.indexOf(street)>0);
        assertTrue(jcontent.indexOf(city)>0);
        assertTrue(jcontent.indexOf(province)>0);
        assertTrue(jcontent.indexOf(zipcode)>0);
        assertTrue(jcontent.indexOf(company)>0);
        assertTrue(jcontent.indexOf(title)>0);
        
        assertTrue(jcontent.indexOf(JIM.QQ)>0);
        assertTrue(jcontent.indexOf(qq)>0);
        
        assertTrue(jcontent.indexOf(webpage)>0);
        assertTrue(jcontent.indexOf(birthday)>0);
        assertTrue(jcontent.indexOf(note)>0);
        
        assertTrue(jcontent.indexOf(xTag_type)>0);
        assertTrue(jcontent.indexOf(xTag_value)>0);
        
        
        //verify field value
        JContact contact = JContact.fromJsonString(jcontent);
        assertEquals(firstName, contact.getFirstName());
        assertEquals(middleName, contact.getMiddleName());
        assertEquals(lastName, contact.getLastName());
        assertEquals(nickName, contact.getNickName());
        assertEquals("pingyin", contact.getFirstNamePinyin());
        assertEquals("pingyin", contact.getMiddleNamePinyin());
        assertEquals("pingyin", contact.getLastNamePinyin());
        assertEquals(null, contact.getNamePrefix());
        assertEquals(null, contact.getNamePostfix());
        
        List<TypedEntity> phones = contact.getPhoneList();
        assertTrue(phones.size()==3);

        assertTrue(check(phones, JPhone.MOBILE, mobile, isPrimary));
        assertTrue(check(phones, JPhone.WORK, work_number, false));
        assertTrue(check(phones, JPhone.WORK, work_number2, false));
        
        List<TypedEntity> emails = contact.getEmailList();
        assertTrue(emails.size()==2);
        assertTrue(check(emails, JEMail.WORK, work_email, isPrimary));
        assertTrue(check(emails, JEMail.HOME, home_email, false));
        
        List<TypedEntity> addresses = contact.getAddressList();
        assertTrue(addresses.size()==2);
        assertTrue(check(addresses, JAddress.HOME, street, city, province, zipcode));
        assertTrue(check(addresses, JAddress.WORK, street2, city2, province2, zipcode2));
        
        
        List<TypedEntity> orgs = contact.getOrgList();
        assertTrue(orgs.size()==2);
        assertTrue(check(orgs, JORG.WORK, company, title));
        assertTrue(check(orgs, JORG.OTHER, company2, title2));
        
        List<TypedEntity> ims = contact.getIMList();
        assertTrue(ims.size()==2);
        assertTrue(check(ims, JIM.QQ, qq));
        assertTrue(check(ims, JIM.MSN, msn));
        
        List<TypedEntity> webpages = contact.getWebpageList();
        assertTrue(webpages.size()==1);
        assertEquals(webpage, webpages.get(0).getValue());
        
        assertEquals(birthday, contact.getBirthday());
        assertEquals(note, contact.getNote());
        
        List<TypedEntity> xTags = contact.getXTags();
        assertTrue(xTags.size()==2);
        assertTrue(check(xTags, xTag_type, xTag_value));
        assertTrue(check(xTags, xTag_type2, xTag_value2));

    }
     
    private boolean check(List<TypedEntity> items, String type, String value, boolean isPrimary){
        boolean found = false;
        for(TypedEntity e : items){
            found = type.equals(e.getType());
            found &= value.equals(e.getValue());
            found &= JPhone.isPrimary(e)==isPrimary;
            
            if(found){
                return true;
            }
        }
        return false;
    }
    private boolean check(List<TypedEntity> items, String type, String value){
        boolean found = false;
        for(TypedEntity e : items){
            found = type.equals(e.getType());
            found &= value.equals(e.getValue());
            if(found){
                return true;
            }
        }
        return found;
    }
    
    private boolean check(List<TypedEntity> items, String type, String st, String city, String province, String zipCode){
        boolean found = false;
        for(TypedEntity e : items){
            found = type.equals(e.getType());
            Object addr = e.getValue();
            found &= JAddress.street(addr).equals(st);
            found &= JAddress.city(addr).equals(city);
            found &= JAddress.province(addr).equals(province);
            found &= JAddress.zipcode(addr).equals(zipCode);
            if(found){
                return true;
            }
        }
        return found;
    }
  
    private boolean check(List<TypedEntity> items, String type, String company, String title){
        boolean found = false;
        for(TypedEntity e : items){
            found = type.equals(e.getType());
            Object org = e.getValue();
            found &= JORG.company(org).equals(company);
            found &= JORG.title(org).equals(title);
            if(found){
                return true;
            }
        }
        return found;
    }
}
