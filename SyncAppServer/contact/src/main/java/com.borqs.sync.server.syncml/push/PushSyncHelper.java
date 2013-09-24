package com.borqs.sync.server.syncml.push;

import com.borqs.sync.server.common.push.IPushService;
import com.borqs.sync.server.common.push.PushServiceImpl;
import com.borqs.sync.server.common.push.PushTaskManager;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.syncml.dao.ContactDAO;
import com.borqs.sync.server.syncml.util.ContactLogger;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 12/29/11
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class PushSyncHelper {

    private static final String TAG = "PushSyncHelper";

    private ContactDAO mDAO ;
    private Context mContext;
    private Logger mLogger;


    public PushSyncHelper(Context context){
        mDAO = new ContactDAO(context);
        mContext = context;
        mLogger = ContactLogger.getLogger(context);
    }


    public void replaceSyncVersionWithSourceVersion(long principalId,String deviceId,String userId){
        mDAO.replaceSyncVersionWithSourceVersion(principalId,deviceId,userId);
    }

    public void schedulePushJob(String userId) throws SchedulerException, ParseException, InterruptedException {
        List<String> syncDevices = mDAO.getSyncDevices(userId);
        if(syncDevices == null || syncDevices.size() <=0 ){
            mLogger.info("no device need to be synced,do schedule a sync-push job");
            return;
        }

        Map<Object,Object> parameterMap = new HashMap<Object, Object>();
        //push_service,receiver,sync_source_version,sync_devices,logger
        String userName = userId;
        IPushService pushService = new PushServiceImpl(mContext);
        pushService.setLogger(mLogger);
        parameterMap.put(SyncMessagePushTask.JOB_DATA_PARAMETER_PUSH_SERVICE, pushService);
        parameterMap.put(SyncMessagePushTask.JOB_DATA_PARAMETER_RECEIVER,userName);
        parameterMap.put(SyncMessagePushTask.JOB_DATA_KEY_LOGGER,mLogger);
        parameterMap.put(SyncMessagePushTask.JOB_DATA_PARAMETER_SYNC_DEVICES,syncDevices);

        PushTaskManager manager = PushTaskManager.getInstance(StdSchedulerFactory.getDefaultScheduler(), mLogger);

        manager.scheduleJob(new SyncMessagePushTask(),userName
                ,SyncMessagePushTask.WAIT_SYNC,parameterMap);

    }

}
