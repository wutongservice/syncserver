/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.common.profilesuggestion;

import com.borqs.sync.server.common.datamining.IntegrationProfileAddress;
import com.borqs.sync.server.common.datamining.IntegrationProfileItem;
import com.borqs.sync.server.common.datamining.IntegrationProfileName;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * builder change request
 *
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
public class ProfileSuggestionBuilder {

    private List<IntegrationProfileItem> mPhones = new ArrayList<IntegrationProfileItem>();
    private List<IntegrationProfileItem> mEmails = new ArrayList<IntegrationProfileItem>();
    private List<IntegrationProfileItem> mIMs = new ArrayList<IntegrationProfileItem>();
    private List<IntegrationProfileItem> mWebs = new ArrayList<IntegrationProfileItem>();
    private List<IntegrationProfileAddress> mAddresses = new ArrayList<IntegrationProfileAddress>();
    private List<IntegrationProfileName> mNames = new ArrayList<IntegrationProfileName>();


    boolean mChanged;

    public ProfileSuggestionBuilder() {
    }
    
    public void addName(IntegrationProfileName name){
        mChanged = true;
        mNames.add(name);
    }
    
    public void addPhone(IntegrationProfileItem phone){
        mChanged = true;
        mPhones.add(phone);
    }
    
    public void addEmail(IntegrationProfileItem email){
        mChanged = true;
        mEmails.add(email);
    }
    
    public void addIM(IntegrationProfileItem im){
        mChanged = true;
        mIMs.add(im);
    }

    public void addWeb(IntegrationProfileItem web){
        mChanged = true;
        mWebs.add(web);
    }
    
    public void addAddress(IntegrationProfileAddress address){
        mChanged = true;
        mAddresses.add(address);
    }

    public String compose() throws JSONException {
        JSONObject changedContact = new JSONObject();

        composeNameArray(changedContact,mNames);
        composeItemArray(changedContact,mPhones, ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_PHONES_KEY);
        composeItemArray(changedContact,mEmails, ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_EMAILS_KEY);
        composeItemArray(changedContact,mIMs, ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_IMS_KEY);
        composeItemArray(changedContact,mWebs, ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_WEBS_KEY);
        composeAddressArray(changedContact, mAddresses);

        return changedContact.toString();
    }

    private void composeNameArray(JSONObject changedContact,List<IntegrationProfileName> names) throws JSONException {
        if(names != null){
            JSONArray nameArray = new JSONArray();
            for(IntegrationProfileName name:names){
                JSONObject nameObject = new JSONObject();
                nameObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_NAME_FIRST_NAME_KEY,name.getFirstName());
                nameObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_NAME_MIDDLE_NAME_KEY,name.getMiddleName());
                nameObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_NAME_LAST_NAME_KEY,name.getLastName());
                nameObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY,name.getCount());
                nameArray.put(nameObject);
            }
            changedContact.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_NAMES_KEY,nameArray);
        }
    }

    private void composeAddressArray(JSONObject changedContact,List<IntegrationProfileAddress> addresses) throws JSONException {
        if(addresses != null){
            JSONArray addressArray = new JSONArray();
            for(IntegrationProfileAddress address:addresses){
                JSONObject addressObject = new JSONObject();
                if(!Utility.isEmpty(address.getCity())){
                    addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_CITY_KEY,address.getCity());
                }
                if(!Utility.isEmpty(address.getStreet())){
                    addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_STREET_KEY,address.getStreet());
                }
                if(!Utility.isEmpty(address.getState())){
                    addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_STATE_KEY,address.getState());
                }
                if(!Utility.isEmpty(address.getExtendedAddress())){
                    addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_EXTENDED_ADDRESS_KEY,address.getExtendedAddress());
                }
                if(!Utility.isEmpty(address.getPostcode())){
                    addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_POSTAL_CODE_KEY,address.getPostcode());
                }
                if(!Utility.isEmpty(address.getPostOfficeAddress())){
                    addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_POST_OFFICE_ADDRESS_KEY,address.getPostOfficeAddress());
                }
                if(!Utility.isEmpty(address.getCountry())){
                    addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_COUNTRY_KEY,address.getCountry());
                }
                addressObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY,address.getCount());
                addressArray.put(addressObject);
            }
            changedContact.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ADDRESSES_KEY,addressArray);
        }
    }

    private void composeItemArray(JSONObject changedContact,List<IntegrationProfileItem> items,String key) throws JSONException {
        if(items != null){
            JSONArray itemArray = new JSONArray();
            for(IntegrationProfileItem item:items){
                JSONObject itemObject = new JSONObject();
                String value = item.getValue();
                if(!Utility.isEmpty(value)){
                    itemObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ITEM_VALUE_KEY,item.getValue());
                    itemObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ITEM_TYPE_KEY,item.getType());
                    itemObject.put(ProfileSuggestionParser.CHANGE_REQUEST_CONTACT_ITEM_COUNT_KEY,item.getCount());
                    itemArray.put(itemObject);
                }
            }
            changedContact.put(key,itemArray);
        }
    }

    public boolean hasChange(){
        return mChanged;
    }

}
