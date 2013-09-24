import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.syncml.converter.JContactConverter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 7/2/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class JContactConverterTest {

    @Test
    public void testConvertToContact(){

        String contactJson = "{\"IM\":[{\"QQ\":\"qq\"},{\"MSN\":\"msn\"},{\"SKYPE\":\"skype\"},{\"AIM\":\"aim\"}," +
                "{\"YAHOO\":\"yahoo\"},{\"GTALK\":\"gtalk\"},{\"ICQ\":\"icq\"},{\"WLIVE\":\"wlive\"},{\"JABBER\":\"jabber\"}," +
                "{\"NMEETING\":\"netmeeting\"}]," +
                "\"TEL\":[{\"MOBILE\":\"138 0013 8000\"}," +
                "{\"HOME\":\"138 0013 8001\"}," +
                "{\"WORK\":\"138 0013 8002\"}," +
                "{\"OTHER\":\"138 0013 8003\"}," +
                "{\"WFAX\":\"138 0013 8004\"}," +
                "{\"HFAX\":\"138 0013 8005\"}," +
                "{\"PAGE\":\"138 0013 8006\"}," +
                "{\"CBACK\":\"138 0013 8007\"}," +
                "{\"CAR\":\"138 0013 8008\"}," +
                "{\"CMAIN\":\"138 0013 8009\"}," +
                "{\"MAIN\":\"138 0013 8010\"}," +
                "{\"OFAX\":\"138 0013 8011\"}," +
                "{\"RADIO\":\"138 0013 8012\"}," +
                "{\"TELEGRAPH\":\"138 0013 8013\"}," +
                "{\"ASSISTANT\":\"138 0013 8014\"}]," +
                "\"BDAY\":\"2012-01-01\"," +
                "\"EMAIL\":[{\"HOME\":\"email1@email.com\"}," +
                "{\"WORK\":\"email2@email.com\"}," +
                "{\"OTHER\":\"email3@email.com\"}]," +
                "\"N\":{\"POST\":\"sufix\",\"PRE\":\"prefix\",\"FN\":\"firstname\",\"LN_PY\":\"lastnamepinyin\",\"FN_PY\":\"firstnamepinyin\",\"MN\":\"middlename\",\"LN\":\"lastname\",\"MN_PY\":\"middlenamepinyin\",\"NN\":\"nickname\"}," +
                "\"URL\":[{\"WORK\":\"work.com\"},{\"HOME\":\"home.com\"},{\"HOMEPAGE\":\"homepage.com\"}]," +
                "\"ADDR\":[{\"HOME\":{\"ZC\":\"\",\"PRO\":\"\",\"CITY\":\"\",\"ST\":\"beijing1\"}}," +
                "{\"WORK\":{\"ZC\":\"\",\"PRO\":\"\",\"CITY\":\"\",\"ST\":\"beijing2\"}}," +
                "{\"OTHER\":{\"ZC\":\"\",\"PRO\":\"\",\"CITY\":\"\",\"ST\":\"beijing3\"}}]," +
                "\"X\":[{\"X-STARRED\":\"false\"}," +
                "{\"X-BLOCK\":\"false\"}," +
                "{\"X-ACCOUNT-TYPE\":\"com.borqs\"}]," +
                "\"ORG\":[{\"OTHER\":{\"COMPANY\":\"company\",\"TITLE\":\"title\"}}]}";
        Contact contact  = JContactConverter.toContact(contactJson);
        List<ContactItem> phones = contact.getTelephones();
        assertNotNull(phones);
        assertTrue(phones.size() == 15);
        for(ContactItem phone:phones){
            if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8014");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8002");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8002");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8004");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8007");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8008");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8009");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8001");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8005");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8001");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8000");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8011");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8003");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8006");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8010");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8012");
            }else if(phone.getType() == ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER){
                assertEquals(phone.getValue(),"138 0013 8013");
            }
        }

        List<ContactItem> emails = contact.getEmails();
        assertNotNull(emails);
        assertTrue(emails.size() == 3);
        for(ContactItem email:emails){
            if(email.getType() == ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS){
                assertEquals(email.getValue(),"email2@email.com");
            }else if(email.getType() == ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS){
                assertEquals(email.getValue(),"email1@email.com");
            }else if(email.getType() == ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS){
                assertEquals(email.getValue(),"email3@email.com");
            }
        }

        List<ContactItem> ims = contact.getIms();
        assertNotNull(ims);
        assertTrue(ims.size() == 10);
        for(ContactItem im:ims){
            if(im.getType() == ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER){
                assertEquals(im.getValue(),"qq");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_AIM){
                assertEquals(im.getValue(),"aim");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_GTALK){
                assertEquals(im.getValue(),"gtalk");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_ICQ){
                assertEquals(im.getValue(),"icq");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_JABBER){
                assertEquals(im.getValue(),"jabber");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_MSN){
                assertEquals(im.getValue(),"msn");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_NETMEETING){
                assertEquals(im.getValue(),"netmeeting");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_SKYPE){
                assertEquals(im.getValue(),"skype");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_WIN_LIVE){
                assertEquals(im.getValue(),"wlive");
            }else if(im.getType() == ContactItem.TYPE_X_TAG_IM_YAHOO){
                assertEquals(im.getValue(),"yahoo");
            }
        }
        
        List<ContactItem> webs = contact.getWebpages();
        assertNotNull(webs);
        assertTrue(webs.size() == 3);
        for(ContactItem web:webs){
            if(web.getType() == ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE){
                assertEquals(web.getValue(),"homepage.com");
            }else if(web.getType() == ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE){
                assertEquals(web.getValue(),"home.com");
            }else if(web.getType() == ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE){
                assertEquals(web.getValue(),"work.com");
            }
        }
        
        List<Address> addresses = contact.getAddress();
        assertNotNull(addresses);
        assertTrue(addresses.size() == 3);
        for(Address address:addresses){
            if(address.getType() == Address.ADDRESS_TYPE_HOME){
                assertEquals(address.getStreet(),"beijing1");
            }else if(address.getType() == Address.ADDRESS_TYPE_OTHER){
                assertEquals(address.getStreet(),"beijing3");
            }else if(address.getType() == Address.ADDRESS_TYPE_WORK){
                assertEquals(address.getStreet(),"beijing2");
            }
        }

        assertEquals(contact.getFirstName(),"firstname");
        assertEquals(contact.getMiddleName(),"middlename");
        assertEquals(contact.getLastName(),"lastname");
        assertEquals(contact.getSuffix(),"sufix");
        assertEquals(contact.getNickName(),"nickname");
        
        List<ContactItem> xtags = contact.getXTags();
        assertNotNull(xtags);
        assertTrue(xtags.size() == 7);
        for(ContactItem xtag:xtags){
            if(xtag.getType() == ContactItem.TYPE_X_TAG_PREFIX_NAME){
                assertEquals(xtag.getValue(),"prefix");
            }else if(xtag.getType() == ContactItem.TYPE_X_TAG_PHONETIC_MIDDLE_NAME){
                assertEquals(xtag.getValue(),"middlenamepinyin");
            }else if(xtag.getType() == ContactItem.TYPE_X_TAG_PHONETIC_LAST_NAME){
                assertEquals(xtag.getValue(),"lastnamepinyin");
            }else if(xtag.getType() == ContactItem.TYPE_X_TAG_PHONETIC_FIRST_NAME){
                assertEquals(xtag.getValue(),"firstnamepinyin");
            }else if(xtag.getType() == ContactItem.TYPE_X_TAG_STARRED){
                assertEquals(xtag.getValue(),"false");
            }else if(xtag.getType() == ContactItem.TYPE_X_TAG_ACCOUNT_TYPE){
                assertEquals(xtag.getValue(),"com.borqs");
            }else if(xtag.getType() == ContactItem.TYPE_X_TAG_BLOCK){
                assertEquals(xtag.getValue(),"false");
            }
        }
        
        assertEquals(contact.getCompany(), "company");
        assertEquals(contact.getTitle(),"title");
        assertEquals(contact.getBirthday(), "2012-01-01");
    }
    
    @Test
    public void testContactToJson(){
        Contact contact = new Contact();
        contact.setFirstName("firstname");
        contact.setMiddleName("middlename");
        contact.setLastName("lastname");
        contact.setBirthday("2012-01-01");
        contact.setBody("body");
        contact.setCompany("company");
        contact.setTitle("title");
        contact.setNickName("nickname");
        contact.setSuffix("suffix");
//        contact.setBorqsName("borqsname");

        //phones
        List<ContactItem> phones = new ArrayList<ContactItem>();
        ContactItem phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER);
        phone.setValue("13800138000");
        phones.add(phone);

