package com.borqs.sync.server.syncml.push;

import com.borqs.json.JSONArray;
import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import com.borqs.sync.server.common.push.PushServiceImpl;
import com.borqs.sync.server.common.push.PushTaskManager;
import com.borqs.sync.server.common.util.LogHelper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 12/27/11
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncMessagePushTask implements Job {

    private static final String TAG = "SyncMessagePushTask";

    private static final String PUSH_DATA_ACTION_KEY = "action";
    private static final String PUSH_DATA_VALUE_KEY = "value";
    private static final String PUSH_SYNC_DEVICES_KEY = "sync_devices";
    private static final String PUSH_DATA_ACTION_REQUEST_SYNC = "REQUEST_SYNC";

    public static final String JOB_DATA_KEY_LOGGER = "LOGGER";
    public static final String JOB_DATA_PARAMETER_PUSH_SERVICE = "push_service";
    public static final String JOB_DATA_PARAMETER_RECEIVER = "receiver";
    public static final String JOB_DATA_PARAMETER_SYNC_DEVICES = "sync_devices";

    public static final long WAIT_SYNC = 30 * 1000;

    private Logger mLogger;

    public SyncMessagePushTask() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        if (!dataMap.containsKey(PushTaskManager.JOB_DATA_KEY_PARAMETER_MAP)) {
            throw new IllegalArgumentException("error!job data map parameter is null");
        }
        Map<Object, Object> parameterMap = (Map<Object, Object>) dataMap.get(PushTaskManager.JOB_DATA_KEY_PARAMETER_MAP);

        if (parameterMap.containsKey(JOB_DATA_KEY_LOGGER)) {
            mLogger = (Logger) parameterMap.get(JOB_DATA_KEY_LOGGER);
        }
        PushServiceImpl pushService = null;
        String receiver = null;
        List<String> deviceList = null;
        //push_service
        if (parameterMap.containsKey(JOB_DATA_PARAMETER_PUSH_SERVICE)) {
            pushService = (PushServiceImpl) parameterMap.get(JOB_DATA_PARAMETER_PUSH_SERVICE);
        }
        //receiver
        if (parameterMap.containsKey(JOB_DATA_PARAMETER_RECEIVER)) {
            receiver = (String) parameterMap.get(JOB_DATA_PARAMETER_RECEIVER);
            LogHelper.logD(mLogger, "============get receiver : " + receiver);
        }

        //sync_devices
        if (parameterMap.containsKey(JOB_DATA_PARAMETER_SYNC_DEVICES)) {
            deviceList = (List<String>) parameterMap.get(JOB_DATA_PARAMETER_SYNC_DEVICES);
        }

        if (pushService != null && receiver != null) {
            try {
                JSONObject dataObj = createDataJson(deviceList);
                pushService.push("0", receiver, dataObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (pushService == null && receiver == null && deviceList == null) {
            throw new IllegalArgumentException("error!,no PushService ,deviceList,and Receiver");
        } else if (receiver == null) {
            throw new IllegalArgumentException("error! no Receiver");
        } else if (pushService == null) {
            throw new IllegalArgumentException("error! no PushService");
        } else if (deviceList == null) {
            throw new IllegalArgumentException("error! no deviceList");
        }
    }

    private JSONObject createDataJson(List<String> syncDevices) throws JSONException {
        //sync devices
        JSONObject deviceJson = new JSONObject();
        if (syncDevices != null) {
            JSONArray deviceArray = new JSONArray();
            for (String device : syncDevices) {
                deviceArray.put(device);
            }
            deviceJson.put(PUSH_SYNC_DEVICES_KEY, deviceArray);
        }

        //data
        JSONObject dataJson = new JSONObject();
        dataJson.put(PUSH_DATA_ACTION_KEY, PUSH_DATA_ACTION_REQUEST_SYNC);
        dataJson.put(PUSH_DATA_VALUE_KEY, deviceJson);
        LogHelper.logD(mLogger, "============get push devices dataJson : " + dataJson.toString());
        return dataJson;
    }

}
