/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework;

import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;

/**
 * User: b251
 * Date: 12/20/11
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ServiceDescriptor {
    private JSONObject mData;
    private ServiceDescriptor(JSONObject data){
        mData = data;
    }

    public static ServiceDescriptor from(JSONObject object){
        return new ServiceDescriptor(object);
    }

    //common, service type
    public String service(){
        return getAttribute(Configuration.TAG.SERVICE);
    }

    //common
    public String desc(){
        return getAttribute(Configuration.TAG.DESC);
    }

    //common
    public String impl(){
        return getAttribute(Configuration.TAG.IMPL);
    }

    //common
    public String priority(){
        return getAttribute(Configuration.TAG.PRIORITY);
    }

    //common
    public boolean enabled(){
        return Boolean.valueOf(getAttribute(Configuration.TAG.ENABLE));
    }

    //for AVRO rpc service
    public String intf(){
        return getAttribute(Configuration.TAG.INTERFACE);
    }

    //for AVRO rpc service
    public String schema(){
        return getAttribute(Configuration.TAG.SCHEMA);
    }

    //For jetty HTTP server
    public int getServicePort(){
        return Integer.valueOf(getAttribute(Configuration.TAG.PORT));
    }
//
//    //For jetty HTTP server
//    public String getURLContext(){
//        return getAttribute(Configuration.TAG.URL_PATH_CONTEXT);
//    }
//
//    //For jetty HTTP server
//    public String getURLPartner(){
//        return getAttribute(Configuration.TAG.URL_PATH_PARTNER);
//    }

    private String getAttribute(String key){
        try {
            if(mData!=null && mData.has(key)){
                return mData.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

