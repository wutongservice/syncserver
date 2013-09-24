/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.notification;

import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;

/**
 * Date: 9/8/11
 * Time: 6:29 PM
 */
public class ActionMessageParser {
    private JSONObject mMessage;
    public ActionMessageParser(String message){
        try {
            mMessage = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String parseAction(){
        if(mMessage != null){
            try {
                return mMessage.getString(MessagePublisherFactory.PTP_CHANGED_CONTACTS_ACTION_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String parseData(){
        if(mMessage != null){
            try {
                return mMessage.getString(MessagePublisherFactory.PTP_CHANGED_CONTACTS_DATA_KEY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
