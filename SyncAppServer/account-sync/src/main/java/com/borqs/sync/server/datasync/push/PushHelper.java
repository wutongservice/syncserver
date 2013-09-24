package com.borqs.sync.server.datasync.push;

import com.borqs.sync.server.common.push.IPushService;
import com.borqs.sync.server.common.push.PushServiceImpl;
import com.borqs.sync.server.common.push.PushTaskManager;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.DSLog;
import com.borqs.sync.server.datasync.ass.dao.ContactSyncDAO;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 12/31/11
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class PushHelper {

    private Context mContext;
    private PushTaskManager mPushTaskManager;
    private IPushService mPushService;
    private Logger mLogger;

    public PushHelper(Context context){
        mContext = context;
        mLogger = DSLog.getInstnace(context).getLogger();
        try {
            mPushTaskManager = PushTaskManager.getInstance(StdSchedulerFactory.getDefaultScheduler(),mLogger);
            mPushService = new PushServiceImpl(context);
            mPushService.setLogger(mLogger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void onFriendShipChange(List<String> userIds){
         onUserContactChange(userIds);
    }

    public void onProfileChange(List<String> friendIds){
        ContactSyncDAO syncDAO = new ContactSyncDAO(mContext);
        mLogger.info("=========friendList:" + friendIds);
        if(friendIds != null && friendIds.size() > 0){
            List<String> userIdList = syncDAO.getUserIdByFriends(friendIds);
            if(userIdList != null && userIdList.size() > 0){
                 onUserContactChange(userIdList);
            }
        }
    }


    private void onUserContactChange(List<String> userIds){
        mLogger.info("=========onUserContactChange: " + userIds);
        if(userIds != null && userIds.size() > 0){
            //use trigger to update SyncSourceVersion
//            updateSyncSourceVersion(userIds);

            //2.schedule a push job
            scheduleSync(userIds);
        }

    }

//    private void updateSyncSourceVersion(List<String> userIds){
//        ContactSyncDAO syncDAO = new ContactSyncDAO(mContext);
//        for(String userId:userIds){
//            syncDAO.updateSyncSourceVersion(userId);
//        }
//    }

    private void scheduleSync(List<String> receivers) {
        try {
            ContactSyncDAO syncDAO = new ContactSyncDAO(mContext);

            for(String receiver:receivers){
                List<String> needSyncDevices = syncDAO.getNeedSyncDevices(receiver);
                mLogger.info("scheduleSync,need to sync devices: " + needSyncDevices);
                //use receiver as jobName
                String jobName = receiver;
                Map<Object,Object> jobMap = new HashMap<Object, Object>();
                jobMap.put(SyncMessagePushTask.JOB_DATA_KEY_PUSH_SERVICE, mPushService);
                jobMap.put(SyncMessagePushTask.JOB_DATA_KEY_RECEIVER, receiver);
                jobMap.put(SyncMessagePushTask.JOB_DATA_KEY_LOGGER, mLogger);
                jobMap.put(SyncMessagePushTask.JOB_DATA_KEY_SYNC_DEVICES, needSyncDevices);

                mPushTaskManager.scheduleJob(new SyncMessagePushTask(),jobName
                        ,SyncMessagePushTask.PUSH_MESSAGE_WAIT_TIMEOUT,jobMap);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
