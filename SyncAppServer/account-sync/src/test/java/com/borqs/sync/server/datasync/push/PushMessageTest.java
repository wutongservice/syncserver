package com.borqs.sync.server.datasync.push;

import com.borqs.sync.server.common.notification.MessagePublisherFactory;
import com.borqs.sync.server.common.push.IPushService;
import com.borqs.sync.server.common.push.PushServiceImpl;
import com.borqs.sync.server.common.push.PushTaskManager;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.DSLog;
import junit.framework.TestCase;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 12/14/11
 * Time: 6:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PushMessageTest extends TestCase{
     private static final String SYNC_APP_HOME = "sync.app.home";
    private static final String SYNC_APP_DEBUG = "sync.app.debug";

    private static final String CONFIG_BASE_FILE = "config/server.properties";
    private PushTaskManager mPushTaskManager;


    class MockConfiguration implements ConfigurationBase {

    private static final String NAMING_HOST = "127.0.0.1";
    private static final int NAMING_PORT = 9899;
    private boolean mHasJMSConsumer = true;
    private boolean mHasRPCServiceImpl = false;

        @Override
        public String getInstalledServices() {
            return null;
        }

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
            return null;
        }

        @Override
    public Properties getDBSettings() {
        Properties db = new Properties();
        db.setProperty("id","default");
        db.setProperty("driverClassName","com.mysql.jdbc.Driver");
        db.setProperty("url","jdbc:mysql://localhost:3306/borqs_sync_test?characterEncoding=UTF-8");
        db.setProperty("password","root");
        db.setProperty("username","root");
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
        public Properties getRPCServiceConfig() {
             Properties rpc = new Properties();
        if(mHasRPCServiceImpl){
            rpc.setProperty("rpc#com.borqs.sync.avro.ISyncDataProvider", "avro://com.borqs.sync.server.provider.SyncDataProviderSkeleton");
            rpc.setProperty("rpc#com.borqs.sync.avro.contactchange.IContactChangeProvider", "avro://com.borqs.sync.server.impl.contactchange.ContactChangeServiceImpl");
        }
         return rpc;
        }

        @Override
        public Properties getHTTPServiceConfig() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getPushSettings() {
            Properties pro = new Properties();
            pro.setProperty("appid","10");
            pro.setProperty("address","http://app1.borqs.com:9090/plugins/xDevice/send");
            return pro;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getWebAgentSettings() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getTaskSettings() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    class MockContext implements Context {

        @Override
        public Connection getSqlConnection() {
            return getConnection();
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
            return new MockConfiguration();  //To change body of implemented methods use File | Settings | File Templates.
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

     private Connection getConnection(){
         Properties mDbProperties = new Properties();
        mDbProperties.setProperty("driverClassName","com.mysql.jdbc.Driver");
        mDbProperties.setProperty("url","jdbc:mysql://192.168.5.208:3306/borqs_sync?characterEncoding=UTF-8");
        mDbProperties.setProperty("password","borqs.com");
        mDbProperties.setProperty("username","syncserver");
        mDbProperties.setProperty("maxActive","8");
        mDbProperties.setProperty("maxIdle","5");
        mDbProperties.setProperty("minIdle","4");
        mDbProperties.setProperty("maxWait","1000");
        mDbProperties.setProperty("removeAbandoned","true");
        mDbProperties.setProperty("removeAbandonedTimeout","120");
        mDbProperties.setProperty("testOnBorrow","false");
        mDbProperties.setProperty("logAbandoned", "false");
        try {
            DataSource dataSource = BasicDataSourceFactory.createDataSource(mDbProperties);
            return dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    IPushService mPushService;
    Context context;

    @Override
    protected  void setUp(){
        try {
            context = new MockContext();
            mPushTaskManager = PushTaskManager.getInstance(StdSchedulerFactory.getDefaultScheduler(), context.getLogger("account-sync-push"));
            mPushService = new PushServiceImpl(context);
            mPushService.setLogger(DSLog.getInstnace(context).getLogger());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void testSend(){
        List<String> receiverList = new ArrayList<String>();
        receiverList.add("10215");
        receiverList.add("10224");
        receiverList.add("225");
        receiverList.add("10231");
        receiverList.add("224");

        testSendPushMessage(context,receiverList);

        try {
            Thread.sleep(1000);
            context.getLogger("").info("================1s later===========");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        receiverList = new ArrayList<String>();
        receiverList.add("10215");
        receiverList.add("10224");
        receiverList.add("225");
        receiverList.add("10231");
        receiverList.add("224");
        receiverList.add("223");
        receiverList.add("226");
        receiverList.add("227");


        testSendPushMessage(context,receiverList);

        try {
            Thread.sleep(2000);
            context.getLogger("").info("================2s later===========");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        receiverList = new ArrayList<String>();
        receiverList.add("226");
        receiverList.add("227");
        receiverList.add("228");
        receiverList.add("229");
        receiverList.add("230");
        receiverList.add("223");
        receiverList.add("224");
        receiverList.add("225");
        receiverList.add("226");
        receiverList.add("227");


        testSendPushMessage(context,receiverList);

        try {
            Thread.sleep(5000);
           context.getLogger("").info("===============5s later===========");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        receiverList = new ArrayList<String>();
        receiverList.add("224");
        receiverList.add("225");
        receiverList.add("226");
        receiverList.add("229");
        receiverList.add("220");
        receiverList.add("221");
        receiverList.add("222");
        receiverList.add("223");
        receiverList.add("224");
        receiverList.add("225");
        receiverList.add("226");
        receiverList.add("227");


        testSendPushMessage(context, receiverList);

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    private void testSendPushMessage(Context context ,List<String> receiverList) {
        try {
            for (String receiver:receiverList){
                //use receiver as jobName
                String jobName = receiver;
                Map<Object,Object> jobMap = new HashMap<Object, Object>();
                jobMap.put(SyncMessagePushTask.JOB_DATA_KEY_PUSH_SERVICE, mPushService);
                jobMap.put(SyncMessagePushTask.JOB_DATA_KEY_RECEIVER, receiver);
                jobMap.put(SyncMessagePushTask.JOB_DATA_KEY_LOGGER, context.getLogger("PushMessageTes"));
                mPushTaskManager.scheduleJob(new SyncMessagePushTask(),jobName
                        ,4000,jobMap);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testOnFriendShipChange(){
        PushHelper pushHelper = new PushHelper(context);
        List<String> userIds = new ArrayList<String>();
        userIds.add("224");
        pushHelper.onFriendShipChange(userIds);
    }

    public void testOnProfileChange(){
        PushHelper pushHelper = new PushHelper(context);
        List<String> friends = new ArrayList<String>();
        friends.add("231");
        pushHelper.onProfileChange(friends);
    }


}
