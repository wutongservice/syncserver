/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.providers.Photo;
import com.borqs.sync.server.common.util.ReflectUtil;
import com.borqs.sync.server.common.util.Utility;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-2-26
 * Time: 上午11:41
 */
public final class ProfileRecord{
    private Logger mLogger;
    private boolean mIsContactInfoVisible;
    private Contact mData = new Contact();
    private SyncInfo mSyncInfo = new SyncInfo();
    public static ProfileRecord createFrom(Reader reader) throws Exception {
        JsonReader jreader = new JsonReader(reader);
        jreader.setLenient(true);
        return createFrom(jreader);
    }

    public static ProfileRecord createFrom(JsonReader reader) throws Exception {
        ProfileRecord record = new ProfileRecord();
//        try{
            record.parse(reader);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        return record;
    }
    
    public Contact asContact(){
        mData.setBorqsName(mData.getDisplayName());
        return mData;
    }

    public long getAddressLastUpdateTime(){
        return mSyncInfo.mAddressLastUpdate;
    }

    public long getBasicInfoLastUpdateTime(){
        return mSyncInfo.mBasicLastUpdate;
    }

    public long getContactInfoLastUpdateTime(){
        return mSyncInfo.mContactInfoLastUpdate;
    }

    public long getProfileLastUpdateTime(){
        return mSyncInfo.mProfileLastUpdate;
    }
    public boolean isContactInfoVisible(){
        return mIsContactInfoVisible;
    }

