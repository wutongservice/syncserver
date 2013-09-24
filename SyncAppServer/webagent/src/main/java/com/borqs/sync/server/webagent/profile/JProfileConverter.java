package com.borqs.sync.server.webagent.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.borqs.sync.server.syncml.converter.TypeMatcher;
import com.borqs.sync.server.syncml.converter.JContactConverter;
import com.borqs.pim.jcontact.JAddress;
import com.borqs.pim.jcontact.JEMail;
import com.borqs.pim.jcontact.JIM;
import com.borqs.pim.jcontact.JPhone;
import com.borqs.pim.jcontact.JWebpage;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.util.Utility;
import com.borqs.sync.server.webagent.util.TextUtil;

public class JProfileConverter {    
    private static final String TYPE = "type";
    private static final String INFO = "info";
    private static final String LABEL = "label";
    private static final String PRIMARY = "primary";
    //private static final String CUSTOME = "x-";
    private static final String CUSTOME = "other";
    
    private static final String EMPTY_STR = "";
    
    public static final TypeMatcher mTypeMatcher = JContactConverter.getTypeMatcher();

    /**
     * convert server json string to Contact struct
     * @param jsonObj
     * @return
     * @throws JSONException
     */
    public static Contact toProfileStruct(JSONObject jsonObj) throws JSONException{        
        int type = 0;
        Contact profile = new Contact();        
        
        //name        
        JSONObject name = jsonObj.optJSONObject("name");
        if (name != null){
            if(!TextUtil.isEmpty(name.optString("first"))) {
                profile.setFirstName(name.optString("first"));
            }
            if(!TextUtil.isEmpty(name.optString("middle"))) {
                profile.setMiddleName(name.optString("middle"));
            }
            if(!TextUtil.isEmpty(name.optString("last"))) {
                profile.setLastName(name.optString("last"));
            }
        }        
        // nickname        
        profile.setNickName(jsonObj.optString("nickname"));        
        // display name        
        profile.setDisplayName(jsonObj.optString("display_name"));
        
        // phone
        JSONArray phoneList = jsonObj.optJSONArray("tel");
        if (phoneList != null){
            for (int i = 0; i < phoneList.length(); i++){
                JSONObject phone = phoneList.getJSONObject(i);
                type = getContactType(phone.optString(TYPE),
                                    ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER,
                                    TypeMatcher.TYPE_PHONE_MATCHER);
                profile.addTelephone(new ContactItem(phone.getString(INFO), type, true, 0));
            }
        }
        
        // email
        JSONArray emailList = jsonObj.optJSONArray("email");
        if (emailList != null){
            for (int i = 0; i < emailList.length(); i++){
                JSONObject email = emailList.getJSONObject(i);
                type = getContactType(email.optString(TYPE),
                                    ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS,
                                    TypeMatcher.TYPE_EMAIL_MATCHER);
                profile.addEmail(new ContactItem(email.optString(INFO), type, true, 0));
            }
        }
        
        // birthday
        JSONArray dateList = jsonObj.optJSONArray("date");
        if (dateList != null){
            for (int i = 0; i < dateList.length(); i++){
                JSONObject date = dateList.getJSONObject(i);            
                if (date.optString(TYPE).equals("birthday")){
                    profile.setBirthday(date.optString(INFO));
                    break;
                }
            }
        }
        
        // organization
        JSONArray orgList = jsonObj.optJSONArray("organization");
        if (orgList != null){
            for (int i = 0; i < orgList.length(); i++){
                JSONObject org = orgList.getJSONObject(i);                
                profile.setCompany(org.optString("company"));
                profile.setDepartment(org.optString("department"));
                break;
            }
        }
        
        // address
        JSONArray addrList = jsonObj.optJSONArray("address");            
        if (addrList != null){            
            List<Address> addresses = new ArrayList<Address>();
            for (int i = 0; i < addrList.length(); i++){                
                JSONObject addr = addrList.getJSONObject(i);
                type = getContactType(addr.optString(TYPE),
                                    Address.ADDRESS_TYPE_OTHER,
                                    TypeMatcher.TYPE_ADDRESS_MATCHER);
                
                Address.BUILDER builder = new Address.BUILDER();
                builder.setCity(JAddress.city(addr.optString("city")));
                builder.setPostalCode(JAddress.zipcode(addr.optString("zip_code")));
                builder.setStreet(JAddress.street(addr.optString("street")));
                builder.setState(JAddress.province(addr.optString("province")));
                builder.setType(type);

                addresses.add(builder.build());
            }
            profile.setAddress(addresses);            
        }
        
        // im
        JSONArray imList = jsonObj.optJSONArray("im");
        if (imList != null){
            for (int i = 0; i < imList.length(); i++){
                JSONObject im = imList.getJSONObject(i);
                type = getContactType(im.optString(TYPE), 
                                    ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER, 
                                    TypeMatcher.TYPE_IM_MATCHER);
                profile.addIm(new ContactItem(im.optString(INFO), type,true,0));;
            }
        }
        
        // website
        JSONArray websiteList = jsonObj.optJSONArray("url");
        if (websiteList != null){
            for (int i = 0; i < websiteList.length(); i++){
                JSONObject website = websiteList.getJSONObject(i);
                type = getContactType(website.optString(TYPE), 
                                        ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE, 
                                        TypeMatcher.TYPE_WEBSITE_MATCHER);
                profile.addWebpage(new ContactItem(website.optString(INFO), type,true,0));;
            }
        }
        
        // photo
        JSONObject photo = jsonObj.optJSONObject("photo");
        if (photo != null){ // a tricky, only save a url
            //TODO: when Contact struct changed, you must avoid manager not used...
            profile.setManager(photo.optString("large_url"));              
        }
        
        return profile;
    }
    
