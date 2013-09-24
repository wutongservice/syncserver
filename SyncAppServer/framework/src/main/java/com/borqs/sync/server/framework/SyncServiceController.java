/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework;

import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.runtime.ContextHolder;

import java.util.*;

/**
 * Date: 9/8/11
 * Time: 7:15 PM
 */
public final class SyncServiceController extends ContextHolder {
    private List<BaseService> mServiceInstances;

    SyncServiceController(Context context){
        super(context);
        mServiceInstances = new ArrayList<BaseService>();
        loadServices(context.getConfig());
    }

    List<BaseService> enumerateServices(){
        return mServiceInstances;
    }

    void runService(BaseService service){
        service.start(mServiceListener);
    }

    void waitForCompleted(){
        for(BaseService service : mServiceInstances){
            if(service.isEnabled()){
                service.join();
            }
        }
    }

    private void loadServices(ConfigurationBase config){
        String services = config.getInstalledServices();
        try {
            JSONArray serviceList = new JSONArray(services);
            int length = serviceList.length();
            for(int i=0; i<length; i++){
                JSONObject s = serviceList.getJSONObject(i);
                ServiceDescriptor descriptor = ServiceDescriptor.from(s);
                BaseService service =
                        BaseService.fromName(s.getString(Configuration.TAG.SERVICE), mContext);
                if(service != null && service.init(descriptor) && descriptor.enabled()){
                    mServiceInstances.add(service);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(mServiceInstances);
    }

    private BaseService.StatusListener mServiceListener = new BaseService.StatusListener(){
        @Override
        public void onStart(Context context) {
        }

        @Override
        public void onStop(Context context) {
        }
    };
}
