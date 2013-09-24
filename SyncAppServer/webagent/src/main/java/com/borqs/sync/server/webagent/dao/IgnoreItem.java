package com.borqs.sync.server.webagent.dao;

import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.profilesuggestion.ProfileSuggestionParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 6/7/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class IgnoreItem {
    
    private static final String IGNORE_ITEM_PHONES_KEY = "phones";
    private static final String IGNORE_ITEM_EMAILS_KEY = "emails";

    private int mType;
    private List<String> mValues;

    /**
     * {"phones":["10086","10087"],"emails":["email1@email.com","email2@email.com"]}
     * @param reader
     * @return
     */
    public static List<IgnoreItem> parseIgnoreItem(JsonReader reader) throws IOException {
        List<IgnoreItem> ignoreItems = new ArrayList<IgnoreItem>();

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(IGNORE_ITEM_PHONES_KEY.equals(name)){
                IgnoreItem ignorePhone = new IgnoreItem();
                ignorePhone.mType = ProfileSuggestionParser.CHANGE_REQUEST_TYPE_PHONE;
                parseItems(reader, ignorePhone);
                ignoreItems.add(ignorePhone);
            }else if(IGNORE_ITEM_EMAILS_KEY.equals(name)){
                IgnoreItem ignoreEmail = new IgnoreItem();
                ignoreEmail.mType = ProfileSuggestionParser.CHANGE_REQUEST_TYPE_EMAIL;
                parseItems(reader, ignoreEmail);
                ignoreItems.add(ignoreEmail);
            }
        }
        reader.endObject();
        return ignoreItems;
    }

    /**
     * compose the ignore item json
     *  {"phones":["10086","10087"],"emails":["email1@email.com","email2@email.com"]}
     * @param items
     * @return
     * @throws JSONException
     */
    public static String composeJsonStr(List<IgnoreItem> items) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (items != null) {
            for (IgnoreItem item : items) {
                switch (item.getType()) {
                    case ProfileSuggestionParser.CHANGE_REQUEST_TYPE_PHONE:
                        JSONArray phones = new JSONArray();
                        for (String phone : item.getValues()) {
                            phones.put(phone);
                        }
                        jsonObject.put(IGNORE_ITEM_PHONES_KEY, phones);
                        break;
                    case ProfileSuggestionParser.CHANGE_REQUEST_TYPE_EMAIL:
                        JSONArray emails = new JSONArray();
                        for (String email : item.getValues()) {
                            emails.put(email);
                        }
                        jsonObject.put(IGNORE_ITEM_EMAILS_KEY, emails);
                        break;
                }
            }
       }
        return jsonObject.toString();
    }

    private static void parseItems(JsonReader reader,IgnoreItem ignoreItem) throws IOException {
        List<String> phones = new ArrayList<String>();
        reader.beginArray();
        while(reader.hasNext()){
            phones.add(reader.nextString());
        }
        ignoreItem.mValues = phones;
        reader.endArray();
    }

    public List<String> getValues(){
        return mValues;
    }
    
    public int getType(){
        return mType;
    }
    
    public void setValues(List<String> values){
        mValues = values;
    }

    public void setType(int type){
        mType = type;
    }

}
