package com.borqs.sync.server.webagent.profile;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.Photo;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import com.borqs.sync.server.webagent.util.TextUtil;
import com.borqs.sync.server.webagent.util.WebLog;

public class ProfileSyncService {
    private static final int ERR_UNKNOWN = 2120;
    private static final int ERR_NETWORK = 2121;
    private static final int ERR_DATA_PROCESS = 2122;
    
    private static Context mContext;
    
    public static void getProfileData(Context context, String uid, String ticket, 
                                      ResponseWriter writer) throws AccountException{
        mContext = context;
        logD("profile--getprofile data:" + uid + ", " + ticket);
        Map<String, String> map = new HashMap<String, String>();
        try{
            ProfileHttpAdapter adapter = new ProfileHttpAdapter(context);
            String res = adapter.getProfileOfUser(uid, ticket);     
            map.put("result", "ok");
            map.put("data", res);
            ResponseWriterUtil.writeMapJson(map, writer);
        }catch (JSONException exp){            
            logD("get profile json exception:" + exp.getMessage());
            finalErrorResponse(ERR_DATA_PROCESS, "data convert or processing exception", writer);
        } catch (IOException exp) {            
            logD("get profile io exception:" + exp.getMessage());
            finalErrorResponse(ERR_NETWORK, exp.getMessage(), writer);
        } catch (AccountException exp) {
            logD("get profile account exception:" + exp.getMessage());
            finalErrorResponse(exp.code, exp.getMessage(), writer);
        } catch (Exception exp){
            logD("get profile unknown exception:" + exp.getMessage());
            finalErrorResponse(ERR_UNKNOWN, exp.getMessage(), writer);
        }        
    }   
    
    //static int i = 0;
    public static void syncProfileData(Context context, String uid, String ticket, 
                                       String clientData, ResponseWriter writer) throws AccountException{
        mContext = context;
        logD("profile--syncProfileData data:" + uid + ", " + ticket);
        Map<String, String> map = new HashMap<String, String>();
        try{
            ProfileHttpAdapter adapter = new ProfileHttpAdapter(context);
            String serverData = adapter.getProfileOfUser(uid, ticket);       
            
            //MergeResult result = mergeProfileData(URLDecoder.decode(clientData,"UTF-8"), 
            //                                     serverData);
            MergeResult result = mergeProfileData(clientData,serverData);
            // for test only
            /*i++;
            if (i == 1){
                throw new JSONException("string at char 0");
            } else if (i == 2){
                throw new IOException("network time out, http 400");
            } else if (i == 3){
                throw new AccountException(212, "no user name");
            } else if (i == 4){
                throw new Exception("a hahaa");
            }*/
            if (!TextUtil.isEmpty(result.data)) {
                if (result.dataOwner == MergeResult.SERVER){
                    // client contentchanged, return it to client
                    map.put("data", result.data);
                } else if (result.dataOwner == MergeResult.CLIENT) {
                    // server content changed, update it
                    adapter.updateProfileData(uid, ticket, result.data);
                }
            }      
            map.put("result", "ok");
            ResponseWriterUtil.writeMapJson(map, writer);
        } catch (JSONException exp){            
            logD("sync profile json exception:" + exp.getMessage());
            finalErrorResponse(ERR_DATA_PROCESS, "data convert or processing exception", writer);
        } catch (IOException exp) {            
            logD("sync profile io exception:" + exp.getMessage());
            finalErrorResponse(ERR_NETWORK, exp.getMessage(), writer);
        } catch (AccountException exp) {
            logD("sync profile account exception:" + exp.getMessage());
            finalErrorResponse(exp.code, exp.getMessage(), writer);
        } catch (Exception exp){
            logD("sync profile unknown exception:" + exp.getMessage());
            finalErrorResponse(ERR_UNKNOWN, "unknow exception", writer);
        }        
    }   
    
    private static void finalErrorResponse(int errCode, String errMsg, ResponseWriter writer){
        try {
            Map<String, String> map = new HashMap<String, String>();
            
            map.put("result", "failed");
            map.put("error_code", String.valueOf(errCode));
            map.put("error", errMsg);
            
            ResponseWriterUtil.writeMapJson(map, writer);
        } catch (Exception exp){
            
        }
    }
        
    private static MergeResult mergeProfileData(String clientData, String serverData) throws JSONException{
        JSONObject serverJson = new JSONObject(serverData);
        Contact serverContact = JProfileConverter.toProfileStruct(serverJson);
        long serverUpdTime = serverJson.getLong("modify_time");
        
        JSONObject clientJson = new JSONObject(clientData);        
        Contact clientContact = JProfileConverter.toContactProfile(clientData);
        long clientUpdTime = clientJson.getLong("modify_time");
     
        //logD("profile--sync merger client data:" + clientData);
        //logD("profile--sync merger server data:" + serverData);
        logD("profile--sync merger time:" + serverUpdTime + ", " + clientUpdTime);
        
        MergeResult result = new MergeResult();
        Contact mergeContact = null;
        if (serverUpdTime >= clientUpdTime){
            //server has the latest data
            result.dataOwner = MergeResult.SERVER;
            mergeContact = mergeContact(serverContact, clientContact);
            
        } else {
            result.dataOwner = MergeResult.CLIENT;
            mergeContact = mergeContact(clientContact, serverContact);            
        }
        
        result.data = JProfileConverter.toContactJson(mergeContact);logD("sync 1-4");
        return result;
    }
    
    /**
     * merge ct1&ct2 content, if different, keep ct1's content
     * currently no policy for merge process: only keep the latest data(ct1)
     * @param ct1
     * @param ct2
     * @return
     */
    private static Contact mergeContact(Contact ct1, Contact ct2){
        //name:first,middle,last
        // nickname        
        // display name        
        // phone        
        // email        
        // birthday        
        // organization        
        // address
        // im        
        // website        
        // photo
        
        return ct1;
    }
    
    private static void logD(String msg){
        WebLog.getLogger(mContext).info(msg);
    }
    
    private static class MergeResult{
        private static final int SERVER = 1;
        private static final int CLIENT = 2;
        String data;
        int dataOwner;
    }
}
