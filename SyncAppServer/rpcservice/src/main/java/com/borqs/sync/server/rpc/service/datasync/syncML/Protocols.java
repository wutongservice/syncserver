/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.service.datasync.syncML;

import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Date: 3/12/12
 * Time: 6:43 PM
 * Borqs project
 */
public final class Protocols {
    public static final XResponse OK;

    public static String parseSyncMode(CharSequence jsonContent) {
        try {
            JSONObject jsonData = new JSONObject(jsonContent.toString());
            JSONObject data = jsonData.getJSONObject("data");
            return data.getString("synctype");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long parseSince(CharSequence jsonContent) {
        try {
            JSONObject jsonData = new JSONObject(jsonContent.toString());
            JSONObject data = jsonData.getJSONObject("data");
            return data.getLong("since");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * {"data":{"key":"3833883"}}
     * return the contact id's key json
     * @param key the contact id
     * @return
     */
    public static String generateKeyJson(long key){
        JSONObject dataJson = new JSONObject();
        JSONObject keyJson = new JSONObject();
        try {
            keyJson.put("key",String.valueOf(key));
            dataJson.put("data",keyJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataJson.toString();
    }
    
    public static String generateContactJson(String contactJson){
        return contactJson;
    }

    /**
     * {"data":{"keys":["1","2","3"]}}
     * return the contact ids's keys json
     * @param keys the contact ids
     * @return
     */
    public static String generateKeysJson(List<Long> keys){
        JSONObject dataJson = new JSONObject();
        JSONObject keysJson = new JSONObject();
        JSONArray keysArray = new JSONArray();
        boolean encounterError = false;
        if(keys != null){
            for(Long key:keys){
                keysArray.put(key);
            }
        }
        try {
            keysJson.put("keys",keysArray);
            dataJson.put("data",keysJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataJson.toString();
    }

    /**
     *  // {"data":{"time":"20081020T082922","tzid":"US\/Central"}}
     * @return  the time json
     */
    public static String generateTimeJson(long time,String tzid){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddTHHmmss");
        String timeStr = sdf.format(new Date(time));

        JSONObject dataJson = new JSONObject();
        JSONObject timeJson = new JSONObject();
        try {
            timeJson.put("time",timeStr);
            timeJson.put("tzid",tzid);
            dataJson.put("data",timeJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataJson.toString();


    }
    
    public final static class StatusCode {
        public static final int OK = 200;
        public static final int ERR_1 = 200;
        public static final int ERR_2 = 406;
        public static final int ERR_3 = 401;
        public static final int ERR_4 = 500;
    }


    static {
        OK = new XResponse();
        OK.status_code = StatusCode.OK;
        OK.content = "";
    }
}
