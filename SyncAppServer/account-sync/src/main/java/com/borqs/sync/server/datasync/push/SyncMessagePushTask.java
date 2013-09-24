
package com.borqs.sync.server.datasync.push;

import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.push.PushServiceImpl;
import com.borqs.sync.server.common.push.PushTaskManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: b211 Date: 12/14/11 Time: 3:50 PM one
 * receiver task
 */
public class SyncMessagePushTask implements Job {

    private static final String PUSH_DATA_ACTION_KEY = "action";
    private static final String PUSH_DATA_VALUE_KEY = "value";
    private static final String PUSH_DATA_ACTION_REQUEST_SYNC = "REQUEST_SYNC";
    public static final String PUSH_DATA_SYNC_DEVICES_KEY = "sync_devices";

    public static final String JOB_DATA_KEY_PUSH_SERVICE = "PUSH_SERVICE";
    public static final String JOB_DATA_KEY_RECEIVER = "RECEIVER";
    public static final String JOB_DATA_KEY_LOGGER = "LOGGER";
    public static final String JOB_DATA_KEY_SYNC_DEVICES = "SYNC_DEVICES";

    public static final int PUSH_MESSAGE_WAIT_TIMEOUT = 30 * 1000;

    private Logger mLogger;

    public SyncMessagePushTask() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        if(!dataMap.containsKey(PushTaskManager.JOB_DATA_KEY_PARAMETER_MAP)){
            throw new IllegalArgumentException("error!job data map parameter is null");
        }
        Map<Object,Object> parameterMap = (Map<Object,Object>)dataMap.get(PushTaskManager.JOB_DATA_KEY_PARAMETER_MAP);
        PushServiceImpl pushService = null;
        String receiver = null;
        List<String> syncDevices = null;
        if(parameterMap.containsKey(JOB_DATA_KEY_PUSH_SERVICE)){
           pushService = (PushServiceImpl) parameterMap
                .get(JOB_DATA_KEY_PUSH_SERVICE);
        }
        if(parameterMap.containsKey(JOB_DATA_KEY_LOGGER)){
           mLogger = (Logger)parameterMap.get(JOB_DATA_KEY_LOGGER);
        }
        if(parameterMap.containsKey(JOB_DATA_KEY_RECEIVER)){
           receiver = (String)parameterMap.get(JOB_DATA_KEY_RECEIVER);
        }

        if(parameterMap.containsKey(JOB_DATA_KEY_SYNC_DEVICES)){
           syncDevices = (List<String>)parameterMap.get(JOB_DATA_KEY_SYNC_DEVICES);
           mLogger.info("execute job,need to sync devices: " + syncDevices);
        }

        if (pushService != null && receiver != null && syncDevices != null) {
            try {
                JSONObject dataObj = createDataJson(syncDevices);
                pushService.push("0", receiver, dataObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (pushService == null && receiver == null && syncDevices == null) {
            throw new IllegalArgumentException("error!PushService ,syncDevices and Receiver are null");
        } else if (receiver == null) {
            throw new IllegalArgumentException("error!Receiver is null");
        } else if (pushService == null) {
            throw new IllegalArgumentException("error!PushService is null");
        } else if (syncDevices == null) {
            throw new IllegalArgumentException("error!syncDevices is null");
        }
    }

    private JSONObject createDataJson(List<String> syncDevices) throws JSONException {

        //sync devices
        JSONObject syncDeviceJson = new JSONObject();
        if(syncDevices != null){
            JSONArray syncDeviceArray = new JSONArray();
            for(String syncDevice:syncDevices){
                syncDeviceArray.put(syncDevice);
            }
            mLogger.info("createDataJson job,need to sync devices: " + syncDeviceArray.toString());
            syncDeviceJson.put(PUSH_DATA_SYNC_DEVICES_KEY,syncDeviceArray);
        }

        //data
        JSONObject dataJson = new JSONObject();
        dataJson.put(PUSH_DATA_ACTION_KEY,PUSH_DATA_ACTION_REQUEST_SYNC);
        dataJson.put(PUSH_DATA_VALUE_KEY,syncDeviceJson);
        log("======data json:" + dataJson.toString());
        return dataJson;
    }

    private void log(String msg){
        if(mLogger != null){
            mLogger.info(msg);
        }
    }

}