//        phone = new ContactItem();
//        phone.setType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER);
//        phone.setValue("13800138001");
//        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER);
        phone.setValue("13800138002");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER);
        phone.setValue("13800138003");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER);
        phone.setValue("13800138004");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER);
        phone.setValue("13800138005");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER);
        phone.setValue("13800138006");
        phones.add(phone);

//        phone = new ContactItem();
//        phone.setType(ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER);
//        phone.setValue("13800138007");
//        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER);
        phone.setValue("13800138008");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER);
        phone.setValue("13800138009");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER);
        phone.setValue("13800138010");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER);
        phone.setValue("13800138011");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER);
        phone.setValue("13800138012");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER);
        phone.setValue("13800138013");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER);
        phone.setValue("13800138014");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER);
        phone.setValue("13800138015");
        phones.add(phone);

        phone = new ContactItem();
        phone.setType(ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER);
        phone.setValue("13800138016");
        phones.add(phone);

        contact.setTelephones(phones);

        //emails
        List<ContactItem> emails = new ArrayList<ContactItem>();
        ContactItem email = new ContactItem();
        email.setType(ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS);
        email.setValue("email1@email.com");
        emails.add(email);

        email = new ContactItem();
        email.setType(ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS);
        email.setValue("email2@email.com");
        emails.add(email);

        email = new ContactItem();
        email.setType(ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS);
        email.setValue("email3@email.com");
        emails.add(email);

        contact.setEmails(emails);
        
        //ims
        List<ContactItem> ims = new ArrayList<ContactItem>();
        ContactItem im = new ContactItem();
        im.setType(ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER);
        im.setValue("qq");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_AIM);
        im.setValue("aim");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_GTALK);
        im.setValue("gtalk");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_ICQ);
        im.setValue("icq");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_JABBER);
        im.setValue("jabber");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_MSN);
        im.setValue("msn");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_NETMEETING);
        im.setValue("netmeeting");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_SKYPE);
        im.setValue("skype");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_WIN_LIVE);
        im.setValue("live");
        ims.add(im);

        im = new ContactItem();
        im.setType(ContactItem.TYPE_X_TAG_IM_YAHOO);
        im.setValue("yahoo");
        ims.add(im);

        contact.setIms(ims);
        
        //webs
        List<ContactItem>  webs = new ArrayList<ContactItem>();
        ContactItem web = new ContactItem();
        web.setType(ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE);
        web.setValue("webpage.com");
        webs.add(web);

        web = new ContactItem();
        web.setType(ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE);
        web.setValue("homewebpage.com");
        webs.add(web);

        web = new ContactItem();
        web.setType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE);
        web.setValue("workwebpage.com");
        webs.add(web);

        contact.setWebpages(webs);

        //addresses  street, city, state, postalCode
        List<Address> addresses = new ArrayList<Address>();
        Address.BUILDER builder = new Address.BUILDER();
        builder.setType(Address.ADDRESS_TYPE_HOME);
        builder.setCity("home_city");