    public static int getContactType(String profileType, int defaultType, int typeMatcher){
        int type = defaultType;
        if (typeMatcher == TypeMatcher.TYPE_PHONE_MATCHER){            
            if (profileType.equals("callback")){
                profileType = JPhone.CALLBACK;
            } else if (profileType.equals("company_main")){
                profileType = JPhone.COMPANY_MAIN;
            } else if (profileType.equals("fax_home")){
                profileType = JPhone.HOME_FAX;
            } else if (profileType.equals("fax_work")){
                profileType = JPhone.WORK_FAX;
            } else if (profileType.equals("other_fax")){
                profileType = JPhone.OTHER_FAX;
            } else if (profileType.equals("pager")){
                profileType = JPhone.PAGE;
            } else if (profileType.equals("telex")){
                profileType = JPhone.TELEGRAPH;
            } else if (profileType.equals("tty_ttd")){
                profileType = JPhone.TTY_TDD;
            } else if (profileType.equals("work_mobile")){
                profileType = JPhone.WORK_MOBILE;
            } else if (profileType.equals("work_pager")){
                profileType = JPhone.WORK_PAGE;
            }
        } else if (typeMatcher == TypeMatcher.TYPE_IM_MATCHER){            
            if (profileType.equals("google_talk")){
                profileType = JIM.GOOGLE_TALK;
            } else if (profileType.equals("netmeeting")){
                profileType = JIM.NETMEETING;
            } 
        } 
        profileType = profileType.toUpperCase();
        type = mTypeMatcher.matchContactType(profileType,defaultType, typeMatcher);
        return type;
    }
    
    public static String toContactJson(Contact contact){
        return JContactConverter.toContactJson(contact);
    }
    
    public static Contact toContactProfile(String jsonData){
        return JContactConverter.toContact(jsonData);
    }
    
    private static String toSafeString(String str){
        return (str==null)?EMPTY_STR:str;
    }

    /** server data type definition may be different with JContac definition
     * server data type
     * date.type: "anniversary" / "other" / "birthday" / x-"
     * telephone.type: "home" / "mobile" / "work" / "fax_work" / "fax_home" / "pager" / "other" / 
     *                 "callback" / "isdn" / "company_main" / "car" / "main" / "other_fax" / "radio" / 
     *                 "telex" / "tty_ttd" / "work_mobile" / "work_pager" / "assistant" / "mms" / "x-"
     * email.type: "home" / "work" / "mobile" / "other" / "x-"
     * im.type: "aim" / "msn" / "yahoo" / "skype" / "qq" / "google_talk" / "icq" / "jabber" / "netmeeting" / "x-"     
     * url.type: "homepage" / "blog" / "profile" / "home" / "work" / "ftp" / "other" / "x-"
     * organization.type: "work" / "other" / "x-"
     * address.type: "home" / "work" / "other" / "x-"
     */
    public static String handleSpeicalType(String type, int typeMatcher){
        String cType = type.toUpperCase();
        if (typeMatcher == TypeMatcher.TYPE_PHONE_MATCHER){            
            if (cType.equals(JPhone.CALLBACK)){
                type = "callback";
            } else if (cType.equals(JPhone.COMPANY_MAIN)){
                type = "company_main";
            } else if (cType.equals(JPhone.HOME_FAX)){
                type = "fax_home";
            } else if (cType.equals(JPhone.WORK_FAX)){
                type = "fax_work";
            } else if (cType.equals(JPhone.OTHER_FAX)){
                type = "other_fax";
            } else if (cType.equals(JPhone.PAGE)){
                type = "pager";
            } else if (cType.equals(JPhone.TELEGRAPH)){
                type = "telex";
            } else if (cType.equals(JPhone.TTY_TDD)){
                type = "tty_ttd";
            } else if (cType.equals(JPhone.WORK_MOBILE)){
                type = "work_mobile";
            } else if (cType.equals(JPhone.WORK_PAGE)){
                type = "work_pager";
            }
        } else if (typeMatcher == TypeMatcher.TYPE_IM_MATCHER){            
            if (cType.equals(JIM.GOOGLE_TALK)){
                type = "google_talk";
            } else if (cType.equals(JIM.NETMEETING)){
                type = "netmeeting";
            }
        }         
        return type;
    }
    
