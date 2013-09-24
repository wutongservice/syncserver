/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.contact;

import com.funambol.server.config.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Date: 3/9/12
 * Time: 1:04 PM
 * Borqs project
 */
public final class BCSConfig {
    private static final String CONFIG_FILE = "contact_sync.properties";

    public static final String NAMING_HOST = "naming_host";
    public static final String NAMING_PORT = "naming_port";


    private static Properties mConfigSettings;

    public static String getConfigString(String key){
        return mConfigSettings.getProperty(key);
    }

    public static int getConfigInt(String key){
        return Integer.parseInt(mConfigSettings.getProperty(key));
    }

    static{
        String configSourcePath = Configuration.getFunambolHome()
                + "/ds-server/" + CONFIG_FILE;
        InputStream in = null;
        try {
            in = new FileInputStream(configSourcePath);
            mConfigSettings = new Properties();
            mConfigSettings.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