//        builder.setCountry("home_country");
//        builder.setExtends("home_extends");
        builder.setPostalCode("home_postalcode");
//        builder.setPostOfficeAddress("home_postofficeaddress");
        builder.setState("home_state");
        builder.setStreet("home_street");
        addresses.add(builder.build());

        builder = new Address.BUILDER();
        builder.setType(Address.ADDRESS_TYPE_OTHER);
        builder.setCity("other_city");
//        builder.setCountry("other_country");
//        builder.setExtends("other_extends");
        builder.setPostalCode("other_postalcode");
//        builder.setPostOfficeAddress("other_postofficeaddress");
        builder.setState("other_state");
        builder.setStreet("other_street");
        addresses.add(builder.build());

        builder = new Address.BUILDER();
        builder.setType(Address.ADDRESS_TYPE_WORK);
        builder.setCity("work_city");
//        builder.setCountry("work_country");
//        builder.setExtends("work_extends");
        builder.setPostalCode("work_postalcode");
//        builder.setPostOfficeAddress("work_postofficeaddress");
        builder.setState("work_state");
        builder.setStreet("work_street");
        addresses.add(builder.build());

        contact.setAddress(addresses);
        
        List<ContactItem> xTags = new ArrayList<ContactItem>();
        ContactItem xTag = new ContactItem();
        xTag.setType(ContactItem.TYPE_X_TAG_ACCOUNT_TYPE);
        xTag.setValue("com.borqs");
        xTags.add(xTag);

        xTag = new ContactItem();
        xTag.setType(ContactItem.TYPE_X_TAG_BLOCK);
        xTag.setValue("false");
        xTags.add(xTag);

