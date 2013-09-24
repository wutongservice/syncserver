/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Date: 2/27/12
 * Time: 10:52 AM
 * Borqs project
 */
public class ProfileRecordTest {
    
    @Test
    public void test_parse() throws Exception {
        String profileContent = "{\"contact_info\" : {\n" +
                "    \"email_3_address\" : \"xuetong3.chen@borqs.com\",\n" +
                "    \"email_2_address\" : \"xuetong2.chen@borqs.com\",\n" +
                "    \"email_address\" : \"chenxt.borqs@gmail.com\",\n" +
                "    \"mobile_telephone_number\" : \"13910912321\",\n" +
                "    \"mobile_2_telephone_number\" : \"13910912322\",\n" +
                "    \"mobile_3_telephone_number\" : \"13910912323\",\n" +
                "    \"business_telephone_number\" : \"85556661\",\n" +
                "    \"business_2_telephone_number\" : \"85556662\",\n" +
                "    \"business_3_telephone_number\" : \"85556663\",\n" +
                "    \"home_telephone_number\" : \"15556661\",\n" +
                "    \"home_2_telephone_number\" : \"15556662\",\n" +
                "    \"home_3_telephone_number\" : \"15556663\",\n" +
                "    \"web_page\" : \"my.borqs.com\"\n" +
                "  },\n" +
                "  \"first_name\" : \"Tong\",\n" +
                "  \"last_name\" : \"Chen\",\n" +
                "  \"middle_name\" : \"Xue\",\n" +
                "    \"login_phone1\" : \"13800138001\",\n" +
                "    \"login_phone2\" : \"13800138002\",\n" +
                "    \"login_phone3\" : \"13800138003\",\n" +
                "    \"login_email1\" : \"email1@email1.com\",\n" +
                "    \"login_email2\" : \"email2@email2.com\",\n" +
                "    \"login_email3\" : \"email3@email3.com\",\n" +
                "  \"user_id\" : 226,\n" +
                "  \"display_name\" : \"ChenXuetong\",\n" +
                "  \"profile_privacy\" : true,\n" +
                " \"domain_name\" : \"com.borqs\",\n" +
                " \"profession\" : \"my_profession\",\n" +
                " \"office_address\" : \"my office\",\n" +
                " \"languages\" : \"Chinese\",\n" +
                " \"job_title\" : \"manager\",\n" +
                " \"interests\" : \"my_interests\",\n" +
                " \"gender\" : \"male\",\n" +
                " \"company\" : \"Borqs\",\n" +
                " \"about_me\" : \"good\",\n" +
                " \"birthday\" : \"2001-01-01\",\n" +
                " \"basic_updated_time\" : 1330064616963,\n" +
                " \"profile_updated_time\" : 1320213929595,\n" +
                " \"contact_info_updated_time\" : 1325843098476,\n" +
                " \"address_updated_time\" : 1322133301983,\n" +
                "  \"address\" : [ {\n" +
                "    \"postal_code\" : \"PCODE\",\n" +
                "    \"street\" : \"STREET\",\n" +
                "    \"state\" : \"STATE\",\n" +
                "    \"type\" : \"TYPE\",\n" +
                "    \"po_box\" : \"PO_BOX\",\n" +
                "    \"extended_address\" : \"EX_ADDRESS\",\n" +
                "    \"city\" : \"CITY\",\n" +
                "    \"country\" : \"COUNTRY\"\n" +
                "  } ]," +
                " \"pedding_requests\" : \"[]\"\n" +
                "}";

        StringBufferInputStream contentReader = new StringBufferInputStream(profileContent);
        ProfileRecord pr = ProfileRecord.createFrom(new InputStreamReader(contentReader));
        
        Contact c = pr.asContact();

        assertFalse(pr.isContactInfoVisible());
        assertEquals("226", c.getBorqsId());
        assertEquals("Tong", c.getBFirstName());
        assertEquals("Xue", c.getBMiddleName());
        assertEquals("Chen", c.getBLastName());
        assertEquals("ChenXuetong", c.getDisplayName());
        assertEquals("my_profession", c.getProfession());
        assertEquals("my office", c.getOfficeLocation());
        assertEquals("Chinese", c.getLanguages());
        assertEquals("manager", c.getJobTitle());
        assertEquals("my_interests", c.getHobbies());
        assertEquals("male", c.getGender());
        assertEquals("Borqs", c.getCompany());
        assertEquals("good", c.getBody());
        assertEquals("2001-01-01", c.getBirthday());

        assertEquals(1330064616963L, pr.getBasicInfoLastUpdateTime());
        assertEquals(1320213929595L, pr.getProfileLastUpdateTime());
        assertEquals(1325843098476L, pr.getContactInfoLastUpdateTime());
        assertEquals(1322133301983L, pr.getAddressLastUpdateTime());


        List<ContactItem> tels = c.getTelephones();
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER, findByValue(tels, "13910912321"));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER, findByValue(tels, "13910912322"));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER, findByValue(tels, "13910912323"));


        assertEquals("85556661", findByType(tels, ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER));

        assertEquals(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER, findByValue(tels, "85556661"));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER, findByValue(tels, "85556662"));

        assertEquals("15556661", findByType(tels, ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER));

        assertEquals(ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER, findByValue(tels, "15556661"));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER, findByValue(tels, "15556662"));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER, findByValue(tels, "15556663"));


        List<ContactItem> emails = c.getEmails();
        assertEquals("chenxt.borqs@gmail.com", findByType(emails, ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS));
        assertEquals("xuetong2.chen@borqs.com", findByType(emails, ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS));
        assertEquals("xuetong3.chen@borqs.com", findByType(emails, ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS));

        assertEquals(ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS, findByValue(emails, "chenxt.borqs@gmail.com"));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS, findByValue(emails, "xuetong2.chen@borqs.com"));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS, findByValue(emails, "xuetong3.chen@borqs.com"));
        
        List<String> loginEmails = c.getLoginEmails();
        assertEquals(3,loginEmails.size());
        assertTrue(loginEmails.contains("email1@email1.com"));
        assertTrue(loginEmails.contains("email2@email2.com"));
        assertTrue(loginEmails.contains("email3@email3.com"));

        List<String> loginPhones = c.getLoginPhones();
        assertEquals(3,loginPhones.size());
        assertTrue(loginPhones.contains("13800138001"));
        assertTrue(loginPhones.contains("13800138002"));
        assertTrue(loginPhones.contains("13800138003"));


        List<ContactItem> webpages = c.getWebpages();
        assertEquals("my.borqs.com", findByType(webpages, ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE));
        assertEquals(ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE, findByValue(webpages, "my.borqs.com"));

        List<Address> addresses = c.getAddress();
        for(Address a:addresses){
            if(a.getType() == Address.ADDRESS_TYPE_OTHER){
                assertEquals("PCODE",a.getPostalCode());
                assertEquals("STREET",a.getStreet());
                assertEquals("STATE",a.getState());
                assertEquals("PO_BOX",a.getPostOfficeAddress());
                assertEquals("EX_ADDRESS",a.getExtendedAddress());
                assertEquals("CITY",a.getCity());
                assertEquals("COUNTRY",a.getCountry());
            }
        }
    }
    
    private int findByValue(List<ContactItem> list, String value){
        for(ContactItem t : list){
            if(value.equals(t.getValue())){
                return t.getType();
            }
        }

        return -1;
    }

    private String findByType(List<ContactItem> list, int type){
        for(ContactItem t : list){
            if(t.getType() == type){
                return t.getValue();
            }
        }

        return null;
    }
}
