/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.json;

/**
 * User: b251
 * Date: 12/28/11
 * Time: 4:25 PM
 * Borqs project
 */
public class JSONCreator {
    private JSONObject mData = new JSONObject();

    public interface Handler{
        public void generate(JSONCreator writer) throws JSONException;
    }

    private JSONCreator(){
    }

    public static JSONObject createJSON(Handler handler){
        JSONCreator writer = new JSONCreator();
        try{
            if(handler != null){
                handler.generate(writer);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return writer.mData;
    }

    public void put(String key, String value) throws JSONException {
        mData.put(key, value);
    }
    public void put(String key, Integer value) throws JSONException {
        mData.put(key, value);
    }
    public void put(String key, Boolean value) throws JSONException {
        mData.put(key, value);
    }
    public void put(String key, JSONObject value) throws JSONException {
        mData.put(key, value);
    }
    public void put(String key, JSONArray value) throws JSONException {
        mData.put(key, value);
    }
}