//        xTag = new ContactItem();
//        xTag.setType(ContactItem.TYPE_X_TAG_BORQS_NAME);
//        xTag.setValue("borqsname");
//        xTags.add(xTag);

        xTag = new ContactItem();
        xTag.setType(ContactItem.TYPE_X_TAG_GROUP);
        xTag.setValue("group");
        xTags.add(xTag);

        xTag = new ContactItem();
        xTag.setType(ContactItem.TYPE_X_TAG_PHONETIC_FIRST_NAME);
        xTag.setValue("firstnamepinyin");
        xTags.add(xTag);

        xTag = new ContactItem();
        xTag.setType(ContactItem.TYPE_X_TAG_PHONETIC_MIDDLE_NAME);
        xTag.setValue("middlenamepinyin");
        xTags.add(xTag);

        xTag = new ContactItem();
        xTag.setType(ContactItem.TYPE_X_TAG_PHONETIC_LAST_NAME);
        xTag.setValue("lastnamepinyin");
        xTags.add(xTag);

        xTag = new ContactItem();
        xTag.setType(ContactItem.TYPE_X_TAG_PREFIX_NAME);
        xTag.setValue("prefix");
        xTags.add(xTag);

        contact.setXTags(xTags);
        

        String contactJson = JContactConverter.toContactJson(contact);
        Contact contact1 = JContactConverter.toContact(contactJson);
        assertEquals(contact.getFirstName(),contact1.getFirstName());
        assertEquals(contact.getMiddleName(),contact1.getMiddleName());
        assertEquals(contact.getLastName(),contact1.getLastName());
        assertEquals(contact.getBirthday(),contact1.getBirthday());
        assertEquals(contact.getBody(),contact1.getBody());
        assertEquals(contact.getCompany(),contact1.getCompany());
        assertEquals(contact.getTitle(),contact1.getTitle());
        assertEquals(contact.getNickName(),contact1.getNickName());
        assertEquals(contact.getSuffix(),contact1.getSuffix());
