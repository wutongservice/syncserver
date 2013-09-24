package com.borqs.sync.server.task;

import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.task.ITaskListener;
import com.borqs.sync.server.task.profilesuggestion.ProfileSuggestionCollectionTask;
import com.borqs.sync.server.task.profilesuggestion.ProfileSuggestionTaskManager;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/31/12
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileSuggestionTaskManagerTest {

    private static final boolean DEBUG = false;
    private Object mTaskLock = new Object();

    private long start = System.currentTimeMillis();

    class TaskListenerTest implements ITaskListener {

        @Override
        public void onTaskStart() {
            System.out.println("===================task start");
        }

        @Override
        public void onTaskEnd() {
            System.out.println("-==================task end");
            synchronized (mTaskLock) {
                mTaskLock.notifyAll();
            }
            System.out.print("========================spent :" + (System.currentTimeMillis() - start) + "ms");
        }
    }

    @Test
    public void testStartTask() {
        if(!DEBUG){
            return;
        }
        MockContext context = new MockContext();
        ProfileSuggestionCollectionTask task = new ProfileSuggestionCollectionTask();

        ProfileSuggestionTaskManager manager = new ProfileSuggestionTaskManager(context);
        manager.registerListener(new TaskListenerTest());

        manager.startTask(task, "com.borqs.sync.server.task.profilesuggestion.ProfileSuggestionCollectionTask");

        synchronized (mTaskLock) {
            try {
                mTaskLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    class MockContext implements Context {

        @Override
        public Connection getSqlConnection() {
            return getConnection(getConfig().getDBSettings());
        }

        @Override
        public Connection getSqlConnection(String dataSource) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public MessagePublisherFactory getMessagePublisherFactory() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public ConfigurationBase getConfig() {
//            String sync_app_home = System.getProperty("sync.app.home");
//            File configFile = new File(sync_app_home + File.separator + CONFIG_BASE_FILE);


            return new DebugConfiguration();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Logger getLogger() {
            return Logger.getLogger("");  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Logger getLogger(String tag) {
            return Logger.getLogger(tag);  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isDebug() {
            return true;
        }
    }

    private Connection getConnection(Properties dbProperties){
        try {
            DataSource dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private class DebugConfiguration implements ConfigurationBase {
        private static final String NAMING_HOST = "127.0.0.1";
        private static final int NAMING_PORT = 9899;
        private boolean mHasJMSConsumer = true;
        private boolean mHasRPCServiceImpl = false;


        @Override
        public int getNamingPort() {
            return NAMING_PORT;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getNamingHost() {
            return NAMING_HOST;
        }

        @Override
        public String getSetting(String settingKey) {
            return "http://api.borqs.com";
        }

        public Properties getDBSettings() {
            Properties db = new Properties();
            db.setProperty("id","default");
            db.setProperty("driverClassName","com.mysql.jdbc.Driver");
            db.setProperty("url","jdbc:mysql://192.168.5.208:3306/borqs_sync_rel?characterEncoding=UTF-8");
            db.setProperty("password","borqs_sync");
            db.setProperty("username","borqs_sync");
            db.setProperty("maxActive","8");
            db.setProperty("maxIdle","5");
            db.setProperty("minIdle","4");
            db.setProperty("maxWait","1000");
            db.setProperty("removeAbandoned","true");
            db.setProperty("removeAbandonedTimeout","120");
            db.setProperty("testOnBorrow","false");
            db.setProperty("logAbandoned", "false");
            return db;
        }

        @Override
        public Properties getJMSServiceConfig() {
            Properties jms = new Properties();
            if(mHasJMSConsumer){
                //base service
                //jms.setProperty("jms.ptp.consumer.identifier.synccontact.change","com.borqs.sync.contactchange.jms.SyncContactChangeConsumer");
                jms.setProperty("jms.ptp.consumer.identifier.ass.ChangeProfile", "AccountChangeListener");
            }

            //jms auth
            jms.setProperty("username","syncserver");
            jms.setProperty("password", "borqs.com");
            //PEASE change below url for your testing/debug
            jms.setProperty("url", "tcp://localhost:61616");
            return jms;
        }

        @Override
        public String getInstalledServices() {
            return "[\\\n" +
                    "  {service:com.borqs.sync.server.services.naming.NamingServic, enable:false, priority:0, desc:naming},\\\n" +
                    "  {service:RPCService, enable:true, priority:1, desc:rpc_contact_provider, interface:com.borqs.sync.avro.ISyncDataProvider, impl:SyncDataProviderSkeleton, schema:avro},\\\n" +
                    "  {service:RPCService, enable:false, priority:2, desc:rpc_contact_change, interface:com.borqs.sync.avro.contactchange.IContactChangeProvider, impl:ContactChangeServiceImpl, schema:avro},\\\n" +
                    "  {service:JMSService, enable:false, priority:3, desc:jms_contact_change, impl:com.borqs.sync.contactchange.jms.SyncContactChangeConsumer},\\\n" +
                    "  {service:JMSService, enable:flase, priority:4, desc:jms_acccount_sync, impl:AccountChangeListener}\\\n" +
                    "  ]";
        }

        @Override
        public Properties getRPCServiceConfig() {
            Properties rpc = new Properties();
            if(mHasRPCServiceImpl){
                rpc.setProperty("rpc#com.borqs.sync.avro.ISyncDataProvider", "avro://SyncDataProviderSkeleton");
                rpc.setProperty("rpc#com.borqs.sync.avro.contactchange.IContactChangeProvider", "avro://ContactChangeServiceImpl");
            }
            return rpc;
        }

        @Override
        public Properties getHTTPServiceConfig() {
            return null;
        }

        @Override
        public Properties getPushSettings() {
            Properties service = new Properties();
            service.setProperty("appid", "102");
            service.setProperty("address", "http://app1.borqs.com:9090/plugins/xDevice/send");
            return service;
        }

        @Override
        public Properties getWebAgentSettings() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getTaskSettings() {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            if(minute == 59){
                hour = hour + 1;
                minute = 0;
            }else{
                minute = minute + 1;
            }

            String taskRuleValue =  "[\n" +
                    "{ task_impl:com.borqs.sync.server.task.profilesuggestion.ProfileSuggestionCollectionTask,\n" +
                    "  task_rule_detail:[\n" +
                    "  {rule:1,\n" +
                    "   enable:true,\n" +
                    "   hour:%d,\n" +
                    "   minute:%d\n" +
                    "  },\n" +
                    "  {rule:2,\n" +
                    "  enable:false,\n" +
                    "  dayOfMonth:1,\n" +
                    "  hour:0,\n" +
                    "  minute:0\n" +
                    "  },\n" +
                    "  {rule:3,\n" +
                    "  enable:false,\n" +
                    "  dayOfWeek:1,\n" +
                    "  hour:0,\n" +
                    "  minute:0\n" +
                    "  }\n" +
                    "  ]\n" +
                    "}\n" +
                    "  ]";
            taskRuleValue = String.format(taskRuleValue,hour,minute);
            System.out.println(taskRuleValue);
            Properties taskSettings = new Properties();
            taskSettings.put("task_rule",taskRuleValue);
            return taskSettings;  //To change body of implemented methods use File | Settings | File Templates.
        }

		@Override
		public Properties getRedisSettings() {
			// TODO Auto-generated method stub
			return null;
		}

        @Deprecated
        @Override
        public InputStream getConfigFile(String fileName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public InputStream getStaticConfigFile(String fileName) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
