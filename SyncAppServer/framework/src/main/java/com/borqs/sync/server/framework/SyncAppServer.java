/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.sql.SqlConnectionFactory;
import com.borqs.sync.server.common.sql.SqlConnectionFactoryBuilder;
import com.borqs.sync.server.framework.debug.DebugConfiguration;
import com.borqs.sync.server.framework.services.rpc.SystemResource;
import sun.management.ManagementFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class SyncAppServer {
    private static final String SYNC_APP_HOME = "sync.app.home";
    private static final String SYNC_APP_DEBUG = "sync.app.debug";

    private static final String CONFIG_BASE_FILE = "config/server.properties";
    private static final String PID_FILE = ".sync_app.pid";


	/**
	 * @param args
	 */
	public static void main(String[] args) {
        try {
            dumpPID();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        //load system properties
        String sync_app_debug = System.getProperty(SYNC_APP_DEBUG, String.valueOf(false));
        boolean debug = sync_app_debug.equals(String.valueOf(true));
        
        String sync_app_home = System.getProperty(SYNC_APP_HOME);
        if(!debug && (sync_app_home==null || "".equals(sync_app_home))){
            System.out.println("Incorrect runtime parameter.\n" +
                    "Usage: java -Dsync.app.home=<> -Dsync.app.debug=<>");
            System.exit(-1);
        }

        AppContext context = new AppContext();
        //set debug flag
        context.setDebug(debug);



        //init the configurations
        ConfigurationBase config;
        if( debug ){
            context.getLogger().info("Start sync application server in debug.");
            config = new DebugConfiguration();
        } else {
        	File configFile = new File(sync_app_home + File.separator + CONFIG_BASE_FILE);
		    try {
			    config = new Configuration(configFile);
		    } catch (IOException e1) {
			    e1.printStackTrace();
			    return;
		    }
        }
        context.setConfig(config);

        //init logger
        int loggerLimit = Integer.parseInt(config.getSetting("logger_limit"));
        int loggerCount = Integer.parseInt(config.getSetting("logger_count"));
        LoggerManager loggerManager = new LoggerManager(sync_app_home,loggerLimit,loggerCount);
        if(debug){
            loggerManager.setDebugLevel();
        }

        // install logger
        context.setLogger(loggerManager);

        //inti base resource object
        SystemResource.init(context);

        //init service context
        context.setMessagePublisherFactory(DefaultMessagePublisherFactory.get(context));
        
        // init database
        Properties dbConfig = config.getDBSettings();
        SqlConnectionFactory scf = new SqlConnectionFactoryBuilder().build(dbConfig);
        context.addSqlConnectionFactory(scf.getId(), scf);

        // init account manager
        context.setAccountManager(new AccountManager(context));
        
        //init sync services
        SyncServiceController controller = new SyncServiceController(context);

        List<BaseService> services = controller.enumerateServices();
        for(BaseService s : services){
            if(s.isEnabled()){
                controller.runService(s);
            }
        }

        controller.waitForCompleted();
	}

	private static void usage(){
	}

    private static void dumpPID() throws IOException {
        String sync_app_home = System.getProperty(SYNC_APP_HOME);
        String pid_file = "/tmp/" + PID_FILE;

        FileOutputStream fos = new FileOutputStream(new File(pid_file));
        String pid = String.valueOf(getPid());
        System.out.println("Current process ID : " + pid);
        fos.write(pid.getBytes());
        fos.write('\n');
        fos.flush();
        fos.close();
    }

    public static int getPid(){
        return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }
}