//        assertEquals(contact.getBorqsName(),contact1.getBorqsName());

        //assert phones
        List<ContactItem> contactPhones = contact.getTelephones();
        List<ContactItem> contact1Phones = contact1.getTelephones();
        assertNotNull(contactPhones);
        assertNotNull(contact1Phones);
        assertTrue(contactPhones.size() == contact1.getTelephones().size());
        
        for(ContactItem contactPhone:contactPhones){
            boolean found = false;
            int type = contactPhone.getType();
            String contactValue = contactPhone.getValue();
            String contact1Value = null;
            for(ContactItem contact1Phone:contact1Phones){
                if(contact1Phone.getType() == type){
                    found = true;
                    contact1Value = contact1Phone.getValue();
                    break;
                }
            }
            assertTrue(found);
            assertEquals(contactValue,contact1Value);
        }

        //assert ims
        List<ContactItem> contactIMs = contact.getIms();
        List<ContactItem> contact1IMs = contact1.getIms();
        assertNotNull(contactIMs);
        assertNotNull(contact1IMs);
        assertTrue(contactIMs.size() == contact1IMs.size());

        for(ContactItem contactIM:contactIMs){
            boolean found = false;
            int type = contactIM.getType();
            String contactValue = contactIM.getValue();
            String contact1Value = null;
            for(ContactItem contact1IM:contact1IMs){
                if(contact1IM.getType() == type){
                    found = true;
                    contact1Value = contact1IM.getValue();
                    break;
                }
            }
            assertTrue(found);
            assertEquals(contactValue,contact1Value);
        }

        //assert emails
        List<ContactItem> contactEmails = contact.getEmails();
        List<ContactItem> contact1Emails = contact1.getEmails();
        assertNotNull(contactEmails);
        assertNotNull(contact1Emails);
        assertTrue(contactEmails.size() == contact1Emails.size());

        for(ContactItem contactEmail:contactEmails){
            boolean found = false;
            int type = contactEmail.getType();
            String contactValue = contactEmail.getValue();
            String contact1Value = null;
            for(ContactItem contact1Email:contact1Emails){
                if(contact1Email.getType() == type){
                    found = true;
                    contact1Value = contact1Email.getValue();
                    break;
                }
            }
            assertTrue(found);
            assertEquals(contactValue,contact1Value);
        }

        //assert webs
        List<ContactItem> contactWebs = contact.getWebpages();
        List<ContactItem> contact1Webs = contact1.getWebpages();
        assertNotNull(contactWebs);
        assertNotNull(contact1Webs);
        assertTrue(contactWebs.size() == contact1Webs.size());

        for(ContactItem contactWeb:contactWebs){
            boolean found = false;
            int type = contactWeb.getType();
            String contactValue = contactWeb.getValue();
            String contact1Value = null;
            for(ContactItem contact1Web:contact1Webs){
                if(contact1Web.getType() == type){
                    found = true;
                    contact1Value = contact1Web.getValue();
                    break;
                }
            }
            assertTrue(found);
            assertEquals(contactValue,contact1Value);
        }

        //assert address
        List<Address> contactAddresses = contact.getAddress();
        List<Address> contact1Addresses = contact1.getAddress();
        assertNotNull(contactAddresses);
        assertNotNull(contact1Addresses);
        assertTrue(contactAddresses.size() == contact1Addresses.size());

        for(Address contactAddr:contactAddresses){
            boolean found = false;
            int type = contactAddr.getType();
            String contactValue = contactAddr.toValue();
            String contact1Value = null;
            for(Address contact1Addr:contact1Addresses){
                if(contact1Addr.getType() == type){
                    found = true;
                    contact1Value = contact1Addr.toValue();
                    break;
                }
            }
            assertTrue(found);
            assertEquals(contactValue,contact1Value);
        }

        //assert xtag
        List<ContactItem> contactXTags = contact.getXTags();
        List<ContactItem> contact1XTags = contact1.getXTags();
        assertNotNull(contactXTags);
        assertNotNull(contact1XTags);
        assertTrue(contactXTags.size() == contact1XTags.size());

        for(ContactItem contactXTag:contactXTags){
            boolean found = false;
            int type = contactXTag.getType();
            String contactValue = contactXTag.getValue();
            String contact1Value = null;
            for(ContactItem contact1XTag:contact1XTags){
                if(contact1XTag.getType() == type){
                    found = true;
                    contact1Value = contact1XTag.getValue();
                    break;
                }
            }
            assertTrue(found);
            assertEquals(contactValue,contact1Value);
        }

    }
}
