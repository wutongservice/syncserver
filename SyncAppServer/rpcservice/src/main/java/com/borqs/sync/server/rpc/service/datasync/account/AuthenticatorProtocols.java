package com.borqs.sync.server.rpc.service.datasync.account;

import com.borqs.sync.avro.XAuthenticatorResponse;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/27/12
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class AuthenticatorProtocols {

    public static final XAuthenticatorResponse SUCCESS;
    public static final XAuthenticatorResponse FAIL;
    public static final XAuthenticatorResponse INVALID_USER;

    public static String generateJsonAuthResponse(String sessionId){
        JSONObject jsonRoot = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("sessionid",sessionId);
            jsonRoot.put("data",jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRoot.toString();
    }

    public static String generateSyncStatus(boolean isSyncing){
        JSONObject jsonRoot = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("syncing",isSyncing?"1":"0");
            jsonRoot.put("data",jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRoot.toString();
    }

    public final static class StatusCode {
        public static final int SUCCESS = 200;
        public static final int FAIL = 401;
        public static final int INVALID_USER = 407;

    }

    static {
        SUCCESS = new XAuthenticatorResponse();
        SUCCESS.status_code = StatusCode.SUCCESS;

        FAIL = new XAuthenticatorResponse();
        FAIL.status_code = StatusCode.FAIL;

        INVALID_USER = new XAuthenticatorResponse();
        INVALID_USER.status_code = StatusCode.INVALID_USER;

    }
    
    public static String generateJsonAuthResponse(){
        JSONObject jsonRoot = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("sessionid","111111111");
            jsonRoot.put("data",jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonRoot.toString();
    }

}
