package com.borqs.sync.server.common.runtime;

import java.io.InputStream;
import java.util.Properties;

/**
 * Date: 9/8/11
 * Time: 7:03 PM
 */
public interface ConfigurationBase {

    public static final String SMS_HOST = "sms_server_host";
    public static final String ACCOUNT_HOST = "account_server_host";
    public static final String NAMING_HOST = "namign_service_host";
    public static final String NAMING_PORT = "naming_service_port";
    public static final String SMS_APP_NAME = "sms_app_name";

    public abstract String getInstalledServices();
    public abstract int getNamingPort();
    public abstract String getNamingHost();
    public abstract String getSetting(String settingKey);
    public abstract Properties getDBSettings();
    public abstract Properties getJMSServiceConfig();
    public abstract Properties getRPCServiceConfig();
    public abstract Properties getHTTPServiceConfig();
    public abstract Properties getPushSettings();
    public abstract Properties getWebAgentSettings();
    public abstract Properties getTaskSettings();
    public abstract Properties getRedisSettings();
    @Deprecated
    public abstract InputStream getConfigFile(String fileName);
    public abstract InputStream getStaticConfigFile(String fileName);


}
