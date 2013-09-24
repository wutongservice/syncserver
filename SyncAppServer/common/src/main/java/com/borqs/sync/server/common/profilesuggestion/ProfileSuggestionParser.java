package com.borqs.sync.server.common.profilesuggestion;

import com.borqs.sync.server.common.datamining.IntegrationProfileAddress;
import com.borqs.sync.server.common.datamining.IntegrationProfileItem;
import com.borqs.sync.server.common.datamining.IntegrationProfileName;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: b211 Date: 11/15/11 Time: 2:31 PM To change
 * profilesuggestion schema
 *
 * {
 *      "names":[{"first_name":"1","middle_name":"2","last_name":"3","count":1},{"first_name":"4","middle_name":"5","last_name":"6","count":1}]
 *      "phones":[{"type":1,"value":"10086","count":1},{"type":1,"value":"10086","count":1},{"type":1,"value":"10086","count":1}],
 *      "emails":[{"type":1,"value":"email1@email.com","count":1},{"type":1,"value":"email2@email.com","count":1}],
 *       "ims":[{"type":1,"value":"1111","count":1},{"type":1,"value":"222","count":1}],
 *       "webs":[{"type":1,"value":"www.google.com","count":1},{"type":1,"value":"www.baidu.com","count":1}],
 *       "addresses":[
 *       {"city":"beijing","street":"street1","country":"country1"
 *       ,"extendedAddress":"extendedAddress1","postalCode":"postalCode1","postOfficeAddress":"postOfficeAddress1"
 *       "state":"state1","count":1},
 *       {"city":"beijing2","street":"street2","country":"country2"
 *       ,"extendedAddress":"extendedAddress2","postalCode":"postalCode2","postOfficeAddress":"postOfficeAddress2"
 *       "state":"state2","count":1}]
 * }
 *
 */
public class ProfileSuggestionParser {

    public static final String CHANGE_REQUEST_CONTACT_NAME_FIRST_NAME_KEY = "first_name";
    public static final String CHANGE_REQUEST_CONTACT_NAME_MIDDLE_NAME_KEY = "middle_name";
    public static final String CHANGE_REQUEST_CONTACT_NAME_LAST_NAME_KEY = "last_name";
    public static final String CHANGE_REQUEST_CONTACT_PHONES_KEY = "phones";
    public static final String CHANGE_REQUEST_CONTACT_EMAILS_KEY = "emails";
    public static final String CHANGE_REQUEST_CONTACT_IMS_KEY = "ims";
    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_KEY = "addresses";
    public static final String CHANGE_REQUEST_CONTACT_WEBS_KEY = "webs";
    public static final String CHANGE_REQUEST_CONTACT_NAMES_KEY = "names";
    public static final String CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY = "count";
    public static final String CHANGE_REQUEST_CONTACT_ITEM_VALUE_KEY = "value";
    public static final String CHANGE_REQUEST_CONTACT_ITEM_TYPE_KEY = "type";

    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_CITY_KEY = "city";
    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_STATE_KEY = "state";
    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_POST_OFFICE_ADDRESS_KEY = "postOfficeAddress";
    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_POSTAL_CODE_KEY = "postalCode";
    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_EXTENDED_ADDRESS_KEY = "extendedAddress";
    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_COUNTRY_KEY = "country";
    public static final String CHANGE_REQUEST_CONTACT_ADDRESSES_STREET_KEY = "street";

    public static final int CHANGE_REQUEST_TYPE_PHONE = 1;
    public static final int CHANGE_REQUEST_TYPE_EMAIL = 2;
    public static final int CHANGE_REQUEST_TYPE_IM = 3;
    public static final int CHANGE_REQUEST_TYPE_ADDRESS = 4;
    public static final int CHANGE_REQUEST_TYPE_WEBS = 5;

    private List<IntegrationProfileItem> mNewPhones ;
    private List<IntegrationProfileItem> mNewEmails ;
    private List<IntegrationProfileAddress> mNewAddresses ;
    private List<IntegrationProfileItem> mNewIMs;
    private List<IntegrationProfileItem> mNewWebs;
    private List<IntegrationProfileName> mNewNames;

    private ProfileSuggestionParser() {
        mNewPhones = new ArrayList<IntegrationProfileItem>();
        mNewEmails = new ArrayList<IntegrationProfileItem>();
        mNewAddresses = new ArrayList<IntegrationProfileAddress>();
        mNewIMs = new ArrayList<IntegrationProfileItem>();
        mNewWebs = new ArrayList<IntegrationProfileItem>();
        mNewNames = new ArrayList<IntegrationProfileName>();
    }
    
    public List<IntegrationProfileItem> getNewPhones() {
        return mNewPhones;
    }
    
    public List<IntegrationProfileItem> getNewEmails(){
        return mNewEmails;
    }
    
    public List<IntegrationProfileItem> getNewIMs(){
        return mNewIMs;
    }
    
    public List<IntegrationProfileAddress> getNewAddresses(){
        return mNewAddresses;
    }
    
    public List<IntegrationProfileItem> getNewWebs(){
        return mNewWebs;
    }
    
    public List<IntegrationProfileName> getNewNames(){
        return mNewNames;
    }

    public boolean hasChange(){
        return mNewNames.size() > 0
                || mNewAddresses.size() > 0
                || mNewEmails.size() > 0
                || mNewIMs.size() > 0
                || mNewPhones.size() > 0
                || mNewWebs.size() > 0;
    }