    private void parse(JsonReader reader) throws IOException {
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(ProfileField.USER_ID.equals(name)){
                mData.setBorqsId(reader.nextString());
            } else if(ProfileField.PROFILE_PRIVACY.equals(name)){
                mIsContactInfoVisible = !reader.nextBoolean();
            } else if(SyncInfo.is(name)){
                mSyncInfo.setTo(name, reader.nextLong());
            } else if(BaseInfo.is(name)){
                new BaseInfo(name, reader.nextString()).setTo(mData);
            } else if(PhotoInfo.is(name)){
                new PhotoInfo(reader.nextString()).setTo(mData);
            } else if(AddressInfo.is(name)){
                new AddressInfo(reader).setTo(mData);
            } else if(ContactInfo.is(name)){
                new ContactInfo(reader).setTo(mData);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private void logD(String msg){
    }
    
    //Helper class to parse/save the base info
    private final static class BaseInfo{
        private String mKey;
        private String mValue;

        public static boolean is(String name){
            return sInfoMap.containsKey(name);
        }
        
        public BaseInfo(String key, String value){
            mKey = key;
            mValue = value;
        }

        public void setTo(Contact c){
            String methodName = sInfoMap.get(mKey);
            if(methodName != null){
                ReflectUtil.invoke(c, methodName, mValue);
            }
        }

        private final static HashMap<String, String> sInfoMap = new HashMap<String, String>();
        static{
            sInfoMap.put(ProfileField.BIRTHDAY, "setBirthday");
            sInfoMap.put(ProfileField.BODY, "setBody");
            sInfoMap.put(ProfileField.COMPANY, "setCompany");
            sInfoMap.put(ProfileField.WEBPAGE, null);
            sInfoMap.put(ProfileField.FIRSTNAME, "setBFirstName");
            sInfoMap.put(ProfileField.MIDDLENAME, "setBMiddleName");
            sInfoMap.put(ProfileField.LASTNAME, "setBLastName");
            sInfoMap.put(ProfileField.LANGUAGES, "setLanguages");
            sInfoMap.put(ProfileField.JOB_TITLE, "setJobTitle");
            sInfoMap.put(ProfileField.HOBBIES, "setHobbies");
            sInfoMap.put(ProfileField.GENDER, "setGender");
            sInfoMap.put(ProfileField.DISPLAYNAME, "setDisplayName");
            sInfoMap.put(ProfileField.DEPARTMENT, "setDepartment");
            sInfoMap.put(ProfileField.OFFICE_LOCATION, "setOfficeLocation");
            sInfoMap.put(ProfileField.PROFESSION, "setProfession");
            sInfoMap.put(ProfileField.LOGIN_EMAIL1, "addLoginEmail");
            sInfoMap.put(ProfileField.LOGIN_EMAIL2, "addLoginEmail");
            sInfoMap.put(ProfileField.LOGIN_EMAIL3, "addLoginEmail");
            sInfoMap.put(ProfileField.LOGIN_PHONE1, "addLoginPhone");
            sInfoMap.put(ProfileField.LOGIN_PHONE2, "addLoginPhone");
            sInfoMap.put(ProfileField.LOGIN_PHONE3, "addLoginPhone");


        }
    };

    //Helper class to parse/save the photo info
    private final static class PhotoInfo{
        private String mPhotoUrl;
        public static final boolean is(String name){
            return ProfileField.PHOTO.equals(name);
        }

        public PhotoInfo(String url){
            mPhotoUrl = url;
        }

        public void setTo(Contact c){
            if(mPhotoUrl != null) {
                byte[] image = loadHttpImage(mPhotoUrl);
                if(image != null) {
                    String photoType = getImageType(image);

                    Photo photo = new Photo(-1, photoType, image, null);
                    c.setPhoto(photo);
                } else {
                    Photo photo = new Photo(-1, "", null, mPhotoUrl);
                    c.setPhoto(photo);
                }
            }
        }
        private byte[] loadHttpImage(String imageUrl) {
            URL photoUrl;
            InputStream pin = null;
            HttpURLConnection photoConn = null;
            byte[] data = null;
            try {
                photoUrl = new URL(imageUrl);
                photoConn = (HttpURLConnection) photoUrl.openConnection();
                pin = photoConn.getInputStream();
                int cl = photoConn.getContentLength();

                if(cl > 0) {
                    data = IOUtils.toByteArray(pin);
                }

            } catch (MalformedURLException e) {
//                if(logger != null) {
//                    logger.info("Account Manager INFO: exception when load image from URL " + e);
//                }
            } catch (IOException e) {
//                if(logger != null) {
//                    logger.info("Account Manager INFO: exception when load image from URL " + e);
//                }
            } finally {
                try {
                    if(pin != null) {
                        pin.close();
                    }
                } catch (IOException e) {
//                    if(logger != null) {
//                        logger.info("Account Manager INFO: exception when load image from URL " + e);
//                    }
                }

                if(photoConn != null) {
                    photoConn.disconnect();
                }
            }

//            if(logger != null) {
//                logger.info("Account Manager INFO: loaded: " + imageUrl + ", " + (data == null ? 0 : data.length));
//            }
            return data;
        }

        private String getImageType(byte[] image) {

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
    }

    //Helper class to parse/save the address info
    private final static class AddressInfo{
        private JsonReader mReader;
        public static final boolean is(String name){
            return ProfileField.ADDRESS.equals(name);
        }

        AddressInfo(JsonReader reader){
            mReader = reader;    
        }
        
        public void setTo(Contact c) throws IOException {
            List<Address> addresses = new ArrayList<Address>();
            mReader.beginArray();
            while(mReader.hasNext()){
                Address a = parseAddress();
                addresses.add(a);
//                if(a.getType() == Address.ADDRESS_TYPE_HOME){
//                    c.setHomeAddress(a);
//                } else if(a.getType() == Address.ADDRESS_TYPE_WORK){
//                    c.setWorkAddress(a);
//                } else if(a.getType() == Address.ADDRESS_TYPE_OTHER){
//                    c.setOtherAddress(a);
//                }
            }
            mReader.endArray();
            c.setAddress(addresses);
        }
        
        private Address parseAddress() throws IOException {
            Address.BUILDER builder = new Address.BUILDER();
            mReader.beginObject();
            while(mReader.hasNext()){
                String name = mReader.nextName();
                String value = mReader.nextString();
                if(ProfileField.ADDRESS_TYPE.equals(name)){ builder.setType(typeOf(value));}
                else if(ProfileField.ADDRESS_COUNTRY.equals(name)){builder.setCountry(value);}
                else if(ProfileField.ADDRESS_STATE.equals(name)) {builder.setState(value);}
                else if(ProfileField.ADDRESS_CITY.equals(name)) {builder.setCity(value);}
                else if(ProfileField.ADDRESS_STREET.equals(name)) {builder.setStreet(value);}
                else if(ProfileField.ADDRESS_CODE.equals(name)) {builder.setPostalCode(value);}
                else if(ProfileField.ADDRESS_PO_BOX.equals(name)) {builder.setPostOfficeAddress(value);}
                else if(ProfileField.ADDRESS_EXTENDED.equals(name)) {builder.setExtends(value);}
            }
            mReader.endObject();
            return builder.build();
        }

        private int typeOf(String typeInProfile){
            if (ProfileField.TYPE_ACCOUNT_ADDRESS_HOME.equalsIgnoreCase(typeInProfile)) {
                return Address.ADDRESS_TYPE_HOME;
            } else if(ProfileField.TYPE_ACCOUNT_ADDRESS_WORK.equalsIgnoreCase(typeInProfile)) {
                return Address.ADDRESS_TYPE_WORK;
            }
            return Address.ADDRESS_TYPE_OTHER;
        }
    }

    //Helper class to parse/save the contact info
    private final static class ContactInfo{
        private JsonReader mReader;
        public static final boolean is(String name){
            return ProfileField.CONTACT_INFO.equals(name);
        }

        ContactInfo(JsonReader reader){
            mReader = reader;
        }
        
        public void setTo(Contact c) throws IOException {
            mReader.beginObject();
            while(mReader.hasNext()){
                String name = mReader.nextName();
                String value = mReader.nextString();
                if(sInfoMap.containsKey(name) && !Utility.isEmpty(value)){
                    MethodType mt = sInfoMap.get(name);
                    ContactItem item = new ContactItem(value, mt.typeInDB,false,0);
                    ReflectUtil.invoke(c, mt.methodName, item);
                }
            }
            mReader.endObject();
        }
        
        private static final class MethodType{
            String methodName;
            int typeInDB;
            MethodType(String m, int t){
                methodName = m;
                typeInDB = t;
            }  
        };

        private final static HashMap<String, MethodType> sInfoMap = new HashMap<String, MethodType>();
        static{
            sInfoMap.put(ProfileField.CONTACT_EMAIL_ADDRESS, new MethodType("addEmail", ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS));
            sInfoMap.put(ProfileField.CONTACT_EMAIL_ADDRESS_2, new MethodType( "addEmail", ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS));
            sInfoMap.put(ProfileField.CONTACT_EMAIL_ADDRESS_3,  new MethodType("addEmail", ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS));

            sInfoMap.put(ProfileField.CONTACT_HOME_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_HOME_TEL_2,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_HOME_TEL_3,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER));

            sInfoMap.put(ProfileField.CONTACT_WORK_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_WORK_TEL_2,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_WORK_TEL_3,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER));

            sInfoMap.put(ProfileField.CONTACT_MOBILE_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_MOBILE_TEL_2,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_MOBILE_TEL_3,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER));

            sInfoMap.put(ProfileField.CONTACT_ASSISTANT_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_CALLBACK_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_CAR_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_COMPANY_MAIN_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER));

            sInfoMap.put(ProfileField.CONTACT_PRIMARY_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_RADIO_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_OTHER_TEL,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER));

