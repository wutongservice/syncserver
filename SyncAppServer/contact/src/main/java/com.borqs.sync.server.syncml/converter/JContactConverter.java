package com.borqs.sync.server.syncml.converter;

import com.borqs.pim.jcontact.*;
import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.providers.Photo;
import com.borqs.sync.server.common.util.Utility;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/28/12
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class JContactConverter {

    public static final TypeMatcher mTypeMatcher = new TypeMatcher();

    // jContact x tag
    public static final String X_TAG_PHONETIC_FIRST_NAME = "X-PHONETIC-FIRST-NAME";
    public static final String X_TAG_PHONETIC_MIDDLE_NAME = "X-PHONETIC-MIDDLE-NAME";
    public static final String X_TAG_PHONETIC_LAST_NAME = "X-PHONETIC-LAST-NAME";
    public static final String X_TAG_PREFIX_NAME = "X-PREFIX-NAME";
    public static final String X_TAG_BORQS_UID = "X-BORQS-UID";
    public static final String X_TAG_BORQS_NAME = "X-BORQS-NAME";
    public static final String X_TAG_ACCOUNT_TYPE = JXTag.X_ACCOUNT_TYPE;
    public static final String X_TAG_GROUP = JXTag.X_GROUPS;
    public static final String X_TAG_STARRED = JXTag.X_STARRED;
    public static final String X_TAG_BLOCK = JXTag.X_BLOCK;
    public static final String X_TAG_RINGTONG = JXTag.X_RINGTONG;

    public static final String X_TAG_IM_MSN = JIM.MSN;
    public static final String X_TAG_IM_GTALK = JIM.GOOGLE_TALK;
    public static final String X_TAG_IM_SKYPE = JIM.SKYPE;
    public static final String X_TAG_IM_AIM = JIM.AIM;
    public static final String X_TAG_IM_YAHOO = JIM.YAHOO;
    public static final String X_TAG_IM_ICQ = JIM.ICQ;
    public static final String X_TAG_IM_JABBER = JIM.JABBER;
    public static final String X_TAG_IM_NETMEETING = JIM.NETMEETING;
    public static final String X_TAG_IM_WIN_LIVE = JIM.WIN_LIVE;
    public static final String X_TAG_IM_QQ = JIM.QQ;


    static {
        // phone matcher
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER, JPhone.ASSISTANT,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER, JPhone.CALLBACK,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER, JPhone.CAR,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER,
                JPhone.COMPANY_MAIN, TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER, JPhone.HOME_FAX,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER, JPhone.WORK_FAX,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER, JPhone.HOME,
                TypeMatcher.TYPE_PHONE_MATCHER);
        // TODO
        // mTypeMatcher.addType(Phone.TYPE_ISDN, JPhone.ISDN,
        // TypeMatcher.TYPE_PHONE_MATCHER);
        // TODO Main number?
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER, JPhone.MAIN,
                TypeMatcher.TYPE_PHONE_MATCHER);
        // TODO mms number
        // mTypeMatcher.addType(Phone.TYPE_MMS, JPhone.MMS,
        // TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER, JPhone.MOBILE,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER, JPhone.OTHER,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER, JPhone.OTHER_FAX,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER, JPhone.PAGE,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER, JPhone.RADIO,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER, JPhone.TELEGRAPH,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER, JPhone.WORK,
                TypeMatcher.TYPE_PHONE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER, JPhone.WORK,
                TypeMatcher.TYPE_PHONE_MATCHER);

        // TODO TTY_TTD
        // TODO work mobile
        // TODO work pager
        // TODO ORG
        // mTypeMatcher.addType(Organization.TYPE_WORK, JORG.COMPANY,
        // TypeMatcher.TYPE_ORG_MATCHER);
        // mTypeMatcher.addType(Organization.TYPE_OTHER, JORG.OTHER,
        // TypeMatcher.TYPE_ORG_MATCHER);
        // TODO email matcher
        // TODO :FIELD_INSTANT_MESSENGER-emailType
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS, JEMail.WORK,
                TypeMatcher.TYPE_EMAIL_MATCHER);
        // mTypeMatcher.addType(FIELD_EMAIL_3_ADDRESS, JEMail.MOBILE,
        // TypeMatcher.TYPE_EMAIL_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS, JEMail.HOME,
                TypeMatcher.TYPE_EMAIL_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS, JEMail.OTHER,
                TypeMatcher.TYPE_EMAIL_MATCHER);

        // TODO address matcher
        mTypeMatcher.addType(Address.ADDRESS_TYPE_HOME, JAddress.HOME,
                TypeMatcher.TYPE_ADDRESS_MATCHER);
        mTypeMatcher.addType(Address.ADDRESS_TYPE_WORK, JAddress.WORK,
                TypeMatcher.TYPE_ADDRESS_MATCHER);
        mTypeMatcher.addType(Address.ADDRESS_TYPE_OTHER, JAddress.OTHER,
                TypeMatcher.TYPE_ADDRESS_MATCHER);

        // website matcher
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE, JWebpage.HOMEPAGE,
                TypeMatcher.TYPE_WEBSITE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE, JWebpage.HOME,
                TypeMatcher.TYPE_WEBSITE_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE, JWebpage.WORK,
                TypeMatcher.TYPE_WEBSITE_MATCHER);

        //xtag
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_BLOCK, X_TAG_BLOCK,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_ACCOUNT_TYPE, X_TAG_ACCOUNT_TYPE,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_GROUP, X_TAG_GROUP,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_BORQS_NAME, X_TAG_BORQS_NAME,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_PHONETIC_FIRST_NAME, X_TAG_PHONETIC_FIRST_NAME,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_PHONETIC_MIDDLE_NAME, X_TAG_PHONETIC_MIDDLE_NAME,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_PHONETIC_LAST_NAME, X_TAG_PHONETIC_MIDDLE_NAME,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_PREFIX_NAME, X_TAG_PREFIX_NAME,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_STARRED, X_TAG_STARRED,
                TypeMatcher.TYPE_XTAG_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_RINGTONG, X_TAG_RINGTONG,
                TypeMatcher.TYPE_XTAG_MATCHER);

        //im
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_MSN, X_TAG_IM_MSN,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_GTALK, X_TAG_IM_GTALK,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_SKYPE, X_TAG_IM_SKYPE,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_AIM, X_TAG_IM_AIM,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_YAHOO, X_TAG_IM_YAHOO,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_ICQ, X_TAG_IM_ICQ,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_JABBER, X_TAG_IM_JABBER,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_NETMEETING, X_TAG_IM_NETMEETING,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.TYPE_X_TAG_IM_WIN_LIVE, X_TAG_IM_WIN_LIVE,
                TypeMatcher.TYPE_IM_MATCHER);
        mTypeMatcher.addType(ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER, X_TAG_IM_QQ,
                TypeMatcher.TYPE_IM_MATCHER);

    }

    public static TypeMatcher getTypeMatcher(){
        return mTypeMatcher;
    }

    public static Contact toContact(String contactJson) {
        JContact jContact = JContact.fromJsonString(contactJson);
        Contact contact = new Contact();
        if (jContact != null) {
            toContactName(contact, jContact);
            toContactPhoto(contact, jContact);
            toContactPhone(contact, jContact);
            toContactEmail(contact, jContact);
            toContactAddress(contact, jContact);
            toContactOrg(contact, jContact);
            toContactWeb(contact, jContact);
            toContactOthers(contact, jContact);
            toContactIM(contact,jContact);
            toContactXTag(contact, jContact);
        }
        return contact;
    }

    public static void toContactIM(Contact contact, JContact jContact){
        List<JContact.TypedEntity> imList = jContact.getIMList();
        if(imList != null){
            for(JContact.TypedEntity im:imList){
                if(im == null || Utility.isEmpty(String.valueOf(im.getValue()))){
                    return;
                }
                contact.addIm(new ContactItem(String.valueOf(im.getValue()),mTypeMatcher.matchContactType(im.getType()
                ,ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER,TypeMatcher.TYPE_IM_MATCHER),true,0));
            }
        }
    }

    public static void toContactOthers(Contact contact, JContact jContact) {
        if(!Utility.isEmpty(jContact.getNote())){
            contact.setBody(jContact.getNote());
        }

        if(!Utility.isEmpty(jContact.getBirthday())){
            contact.setBirthday(jContact.getBirthday());
        }

    }

    public static void toContactName(Contact contact, JContact jContact) {
        contact.setFirstName(jContact.getFirstName());
        contact.setMiddleName(jContact.getMiddleName());
        contact.setLastName(jContact.getLastName());
        contact.setNickName(jContact.getNickName());
        contact.setSuffix(jContact.getNamePostfix());

        if(!Utility.isEmpty(jContact.getFirstNamePinyin())){
            contact.addXTag(new ContactItem(jContact.getFirstNamePinyin(),ContactItem.TYPE_X_TAG_PHONETIC_FIRST_NAME,true,0));
        }

        if(!Utility.isEmpty(jContact.getMiddleNamePinyin())){
            contact.addXTag(new ContactItem(jContact.getMiddleNamePinyin(),ContactItem.TYPE_X_TAG_PHONETIC_MIDDLE_NAME,true,0));
        }

        if(!Utility.isEmpty(jContact.getLastNamePinyin())){
            contact.addXTag(new ContactItem(jContact.getLastNamePinyin(),ContactItem.TYPE_X_TAG_PHONETIC_LAST_NAME,true,0));
        }

        if(!Utility.isEmpty(jContact.getNamePrefix())){
            contact.addXTag(new ContactItem(jContact.getNamePrefix(),ContactItem.TYPE_X_TAG_PREFIX_NAME,true,0));
        }
    }

    public static void toContactAddress(Contact contact, JContact jContact) {
        List<JContact.TypedEntity> addressList = jContact.getAddressList();
        List<Address> addresses = new ArrayList<Address>();
        for (JContact.TypedEntity address : addressList) {
            if (address == null
                    || address.getValue() == null
                    || (
                    Utility.isEmpty(JAddress.city(address.getValue()))
                            && Utility.isEmpty(JAddress.zipcode(address.getValue()))
                            && Utility.isEmpty(JAddress.street(address.getValue()))
                            && Utility.isEmpty(JAddress.province(address.getValue()))
                        )
                    ) {
                continue;
            }

            Address.BUILDER builder = new Address.BUILDER();
            builder.setCity(JAddress.city(address.getValue()));
            builder.setPostalCode(JAddress.zipcode(address.getValue()));
            builder.setStreet(JAddress.street(address.getValue()));
            builder.setState(JAddress.province(address.getValue()));
            builder.setType(mTypeMatcher.matchContactType(address.getType(),
                    Address.ADDRESS_TYPE_OTHER, TypeMatcher.TYPE_ADDRESS_MATCHER));

            addresses.add(builder.build());
        }
        contact.setAddress(addresses);
    }

    public static void toContactOrg(Contact contact, JContact jContact) {
        List<JContact.TypedEntity> orgList = jContact.getOrgList();
        if (orgList != null) {
            for (JContact.TypedEntity org : orgList) {
                if (org == null || (Utility.isEmpty(JORG.company(org.getValue()))
                && Utility.isEmpty(JORG.title(org.getValue())))) {
                    continue;
                }
//                if (JORG.WORK.equals(org.getType())) {
                    //only support one org,so use work
                    String company = JORG.company(org.getValue());
                    String title = JORG.title(org.getValue());
                    contact.setCompany(company);
                    contact.setTitle(title);
                break;
//                }
            }
        }
    }

    public static void toContactPhone(Contact contact, JContact jContact) {
        List<JContact.TypedEntity> phoneList = jContact.getPhoneList();
        if (phoneList != null) {
            for (JContact.TypedEntity phone : phoneList) {
                if (phone == null || Utility.isEmpty(String.valueOf(phone.getValue()))) {
                    continue;
                }
                contact.addTelephone(new ContactItem(String.valueOf(phone.getValue()),
                        mTypeMatcher.matchContactType(phone.getType(), ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER, TypeMatcher.TYPE_PHONE_MATCHER
                        ),true,0));
            }
        }
    }

    public static void toContactEmail(Contact contact, JContact jContact) {
        List<JContact.TypedEntity> emailList = jContact.getEmailList();
        if (emailList != null) {
            for (JContact.TypedEntity email : emailList) {
                if (email == null || Utility.isEmpty(String.valueOf(email.getValue()))) {
                    continue;
                }
                contact.addEmail(new ContactItem(String.valueOf(email.getValue()),
                        mTypeMatcher.matchContactType(email.getType(), ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS, TypeMatcher.TYPE_EMAIL_MATCHER),true,0));
            }
        }
    }

    public static void toContactWeb(Contact contact, JContact jContact) {
        List<JContact.TypedEntity> webpageList = jContact.getWebpageList();
        if (webpageList != null) {
            for (JContact.TypedEntity webpage : webpageList) {
                if (webpage == null || Utility.isEmpty(String.valueOf(webpage.getValue()))) {
                    continue;
                }
                contact.addWebpage(new ContactItem(String.valueOf(webpage.getValue()),
                        mTypeMatcher.matchContactType(webpage.getType(), ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE, TypeMatcher.TYPE_WEBSITE_MATCHER),true,0));
            }
        }
    }

    public static void toContactPhoto(Contact contact, JContact jContact) {
        byte[] photo = jContact.getPhoto();
        if(photo != null){
            contact.setPhotoType(Photo.PHOTO_IMAGE);
            contact.setPhoto(new Photo(0, getImageType(photo), photo, null));
        }
    }

    private static String getImageType(byte[] image) {

        String type = null;
        MemoryCacheImageInputStream mcis = null;

        mcis = new MemoryCacheImageInputStream(new ByteArrayInputStream(image));

        Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);

        while (itr.hasNext()) {
            ImageReader reader = (ImageReader)  itr.next();
            String imageReaderName = reader.getClass().getSimpleName();
            if ("GIFImageReader".equals(imageReaderName)) {
                type = "GIF";
            } else if ("JPEGImageReader".equals(imageReaderName)) {
                type = "JPEG";
            } else if ("PNGImageReader".equals(imageReaderName)) {
                type = "PNG";
            } else if ("BMPImageReader".equals(imageReaderName)) {
                type = "BMP";
            }
        }

        return type;
    }

    public static void toContactXTag(Contact contact, JContact jContact) {
        List<JContact.TypedEntity> xtags = jContact.getXTags();
        if (xtags != null) {
            for (JContact.TypedEntity xtag : xtags) {
                if(xtag == null || Utility.isEmpty(String.valueOf(xtag.getValue()))){
                    continue;
                }
                //only borqsid and borqsName should to set contact normal field,others should be xtag
                if(X_TAG_BORQS_UID.equals(xtag.getType())){
                    contact.setBorqsId(String.valueOf(xtag.getValue()));
                }else if(X_TAG_BORQS_NAME.equals(xtag.getType())){
                    contact.setBorqsName(String.valueOf(xtag.getValue()));
                }else{
                    contact.addXTag(new ContactItem(String.valueOf(xtag.getValue()),
                            mTypeMatcher.matchContactType(xtag.getType(), ContactItem.CONTACT_ITEM_TYPE_UNDEFINED, TypeMatcher.TYPE_XTAG_MATCHER),true,0));
                }
            }
        }
    }


    public static String toContactJson(Contact contact){
        JContactBuilder builder = new JContactBuilder();
        toContactNameJson(builder,contact);
        toContactPhotoJson(builder,contact);
        toContactPhoneJson(builder,contact);
        toContactEmailJson(builder,contact);
        toContactAddresslJson(builder,contact);
        toContactOrgJson(builder,contact);
        toContactWebJson(builder,contact);
        toContactIMJson(builder,contact);
        toContactOthersJson(builder,contact);
        toContactXTagJson(builder,contact);
        return builder.createJson();
    }

    public static void toContactOthersJson(JContactBuilder builder,Contact contact){
        if(!Utility.isEmpty(contact.getBorqsId())){
            builder.addXTag(X_TAG_BORQS_UID,contact.getBorqsId()) ;
        }
        if(!Utility.isEmpty(contact.getBirthday())){
            builder.setBirthday(contact.getBirthday());
        }
        if(!Utility.isEmpty(contact.getBody())){
            builder.setNote(contact.getBody());
        }
        if(!Utility.isEmpty(contact.getBorqsName())){
            builder.addXTag(X_TAG_BORQS_NAME,contact.getBorqsName());
        }
    }

    public static void toContactXTagJson(JContactBuilder builder,Contact contact){
        //except firstnamepinyin,middlenamepinyin,lastnamepinyin,prefixname
        List<ContactItem> xtags = contact.getXTags();
        if(xtags != null){
            for(ContactItem item:xtags){
                if(item != null && !Utility.isEmpty(item.getValue())
                        && item.getType() != ContactItem.TYPE_X_TAG_PHONETIC_FIRST_NAME
                        && item.getType() != ContactItem.TYPE_X_TAG_PHONETIC_MIDDLE_NAME
                        && item.getType() != ContactItem.TYPE_X_TAG_PHONETIC_LAST_NAME
                        && item.getType() != ContactItem.TYPE_X_TAG_PREFIX_NAME){
                    String type = mTypeMatcher.matchJContactType(item.getType(),null,TypeMatcher.TYPE_XTAG_MATCHER);
                    if(!Utility.isEmpty(type)){
                        builder.addXTag(type,String.valueOf(item.getValue()));
                    }
                }
            }
        }

    }

    public static void toContactIMJson(JContactBuilder builder,Contact contact){
        List<ContactItem> ims = contact.getIms();
        if(ims != null){
            for(ContactItem im:ims){
                if(im != null && !Utility.isEmpty(im.getValue())){
                    builder.addIM(mTypeMatcher.matchJContactType(im.getType(),JIM.QQ,TypeMatcher.TYPE_IM_MATCHER)
                            ,String.valueOf(im.getValue()));
                }
            }
        }
    }

    public static void toContactWebJson(JContactBuilder builder,Contact contact){
        List<ContactItem> webs = contact.getWebpages();
        if(webs != null){
            for(ContactItem web:webs){
                if(web != null && !Utility.isEmpty(web.getValue())){
                    builder.addWebpage(mTypeMatcher.matchJContactType(web.getType(),
                            JWebpage.HOMEPAGE,TypeMatcher.TYPE_WEBSITE_MATCHER),String.valueOf(web.getValue()));
                }
            }
        }
    }
    
    public static void toContactOrgJson(JContactBuilder builder,Contact contact){
        String company = contact.getCompany();
        String title = contact.getTitle();
        if(Utility.isEmpty(company) && Utility.isEmpty(title)){
             return;
        }
        builder.addOrg(JORG.WORK,company,title);
    }

    public static void toContactAddresslJson(JContactBuilder builder, Contact contact) {
        //type, street, city,  province,  zipcode
        List<Address> addresses = contact.getAddress();
        if (addresses != null) {
            for (Address address : addresses) {
                String street = Utility.isEmpty(address.getStreet())?"":address.getStreet();
                String city = Utility.isEmpty(address.getCity())?"":address.getCity();
                String state = Utility.isEmpty(address.getState())?"":address.getState();
                String postalCode = Utility.isEmpty(address.getPostalCode())?"":address.getPostalCode();

                if (address == null || (Utility.isEmpty(street) && Utility.isEmpty(city)
                        && Utility.isEmpty(state) && Utility.isEmpty(postalCode))) {
                    continue;
                }
                builder.addAddress(mTypeMatcher.matchJContactType(address.getType(), JAddress.OTHER, TypeMatcher.TYPE_ADDRESS_MATCHER),
                        street, city, state, postalCode);
            }
        }
    }

    public static void toContactEmailJson(JContactBuilder builder,Contact contact){
        List<ContactItem> emails = contact.getEmails();
        if(emails != null){
            for(ContactItem email:emails){
                if(email != null && !Utility.isEmpty(email.getValue())){
                    builder.addEmail(mTypeMatcher.matchJContactType(email.getType(),
                            JEMail.OTHER,TypeMatcher.TYPE_EMAIL_MATCHER),String.valueOf(email.getValue()),false);
                }
            }
        }
    }

    public static void toContactPhoneJson(JContactBuilder builder,Contact contact){
        List<ContactItem> phones = contact.getTelephones();
        if(phones != null){
            for(ContactItem phone:phones){
                if(phone != null && !Utility.isEmpty(phone.getValue())){
                    builder.addPhone(mTypeMatcher.matchJContactType(phone.getType(),
                            JPhone.OTHER,TypeMatcher.TYPE_PHONE_MATCHER),String.valueOf(phone.getValue()),false);
                }
            }
        }
    }

    public static void toContactPhotoJson(JContactBuilder builder,Contact contact){
        Photo photo = contact.getPhoto();
        if(photo != null && photo.getImage() != null){
            builder.setPhoto(photo.getImage());
        }
    }

    public static void toContactNameJson(JContactBuilder builder,Contact contact){
        builder.setFirstName(contact.getFirstName(),getPhoneticFirstName(contact));
        builder.setMiddleName(contact.getMiddleName(),getPhoneticMiddleName(contact));
        builder.setLastName(contact.getLastName(),getPhoneticLastName(contact));
        builder.setNamePostfix(contact.getSuffix());
        builder.setNamePrefix(getNamePrefix(contact));
        builder.setNickName(contact.getNickName());
    }

    private static String getPhoneticFirstName(Contact contact){
        List<ContactItem> xtags = contact.getXTags();
        if(xtags != null){
            for(ContactItem item:xtags){
                if(item != null && !Utility.isEmpty(item.getValue())
                        && item.getType() == ContactItem.TYPE_X_TAG_PHONETIC_FIRST_NAME){
                    return String.valueOf(item.getValue());
                }
            }
        }
        return null;
    }

    private static String getPhoneticMiddleName(Contact contact){
        List<ContactItem> xtags = contact.getXTags();
        if(xtags != null){
            for(ContactItem item:xtags){
                if(item != null && !Utility.isEmpty(item.getValue())
                        && item.getType() == ContactItem.TYPE_X_TAG_PHONETIC_MIDDLE_NAME){
                    return String.valueOf(item.getValue());
                }
            }
        }
        return null;
    }

    private static String getPhoneticLastName(Contact contact){
        List<ContactItem> xtags = contact.getXTags();
        if(xtags != null){
            for(ContactItem item:xtags){
                if(item != null && !Utility.isEmpty(item.getValue())
                        && item.getType() == ContactItem.TYPE_X_TAG_PHONETIC_LAST_NAME){
                    return String.valueOf(item.getValue());
                }
            }
        }
        return null;
    }

    private static String getNamePrefix(Contact contact){
        List<ContactItem> xtags = contact.getXTags();
        if(xtags != null){
            for(ContactItem item:xtags){
                if(item != null && !Utility.isEmpty(item.getValue())
                        && item.getType() == ContactItem.TYPE_X_TAG_PREFIX_NAME){
                    return String.valueOf(item.getValue());
                }
            }
        }
        return null;
    }

}