    public static Map<String,String> toParamMap(Contact profile) throws JSONException{        
        Map<String, String> params = new HashMap<String, String>();
        
        //name        
        params.put("name.first", toSafeString(profile.getFirstName()));
        params.put("name.middle", toSafeString(profile.getMiddleName()));
        params.put("name.last", toSafeString(profile.getLastName()));
        
        // nickname        
        params.put("nickname", toSafeString(profile.getNickName()));
        
        // display name        
        params.put("display_name", toSafeString(profile.getDisplayName()));
        
        // phone
        JSONArray jPhoneList = new JSONArray();
        List<ContactItem> phones = profile.getTelephones();
        if(phones != null){
            for(ContactItem phone:phones){
                if(phone != null){
                    JSONObject jPhone = new JSONObject();
                    String type = mTypeMatcher.matchJContactType(phone.getType(),
                                JPhone.OTHER, TypeMatcher.TYPE_PHONE_MATCHER);
                    jPhone.put(TYPE, handleSpeicalType(type.toLowerCase(), TypeMatcher.TYPE_PHONE_MATCHER));
                    jPhone.put(INFO, phone.getValue());
                    jPhone.put(PRIMARY, false);
                    
                    jPhoneList.put(jPhone);
                 } 
            }
        }
        params.put("tel", jPhoneList.toString());
        
        // email
        JSONArray jEmailList = new JSONArray();
        List<ContactItem> emails = profile.getEmails();
        if(emails != null){
            for(ContactItem email:emails){
                if(email != null){
                    JSONObject jEmail = new JSONObject();
                    String type = mTypeMatcher.matchJContactType(email.getType(),
                                    JEMail.OTHER, TypeMatcher.TYPE_EMAIL_MATCHER);
                    jEmail.put(TYPE, type.toLowerCase());
                    
                    jEmail.put(INFO, email.getValue());
                    jEmail.put(PRIMARY, false);
                    
                    jEmailList.put(jEmail);
                }
            }
        }
        params.put("email", jEmailList.toString());
                
        // birthday
        JSONArray jDateList = new JSONArray();        
        JSONObject jDate = new JSONObject();
        jDate.put(TYPE, "birthday");
        jDate.put(INFO, toSafeString(profile.getBirthday()));
        jDateList.put(jDate);
        params.put("date", jDateList.toString());
                
        // organization
        JSONArray jOrgList = new JSONArray();
        JSONObject jOrg = new JSONObject();
        jOrg.put(TYPE, CUSTOME);
        jOrg.put("company", toSafeString(profile.getCompany()));
        jOrg.put("title", EMPTY_STR);
        jOrg.put("department", toSafeString(profile.getDepartment()));
        jOrg.put("office_location", EMPTY_STR);
        jOrg.put("job_description", EMPTY_STR);
        jOrg.put("symbol", EMPTY_STR);
        jOrgList.put(jOrg);
        params.put("organization", jOrgList.toString());
        
        // address
        JSONArray jAddrList = new JSONArray();
        List<Address> addresses = profile.getAddress();
        if (addresses != null) {
            for (Address address : addresses) {
                String street = toSafeString(address.getStreet());
                String city = toSafeString(address.getCity());
                String state = toSafeString(address.getState());
                String postalCode = toSafeString(address.getPostalCode());

                JSONObject jAddr = new JSONObject();
                String type = mTypeMatcher.matchJContactType(address.getType(), 
                        JAddress.OTHER, TypeMatcher.TYPE_ADDRESS_MATCHER);
                jAddr.put(TYPE, type.toLowerCase());                    
                jAddr.put("street", street);
                jAddr.put("city", city);
                jAddr.put("province", state);
                jAddr.put("zip_code", postalCode);   
                
                jAddrList.put(jAddr);
            }
        }
        params.put("address", jAddrList.toString());
        
        // im
        JSONArray jIMlist = new JSONArray();
        List<ContactItem> ims = profile.getIms();
        if(ims != null){
            for(ContactItem im:ims){
                if(im != null){
                    JSONObject jIM = new JSONObject();
                    String type = mTypeMatcher.matchJContactType(im.getType(),
                            JIM.QQ, TypeMatcher.TYPE_IM_MATCHER);
                    jIM.put(TYPE, handleSpeicalType(type.toLowerCase(), TypeMatcher.TYPE_IM_MATCHER));
                    jIM.put(INFO, im.getValue());
                    jIMlist.put(jIM);
                }
            }
        }
        params.put("im", jIMlist.toString());
        
        // website
        JSONArray jWeblist = new JSONArray();
        List<ContactItem> webs = profile.getWebpages();
        if(webs != null){
            for(ContactItem web:webs){
                if(web != null){
                    JSONObject jWeb = new JSONObject();
                    String type = mTypeMatcher.matchJContactType(web.getType(),
                            JWebpage.HOMEPAGE,TypeMatcher.TYPE_WEBSITE_MATCHER);
                    jWeb.put(TYPE, type.toLowerCase());
                    jWeb.put(INFO, web.getValue());
                    jWeblist.put(jWeb);  
                }
            }
        }
        params.put("url", jWeblist.toString());
        
        // photo, in other functions
                
        return params;
    }
}