            sInfoMap.put(ProfileField.CONTACT_TELEX_NUMBER,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_PAGER,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER));;

            sInfoMap.put(ProfileField.CONTACT_HOME_FAX,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_WORK_FAX,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER));
            sInfoMap.put(ProfileField.CONTACT_OTHER_FAX,  new MethodType("addTelephone", ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER));

            sInfoMap.put(ProfileField.CONTACT_IM_QQ,  new MethodType("addIm", ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER));

            sInfoMap.put(ProfileField.CONTACT_HOME_WEB_PAGE,  new MethodType("addWebpage", ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE));
            sInfoMap.put(ProfileField.CONTACT_WEB_PAGE,  new MethodType("addWebpage", ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE));
            sInfoMap.put(ProfileField.CONTACT_WORK_WEB_PAGE,  new MethodType("addWebpage", ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE));
        }
    }

    public final static class SyncInfo{
        public long mAddressLastUpdate;
        public long mBasicLastUpdate;
        public long mContactInfoLastUpdate;
        public long mProfileLastUpdate;

        
        public static boolean is(String name){
            return sInfoMap.containsKey(name);
        }

        public void setTo(String key, long value){
            String methodName = sInfoMap.get(key);
            if(methodName != null){
                ReflectUtil.invoke(this, methodName, value);
            }
        }
        public void setAddressLastUpdate(Long v){mAddressLastUpdate=v;}
        public void setBasicLastUpdate(Long v){mBasicLastUpdate=v;}
        public void setContactInfoLastUpdate(Long v){mContactInfoLastUpdate=v;}
        public void setProfileLastUpdate(Long v){mProfileLastUpdate=v;}
        private final static HashMap<String, String> sInfoMap = new HashMap<String, String>();
        static{
            sInfoMap.put(ProfileField.LAST_UPDATE_ADDRESS, "setAddressLastUpdate");
            sInfoMap.put(ProfileField.LAST_UPDATE_BASIC, "setBasicLastUpdate");
            sInfoMap.put(ProfileField.LAST_UPDATE_CONTACT_INFO, "setContactInfoLastUpdate");
            sInfoMap.put(ProfileField.LAST_UPDATE_PROFILE, "setProfileLastUpdate");
        }
    }
}
