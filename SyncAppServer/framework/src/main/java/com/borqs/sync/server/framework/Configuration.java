/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework;

import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration implements ConfigurationBase {
    public static final String NAMING_PORT = "naming_service_port";
    public static final String NAMING_HOST = "namign_service_host";

    private static final String DB_SETTING_FILE = "db_setting_entry";
    private static final String JMS_SETTING_FILE = "jms_setting_entry";
    private static final String RPC_SETTING_FILE = "rpc_setting_entry";
    private static final String SERVICES = "services";
    private static final String PUSH_SETTING_FILE = "push_setting_entry";
    private static final String WEB_AGENT_SETTING_FILE = "web_agent_setting_entry";
    private static final String TASK_SETTING_FILE = "task_setting_entry";
    private static final String REDIS_SETTING_FILE = "redis_setting_entry";

    //tag for XML configuration
    final static class TAG{
        public static final String SERVICE = "service";
        public static final String ENABLE = "enable";
        public static final String PRIORITY = "priority";
        public static final String DESC = "desc";
        public static final String INTERFACE = "interface";
        public static final String IMPL = "impl";
        public static final String SCHEMA = "schema";
        public static final String PORT = "port";
        public static final String URL_PATH_CONTEXT = "url_context";
        public static final String URL_PATH_PARTNER = "url_partner";
    }

    public static final String PTP_SEND_CHANGED_CONTACTS_TO_MONITOR = "jms.ptp.send.changed.contacts.to.monitor";

    private String mConfigPath;
	private Properties mValues;
	
	public Configuration(File settings) throws IOException{		
		mValues = new Properties();
		FileInputStream fis = new FileInputStream(settings);
		mValues.load(fis);
        fis.close();

        mConfigPath = settings.getParentFile().getAbsolutePath();
	}

    public String getInstalledServices(){
        return mValues.getProperty(SERVICES, "[]");
    }

    public Properties getRPCServiceConfig(){
        String rpcSettingFile = mValues.getProperty(RPC_SETTING_FILE);
        return loadSetting(rpcSettingFile);
    }

    @Override
    public Properties getHTTPServiceConfig() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNamingPort(){
		return Integer.parseInt(mValues.getProperty(NAMING_PORT, String.valueOf(NamingServiceProxy.DEFAULT_PORT)));
	}

    public String getNamingHost(){
		return mValues.getProperty(NAMING_HOST, NamingServiceProxy.DEFAULT_HOST);
	}

    @Override
    public String getSetting(String settingKey) {
        return mValues.getProperty(settingKey);
    }

    public Properties getDBSettings(){
        String dbSettingFile = mValues.getProperty(DB_SETTING_FILE);
        return loadSetting(dbSettingFile);
	}

    public Properties getJMSServiceConfig(){
        String jmsSettingFile = mValues.getProperty(JMS_SETTING_FILE);
        return loadSetting(jmsSettingFile);
    }
	
	public void dump(){
		System.out.print("============configration============");
		mValues.list(System.out);
		System.out.print("==================END===============");
	}

    private Properties loadSetting(String configFileName){
        String settingFile =   configFileName;
        Properties entry = new Properties();
        try{
            settingFile = mConfigPath + File.separator +  settingFile;
            FileInputStream fis = new FileInputStream(settingFile);
            entry.load(fis);
        }catch(IOException e){
            System.out.print("Failed to open setting file: " + settingFile);
        }

        return entry;
    }

    public Properties getPushSettings(){
        String pushSettingFile = mValues.getProperty(PUSH_SETTING_FILE);
        return loadSetting(pushSettingFile);
    }

    @Override
    public Properties getWebAgentSettings() {
        String pushSettingFile = mValues.getProperty(WEB_AGENT_SETTING_FILE);
        return loadSetting(pushSettingFile);
    }

    @Override
    public Properties getTaskSettings() {
        String pushSettingFile = mValues.getProperty(TASK_SETTING_FILE);
        return loadSetting(pushSettingFile);
    }

	@Override
	public Properties getRedisSettings() {
		 String redisSetting = mValues.getProperty(REDIS_SETTING_FILE);
	        return loadSetting(redisSetting);
	}

    //TODO can config the file into config folder,should use speatared folder
    // to storage these files for fetch by client
    @Deprecated
    @Override
    public InputStream getConfigFile(String fileName) {
        String settingFile =   fileName;
        Properties entry = new Properties();
        try{
            settingFile = mConfigPath + File.separator +  settingFile;
            FileInputStream fis = new FileInputStream(settingFile);
            return fis;
        }catch(IOException e){
            System.out.print("Failed to open setting file: " + settingFile);
        }
        return null;
    }

    @Override
    public InputStream getStaticConfigFile(String fileName) {
        String settingFile =   fileName;
        Properties entry = new Properties();
        try{
            settingFile = mConfigPath + File.separator + ".." + File.separator
                    + "static_config" + File.separator  +  settingFile;
            FileInputStream fis = new FileInputStream(settingFile);
            return fis;
        }catch(IOException e){
            System.out.print("Failed to open setting file: " + settingFile);
        }
        return null;
    }


}
