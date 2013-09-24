/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.service;

import com.borqs.sync.server.common.runtime.Context;

/**
 * Date: 3/23/12
 * Time: 4:52 PM
 * Borqs project
 */
public class ConfigService {
    //Config list
    //public static final String SETTING_SMS_SERVICE_NUMBER = "sms_service_number";
    public String getConfig(Context context, String key){
        return context.getConfig().getSetting(key);
    }
}