    public static ProfileSuggestionParser parse(String changeRequest) {
        ProfileSuggestionParser change = new ProfileSuggestionParser();
        if(Utility.isEmpty(changeRequest)){
            return change;
        }
        try {
            change.parseData(new JSONObject(changeRequest));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return change;
    }

    private void parseData(JSONObject changeRequestJson) {
        try {
            parseNameItem(changeRequestJson);
            parseChangedItems(changeRequestJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseNameItem(JSONObject changedItem) throws JSONException {
        if(changedItem.has(CHANGE_REQUEST_CONTACT_NAMES_KEY)){
            JSONArray nameArray = changedItem.getJSONArray(CHANGE_REQUEST_CONTACT_NAMES_KEY);
            for(int i=0;i<nameArray.length();i++){
                IntegrationProfileName nameItem = new IntegrationProfileName();
                JSONObject nameJson = nameArray.getJSONObject(i);
                if(nameJson.has(CHANGE_REQUEST_CONTACT_NAME_FIRST_NAME_KEY)){
                    nameItem.setFirstName(nameJson.getString(CHANGE_REQUEST_CONTACT_NAME_FIRST_NAME_KEY));
                }
                if(nameJson.has(CHANGE_REQUEST_CONTACT_NAME_MIDDLE_NAME_KEY)){
                    nameItem.setMiddleName(nameJson.getString(CHANGE_REQUEST_CONTACT_NAME_MIDDLE_NAME_KEY));
                }
                if(nameJson.has(CHANGE_REQUEST_CONTACT_NAME_LAST_NAME_KEY)){
                    nameItem.setLastName(nameJson.getString(CHANGE_REQUEST_CONTACT_NAME_LAST_NAME_KEY));
                }
                if(nameJson.has(CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY)){
                    nameItem.setCount(nameJson.getInt(CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY));
                }
                mNewNames.add(nameItem);
            }
        }
    }

    private void parseChangedItems(JSONObject changedItem) throws JSONException {
        // phone
        parseOneItem(CHANGE_REQUEST_CONTACT_PHONES_KEY,changedItem,mNewPhones);

        //email
        parseOneItem(CHANGE_REQUEST_CONTACT_EMAILS_KEY,changedItem,mNewEmails);

        //im
        parseOneItem(CHANGE_REQUEST_CONTACT_IMS_KEY,changedItem,mNewIMs);

        //web
        parseOneItem(CHANGE_REQUEST_CONTACT_WEBS_KEY,changedItem,mNewWebs);

        parseChangedAddress(changedItem);
    }

    private void parseOneItem(String key,JSONObject changedItem,List<IntegrationProfileItem> items) throws JSONException {
        if(changedItem.has(key)){
            JSONArray itemArray = changedItem.getJSONArray(key);
            for(int i=0;i<itemArray.length();i++){
                JSONObject item = itemArray.getJSONObject(i);
                IntegrationProfileItem profileItem = new IntegrationProfileItem();
                if(item.has(CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY)){
                    profileItem.setCount(item.getInt(CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY));
                }
                if(item.has(CHANGE_REQUEST_CONTACT_ITEM_TYPE_KEY)){
                    profileItem.setType(item.getInt(CHANGE_REQUEST_CONTACT_ITEM_TYPE_KEY));
                }
                if(item.has(CHANGE_REQUEST_CONTACT_ITEM_VALUE_KEY)){
                    profileItem.setValue(item.getString(CHANGE_REQUEST_CONTACT_ITEM_VALUE_KEY));
                }
                items.add(profileItem);
            }
        }
    }

    private void parseChangedAddress(JSONObject changedItem) throws JSONException {
        if(changedItem.has(CHANGE_REQUEST_CONTACT_ADDRESSES_KEY)){
            JSONArray addressArray = changedItem.getJSONArray(CHANGE_REQUEST_CONTACT_ADDRESSES_KEY);
            for(int i=0;i<addressArray.length();i++){
                IntegrationProfileAddress address = new IntegrationProfileAddress();
                JSONObject addressJson = addressArray.getJSONObject(i);

                address.setCity(addressJson.has(CHANGE_REQUEST_CONTACT_ADDRESSES_CITY_KEY)
                        ?addressJson.getString(CHANGE_REQUEST_CONTACT_ADDRESSES_CITY_KEY):"");
                address.setState(addressJson.has(CHANGE_REQUEST_CONTACT_ADDRESSES_COUNTRY_KEY)
                        ? addressJson.getString(CHANGE_REQUEST_CONTACT_ADDRESSES_COUNTRY_KEY) : "");
                address.setExtendedAddress(addressJson.has(CHANGE_REQUEST_CONTACT_ADDRESSES_EXTENDED_ADDRESS_KEY)
                        ? addressJson.getString(CHANGE_REQUEST_CONTACT_ADDRESSES_EXTENDED_ADDRESS_KEY) : "");
                address.setPostcode(addressJson.has(CHANGE_REQUEST_CONTACT_ADDRESSES_POSTAL_CODE_KEY)
                        ? addressJson.getString(CHANGE_REQUEST_CONTACT_ADDRESSES_POSTAL_CODE_KEY) : "");
                address.setPostOfficeAddress(addressJson.has(CHANGE_REQUEST_CONTACT_ADDRESSES_POST_OFFICE_ADDRESS_KEY)
                        ?addressJson.getString(CHANGE_REQUEST_CONTACT_ADDRESSES_POST_OFFICE_ADDRESS_KEY):"");
                address.setCountry(addressJson.has(CHANGE_REQUEST_CONTACT_ADDRESSES_STATE_KEY)
                        ?addressJson.getString(CHANGE_REQUEST_CONTACT_ADDRESSES_STATE_KEY):"");
                address.setStreet(addressJson.has(CHANGE_REQUEST_CONTACT_ADDRESSES_STREET_KEY)
                        ?addressJson.getString(CHANGE_REQUEST_CONTACT_ADDRESSES_STREET_KEY):"");

                mNewAddresses.add(address);
            }
        }

    }

}
