/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent;

import java.util.Properties;

import com.borqs.sync.server.common.runtime.Context;

public class WebConfiguration {
    
    private static final String WEB_AGENT_CONF_PATH_KEY = "webagent_conf_path";
    
    public static final String getWebAgentConfPath(Context context){
        Properties properties = context.getConfig().getWebAgentSettings();
        String path = properties.getProperty(WEB_AGENT_CONF_PATH_KEY);
        return path;
    }
    

}
