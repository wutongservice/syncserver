package com.borqs.sync.server.webagent.fastlogin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.borqs.sync.server.common.account.adapters.BPCAccountHttpAdapter;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.profile.ProfileHttpAdapter;
import com.borqs.sync.server.webagent.service.ConfigService;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import com.borqs.sync.server.webagent.util.TextUtil;
import com.borqs.sync.server.webagent.util.WebLog;
import org.apache.commons.lang.math.RandomUtils;

/**
 * Created by IntelliJ IDEA.
 * User: b335
 * Date: 12-5-23
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public class LoginImpl {

    //////////for fast logion//////////////////////
    private static final String TAG_NAME = "name";
    private static final String TAG_PASS = "pass";
    private static final String TAG_GUID = "guid";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_SIM = "sim";
    private static final String TAG_DEVICE_ID = "device_id";    
    private static final String TAG_VERIFYCODE = "verifycode";
    private static final String TAG_MSGFMT = "msgfmt";
    private static final String TAG_ERROR = "error";
    private static final String TAG_ERROR_CODE = "error_code";
    private static final String TAG_RESULT = "result";
    private static final String TAG_CREATE = "create";
    private static final String TAG_FIELD = "fieldandvalue";
    private static final String TAG_PWD_TO = "pwd_to";
    private static final String TAG_VERIFY_CODE_TO = "verify_code_to";
    private static final String TAG_TICKET = "ticket";
    private static final String TAG_PHOTO= "photo";
    
    private static final int ERR_UNKNOWN = 1020;
    private static final int ERR_NO_SIM = 1021;
    private static final int ERR_NO_PHONE = 1022;
    private static final int ERR_NO_USER = 1023;
    private static final int ERR_NO_RECORD = 1024;
    private static final int ERR_NO_VERIFY_CODE = 1025;
    private static final int ERR_VERIFY_CODE = 1026;
    private static final int ERR_VERIFY_CODE_OUT = 1027;
    private static final int ERR_USER_NAME_INVALID = 1028;
    private static final int ERR_SMS_SERVER_NOT_WORKING = 1029;
    private static final int ERR_ACCOUNT_SERVER = 1030;
    
    private static final String SMS_DOWN_SERVER_NUMBER = "sms_down_server_num";
    private static final String SMS_NOT_WORKING = "NotWork";
    private static final String SMS_SUB_URL = "/sendsms";
    private static final String SMS_PARAM = "appname=%s&data={\"from\":\"\",\"to\":\"%s\",\"subject\":\"%s\"}";

    private static String dfltEncName = "UTF-8";
    
    private static int CONTENT_TO_NONE = 0;
    private static int CONTENT_TO_PHONE = 1;
    private static int CONTENT_TO_MAIL = 2;

    private static void printMap(Context context, Map<String, String> map){
        Set<String> keys = map.keySet();
        for(String key : keys){
            WebLog.getLogger(context).info(key+":"+map.get(key));
        }
    }
    
    private static void handleExceptionResponse(String expMsg, ResponseWriter writer){
        try {
            Map<String, String> map = new HashMap<String, String>();
            
            map.put("result", "failed");
            map.put(TAG_ERROR_CODE, String.valueOf(ERR_ACCOUNT_SERVER));
            map.put(TAG_ERROR, expMsg);
            
            ResponseWriterUtil.writeMapJson(map, writer);
        } catch (Exception exp){
            
        }
    }

    //此方法总是被短信服务器调用,生成GUID以提供查询,，没有encode
    public static void verifyBySim(Context context, String data, ResponseWriter writer) {
       try {
            WebLog.getLogger(context).info("ServletImp-verifyBySim:"+data);
            Map<String, String> map = new HashMap<String, String>();
            JSONObject ro = new JSONObject(data);
            String phone = ro.getString("from");
            String json = ro.getString("subject");
            JSONObject obj = new JSONObject(json);
            
            String sim = obj.optString(TAG_DEVICE_ID);
            if (TextUtil.isEmpty(sim)){
                sim = obj.getString(TAG_SIM);
            }
            if(TextUtil.isEmpty(sim)||TextUtil.isEmpty(phone)){
                WebLog.getLogger(context).info("ServletImp-verifyBySim:"+"sim or phone is empty.");
                map.put(TAG_ERROR,"no_sim");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_SIM));                
                ResponseWriterUtil.writeMapJson(map, writer);
                return;
            }
            UserSimPhoneDAO dao = new UserSimPhoneDAO(context);
            String guid = dao.getGuidByPhone(phone);
            if(TextUtil.isEmpty(guid)){
                guid = UUID.randomUUID().toString();
                dao.addUserSimPhoneData(guid,phone,sim,"");
            }
            else{
                dao.updatetSim(guid,sim);
            }
            try {
                map.put(TAG_RESULT,"ok");
                ResponseWriterUtil.writeMapJson(map,writer);
                printMap(context, map);
            } catch (IOException e) {
                handleExceptionResponse(e.getMessage(), writer);
            }
        } catch (Exception e) {
            WebLog.getLogger(context).info("ServletImp-verifyBySim Exception:"+e.getMessage());
            e.printStackTrace();
            handleExceptionResponse(e.getMessage(), writer);
        }
        WebLog.getLogger(context).info("ServletImp-verifyBySim end");
    }
    //此方法由客户端直接调用，下发验证码
    public static void verifyNoSim(Context context, String data, ResponseWriter writer) throws AccountException{
        try {
            data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-verifyNoSim:"+data);
            Map<String, String> map = new HashMap<String, String>();

            JSONObject ro = new JSONObject(data);
            String userName = ro.optString(TAG_NAME);
            if(TextUtil.isEmpty(userName)){ // for previous version compatible
               userName = ro.getString(TAG_PHONE); 
            } 
            if(TextUtil.isEmpty(userName)){
                WebLog.getLogger(context).info("ServletImp-verifyNoSim:phone is empty.");
                map.put(TAG_ERROR,"no user name");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_USER));                
                ResponseWriterUtil.writeMapJson(map, writer);
                return;
            } else if (!TextUtil.isVaildPhoneNumber(userName) 
                       && !TextUtil.isVaildMailAddress(userName)){
                WebLog.getLogger(context).info("ServletImp-verifyNoSim:invalid user name");
                map.put(TAG_ERROR,"invalid user name");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_USER_NAME_INVALID));                
                ResponseWriterUtil.writeMapJson(map, writer);
                return;
            }
            
            //phone, check sms server is OK or not
            if (TextUtil.isVaildPhoneNumber(userName)){
                String smsServer = new ConfigService().getConfig(context, SMS_DOWN_SERVER_NUMBER);
                if ((smsServer != null) && (smsServer.equalsIgnoreCase(SMS_NOT_WORKING))){
                    WebLog.getLogger(context).info("ServletImp-verifyNoSim:sms down server stop working");
                    map.put(TAG_ERROR, SMS_NOT_WORKING);
                    map.put(TAG_ERROR_CODE,String.valueOf(ERR_SMS_SERVER_NOT_WORKING));                
                    ResponseWriterUtil.writeMapJson(map, writer);
                    return;
                }
            }
            
            UserSimPhoneDAO dao = new UserSimPhoneDAO(context);
            String guid = dao.getGuidByPhone(userName);
            String verifycode = genVerifyCode();
            if(TextUtil.isEmpty(guid)){
                WebLog.getLogger(context).info("ServletImp-verifyNoSim:no guid");
                guid = UUID.randomUUID().toString();
                dao.addUserSimPhoneData(guid, userName, "",verifycode);
            } else{
                WebLog.getLogger(context).info("ServletImp-verifyNoSim:update guid");
                dao.updateVerifyCode(userName,verifycode);
            }
            String subject = verifycode;
            if(ro.has(TAG_MSGFMT)){
                String msgfmt = ro.getString(TAG_MSGFMT);
                subject = msgfmt.replace("<CODE>",verifycode);
                //String.format(msgfmt,verifycode);
                if(subject.length()>50){
                    subject = subject.substring(0,50);
                }
            }
            
            int to = sendVerifyCode(context, userName, subject);            
            if (to == CONTENT_TO_PHONE){
                map.put(TAG_VERIFY_CODE_TO, "phone");
            } else if (to == CONTENT_TO_MAIL){
                map.put(TAG_VERIFY_CODE_TO, "mail");
            }
            
            map.put(TAG_RESULT,"ok");
            ResponseWriterUtil.writeMapJson(map,writer);
            printMap(context, map);
        } catch (Exception e) {            
            handleExceptionResponse(e.getMessage(), writer);
            e.printStackTrace();
            WebLog.getLogger(context).info("ServletImp-verifyNoSim Exception:"+e.getMessage());
            //throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);  
        }        
    }
    
    private static int sendVerifyCode(final Context context, final String toUser,final String content){        
        int to = CONTENT_TO_NONE;
        if (TextUtil.isVaildPhoneNumber(toUser)){
            WebLog.getLogger(context).info("ServletImp-verifyNoSim:sendVerifyCode to phone");
            //下行短信
            to = CONTENT_TO_PHONE;
            String smsHost = context.getConfig().getSetting(ConfigurationBase.SMS_HOST);
            String appName = context.getConfig().getSetting(ConfigurationBase.SMS_APP_NAME);
            String smsUrl = smsHost + SMS_SUB_URL;            
            visitUrl(context,smsUrl,
                    String.format(SMS_PARAM,appName,toUser,content));
        } else if (TextUtil.isVaildMailAddress(toUser)){
            WebLog.getLogger(context).info("ServletImp-verifyNoSim:sendVerifyCode to mail");
            // 邮件下发
            to = CONTENT_TO_MAIL;
            //new Thread(new Runnable(){
             //   public void run(){
                    new MailSender(context).sendMessage(toUser, content);
               // }
           // }).start();
        }
        
        return to;
    }

    private static String genVerifyCode()
    {
        return ""+
                RandomUtils.nextInt(10) +
                RandomUtils.nextInt(10)+
                RandomUtils.nextInt(10)+
                RandomUtils.nextInt(10);
    }

    private static String visitUrl(Context context,String _url,String _param)
    {
        WebLog.getLogger(context).info("visitUrl:"+_url+"?"+_param);
        try {
            URL url = new URL(_url);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //String _param = Param_Sms+"="+sms;
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            OutputStream out = urlConn.getOutputStream();
            out.write(_param.getBytes("UTF-8"));
            out.flush();
            out.close();
            //maybe no response
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream(), "UTF-8"));

            StringBuffer sb = new StringBuffer();
            int ch;
            while ((ch = rd.read()) > -1) {
                sb.append((char) ch);
            }
            WebLog.getLogger(context).info(sb.toString());
            rd.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();

            WebLog.getLogger(context).info("ServletImp-visitUrl Exception:"+e.getMessage());
            return null;
        }
    }
    //此方法由客户端直接调用，获取GUID
    //需要的凭据为SIM卡ID
    public static void getGUIDBySim(Context context, String data, ResponseWriter writer) {
      try {
            data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-getGUIDBySim:" + data);
            Map<String, String> map = new HashMap<String, String>();

            JSONObject ro = new JSONObject(data);
            UserSimPhoneDAO dao = new UserSimPhoneDAO(context);
            String sim = ro.optString(TAG_DEVICE_ID);
            if (TextUtil.isEmpty(sim)){
                sim = ro.getString(TAG_SIM);
            } 
            String guid = dao.getGuidBySim(sim);
            if(TextUtil.isEmpty(guid)){
                map.put(TAG_RESULT,"");
            }
            else {
                String phone = dao.getPhoneByGuid(guid);
                map.put(TAG_RESULT,guid);
                map.put(TAG_PHONE,phone);
            }
            
            ResponseWriterUtil.writeMapJson(map, writer);
            printMap(context, map);
            WebLog.getLogger(context).info("ServletImp-getGUIDBySim finished");
        } catch (Exception e) {
            handleExceptionResponse(e.getMessage(), writer);
            WebLog.getLogger(context).info("ServletImp-getGUIDBySim Exception:"+e.getMessage());
            e.printStackTrace();
        }
        WebLog.getLogger(context).info("ServletImp-getGUIDBySim end");
    }
    //此方法由客户端直接调用，获取GUID
    //需要的凭据为电话号码+验证码
    public static void getGUIDByVerifyCode(Context context, String data, ResponseWriter writer) {

       try {
            data = URLDecoder.decode(data,dfltEncName);
           WebLog.getLogger(context).info("ServletImp-getGUIDByVerifyCode:"+data);
           Map<String, String> map = new HashMap<String, String>();

           JSONObject ro = new JSONObject(data);
            UserSimPhoneDAO dao = new UserSimPhoneDAO(context);
            String userName = ro.optString(TAG_NAME);
            if(TextUtil.isEmpty(userName)){ // for previous version compatible
               userName = ro.getString(TAG_PHONE); 
            }
            String verifycode = ro.getString(TAG_VERIFYCODE);
            String very = dao.getVerifyByPhone(userName);
            if(null == very){
                map.put(TAG_ERROR,"no_record");
                map.put(TAG_ERROR_CODE, String.valueOf(ERR_NO_RECORD));
            }
            else if(TextUtil.isEmpty(very)) {
                map.put(TAG_ERROR,"code_is_empty");
                map.put(TAG_ERROR_CODE, String.valueOf(ERR_NO_VERIFY_CODE));
            }
            else if(verifycode.equals(very)){
                dao.updateVerifyCode(userName,"");
                dao.updatetExtra(userName,0);
                String guid = dao.getGuidByPhone(userName);
                map.put(TAG_RESULT,guid);
            } else {//not equal
                int times = dao.addAndGetExtraCode(userName);
                WebLog.getLogger(context).info("ServletImp-getGUIDByVerifyCode-cmp times:"+times);
                if(times>3){
                    dao.updateVerifyCode(userName,"");
                    dao.updatetExtra(userName, 0);
                    map.put(TAG_ERROR,"cmp_gt_3");
                    map.put(TAG_ERROR_CODE, String.valueOf(ERR_VERIFY_CODE_OUT));
                } else {
                    map.put(TAG_ERROR,"cmp_failed");
                    map.put(TAG_ERROR_CODE, String.valueOf(ERR_VERIFY_CODE));
                }
            }

            ResponseWriterUtil.writeMapJson(map, writer);
            printMap(context, map);
        } catch (Exception e) {
           handleExceptionResponse(e.getMessage(), writer);
           WebLog.getLogger(context).info("ServletImp-getGUIDByVerifyCode Exception:"+e.getMessage());
           e.printStackTrace();
        }
        WebLog.getLogger(context).info("ServletImp-getGUIDByVerifyCode end");
    }
    //此方法由客户端直接调用，凭借用户名密码登录
    public static void normalLogin(Context context, String data, ResponseWriter writer) {
        try {
            data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-normalLogin:"+data);
            Map<String, String> map = new HashMap<String, String>();

            JSONObject ro = new JSONObject(data);
            String name = ro.getString(TAG_NAME);
            String pass = ro.getString(TAG_PASS);
            String accountHost = context.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
            BPCAccountHttpAdapter adapter = new BPCAccountHttpAdapter(accountHost);

            String ret = adapter.login(name,pass,"0");
            map.put(TAG_RESULT,ret);
            ResponseWriterUtil.writeMapJson(map,writer);
            printMap(context, map);
        } catch (Exception e) {
            handleExceptionResponse(e.getMessage(), writer);
            e.printStackTrace();
            WebLog.getLogger(context).info("ServletImp-normalLogin Exception:"+e.getMessage());
        }
        WebLog.getLogger(context).info("ServletImp-normalLogin end");
    }

    //此方法由客户端直接调用，凭借GUID快速登录
    public static void fastLogin(Context context, String data, ResponseWriter writer) {
        try {
            data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-fastLogin:"+data);
            Map<String, String> map = new HashMap<String, String>();
            JSONObject ro = new JSONObject(data);
            UserSimPhoneDAO dao = new UserSimPhoneDAO(context);
            String guid = ro.getString(TAG_GUID);
            String phone = dao.getPhoneByGuid(guid);
            WebLog.getLogger(context).info("ServletImp-fastLogin phone"+phone);
            if(TextUtil.isEmpty(phone)){
                map.put(TAG_ERROR,"no_phone");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_PHONE));                
                ResponseWriterUtil.writeMapJson(map, writer);
                WebLog.getLogger(context).info("ServletImp-fastLogin:guid ["+guid+"] not found");
                return;
            }
            String accountHost = context.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
            BPCAccountHttpAdapter adapter = new BPCAccountHttpAdapter(accountHost);
            try {
                String userID = adapter.findUserIdByUserName(phone);
                WebLog.getLogger(context).info("ServletImp-fastLogin user"+userID);
                if(userID.equals("0")){
                    String pass = MD5.toMd5(guid.getBytes()).toUpperCase();
                    String ret = "";
                    if (TextUtil.isVaildPhoneNumber(phone)){
                        ret = adapter.createAccount("",phone,pass,phone,"","","","","");
                    } else { //email
                        ret = adapter.createAccount(phone,"",pass,phone,"","","","","");
                    }
                    map.put(TAG_CREATE,"true");
                    //ret: account borqs_id
                    WebLog.getLogger(context).info("ServletImp-fastLogin:createAccount:"+ret);
                }
                String passwd = "3E32F8E32574E143B061D731BA78A515";
                String ret = adapter.login(phone,passwd,"0");
                map.put(TAG_RESULT,ret);
                ResponseWriterUtil.writeMapJson(map,writer);
                printMap(context, map);
            } catch (IOException e) {
                handleExceptionResponse(e.getMessage(), writer);
                e.printStackTrace();
                WebLog.getLogger(context).info("ServletImp-fastLogin Exception1:"+e.getMessage());
            } catch (AccountException e) {
                handleExceptionResponse(e.getMessage(), writer);
                e.printStackTrace();
                WebLog.getLogger(context).info("ServletImp-fastLogin Exception2:"+e.getMessage());
            }
        } catch (Exception e) {
            handleExceptionResponse(e.getMessage(), writer);
            e.printStackTrace();
            WebLog.getLogger(context).info("ServletImp-fastLogin Exception3:"+e.getMessage());

        }
        WebLog.getLogger(context).info("ServletImp-fastLogin end");
    }

    public static void changePassword(Context context, String data, ResponseWriter writer) {
        try {
            data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-changePassword:"+data);
            Map<String, String> map = new HashMap<String, String>();

            UserSimPhoneDAO dao = new UserSimPhoneDAO(context);
            JSONObject ro = new JSONObject(data);

            String guid = ro.getString(TAG_GUID);
            String pass = ro.getString(TAG_PASS);
            String phone = dao.getPhoneByGuid(guid);
            if(TextUtil.isEmpty(phone)){
                map.put(TAG_ERROR,"no_phone");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_PHONE));                
                ResponseWriterUtil.writeMapJson(map, writer);
                WebLog.getLogger(context).info("ServletImp-changePassword:guid ["+guid+"] not found");
                return;
            }

            String accountHost = context.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
            BPCAccountHttpAdapter adapter = new BPCAccountHttpAdapter(accountHost);
            String userID = adapter.findUserIdByUserName(phone);
            if(userID.equals("0")){
                map.put(TAG_ERROR,"no_user");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_USER));                
                ResponseWriterUtil.writeMapJson(map, writer);
                WebLog.getLogger(context).info("ServletImp-changePassword:guid ["+guid+"] not found");
                return;
            }
            if(adapter.updatePassword(userID,pass))
                map.put(TAG_RESULT,"ok");
            else
                map.put(TAG_RESULT,"error");
            ResponseWriterUtil.writeMapJson(map,writer);
            printMap(context, map);
        } catch (Exception e) {
            handleExceptionResponse(e.getMessage(), writer);
            e.printStackTrace();
            WebLog.getLogger(context).info("ServletImp-changePassword Exception:"+e.getMessage());

        }
        WebLog.getLogger(context).info("ServletImp-changePassword end");

    }

    public static void modifyField(Context context, String data, ResponseWriter writer) {
        try {
            data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-modifyField:"+data);
            Map<String, String> map = new HashMap<String, String>();

            UserSimPhoneDAO dao = new UserSimPhoneDAO(context);
            JSONObject ro = new JSONObject(data);

            String guid = ro.getString(TAG_GUID);
            String fields = ro.getString(TAG_FIELD);
            String phone = dao.getPhoneByGuid(guid);
            if(TextUtil.isEmpty(phone)){
                map.put(TAG_ERROR,"no_phone");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_PHONE));                
                ResponseWriterUtil.writeMapJson(map, writer);
                WebLog.getLogger(context).info("ServletImp-modifyField:guid ["+guid+"] not found");
                return;
            }

            String accountHost = context.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
            BPCAccountHttpAdapter adapter = new BPCAccountHttpAdapter(accountHost);
            String userID = adapter.findUserIdByUserName(phone);
            if(userID.equals("0")){
                map.put(TAG_ERROR,"no_user");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_USER));                
                ResponseWriterUtil.writeMapJson(map, writer);
                WebLog.getLogger(context).info("ServletImp-modifyField:guid ["+guid+"] not found");
                return;
            }
            if(adapter.updateField(userID,fields))
                map.put(TAG_RESULT,"ok");
            else{
                map.put(TAG_ERROR,"update failed");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_UNKNOWN));
            }
            printMap(context, map);
            ResponseWriterUtil.writeMapJson(map,writer);
        } catch (Exception e) {
            handleExceptionResponse(e.getMessage(), writer);
            e.printStackTrace();
            WebLog.getLogger(context).info("ServletImp-modifyField Exception:"+e.getMessage());

        }
        WebLog.getLogger(context).info("ServletImp-modifyField end");
    }
    
    public static void getNewPassword(Context context, String data, ResponseWriter writer) throws AccountException{
        try {
            data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-getpwd:"+data);
            
            Map<String, String> map = new HashMap<String, String>();
            JSONObject ro = new JSONObject(data);
            
            String userName = ro.getString(TAG_NAME);
            if (TextUtil.isEmpty(userName))  {
                map.put(TAG_ERROR,"no_user_name");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_USER));                
                ResponseWriterUtil.writeMapJson(map, writer);
                WebLog.getLogger(context).info("ServletImp-getpwd: no user pass in");
                return;
            }
            try {
                String accountHost = context.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
                BPCAccountHttpAdapter adapter = new BPCAccountHttpAdapter(accountHost);
                String ret = null;
                if (TextUtil.isVaildPhoneNumber(userName)){
                  //phone, check sms server is OK or not
                    String smsServer = new ConfigService().getConfig(context, SMS_DOWN_SERVER_NUMBER);
                    if ((smsServer != null) && (smsServer.equalsIgnoreCase(SMS_NOT_WORKING))){
                        WebLog.getLogger(context).info("ServletImp-getpwd:sms down server stop working");
                        map.put(TAG_ERROR, SMS_NOT_WORKING);
                        map.put(TAG_ERROR_CODE,String.valueOf(ERR_SMS_SERVER_NOT_WORKING));                
                        ResponseWriterUtil.writeMapJson(map, writer);
                    } else {
                        WebLog.getLogger(context).info("ServletImp-getpwd by phone");
                        ret = adapter.getNewPasswordByPhone(userName);
                        map.put(TAG_PWD_TO, "phone");
                    }
                } else if (TextUtil.isVaildMailAddress(userName)){
                    WebLog.getLogger(context).info("ServletImp-getpwd by mail");
                    ret = adapter.getNewPasswordByMail(userName);
                    map.put(TAG_PWD_TO, "mail");
                } else {      
                    WebLog.getLogger(context).info("ServletImp-getpwd invalid username:" + userName);
                    map.put(TAG_ERROR,"user_name_invalid");
                    map.put(TAG_ERROR_CODE,String.valueOf(ERR_USER_NAME_INVALID));
                }
                if (ret != null){
                    map.put(TAG_RESULT, ret);
                }
            } catch (AccountException ae){
                WebLog.getLogger(context).info("ServletImp-getpwd exp:" + ae.getMessage());
                map.put("error_msg", ae.getMessage());
                map.put(TAG_ERROR_CODE, String.valueOf(ae.code));
            }
            printMap(context, map);
            ResponseWriterUtil.writeMapJson(map,writer);
        } catch (Exception e) {
            handleExceptionResponse(e.getMessage(), writer);
            e.printStackTrace();
            WebLog.getLogger(context).info("ServletImp-get pwd Exception:"+e.getMessage());
            //throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);            
        }
    }
    
    public static void changePhoto(Context context, String ticket, String data, ResponseWriter writer) throws AccountException{
        try {
            //data = URLDecoder.decode(data,dfltEncName);
            WebLog.getLogger(context).info("ServletImp-change photo begin");
            
            Map<String, String> map = new HashMap<String, String>();
            if (TextUtil.isEmpty(ticket))  {
                map.put(TAG_ERROR,"no_user_name");
                map.put(TAG_ERROR_CODE,String.valueOf(ERR_NO_USER));                
                ResponseWriterUtil.writeMapJson(map, writer);
                WebLog.getLogger(context).info("ServletImp-change photo: no ticket in");
                return;
            }
            try {
                String accountHost = context.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);                
                byte[] photoData = Base64.decode(data, Base64.DEFAULT);
                if (photoData != null ){
                    ProfileHttpAdapter adapter = new ProfileHttpAdapter(context);
                    if(adapter.updateProfilePhoto(ticket, photoData)){
                        map.put(TAG_RESULT,"ok");
                    } else{
                        map.put(TAG_RESULT,"error");
                    }
                } else {
                    WebLog.getLogger(context).info("ServletImp-change photo no photo data, no update:");
                    map.put(TAG_RESULT,"ok");
                }                
            } catch (AccountException ae){
                WebLog.getLogger(context).info("ServletImp-change photo exp:" + ae.getMessage());
                map.put("error_msg", ae.getMessage());
                map.put(TAG_ERROR_CODE, String.valueOf(ae.code));
            }
            printMap(context, map);
            ResponseWriterUtil.writeMapJson(map,writer);
        } catch (Exception e) {
            handleExceptionResponse(e.getMessage(), writer);
            e.printStackTrace();
            WebLog.getLogger(context).info("ServletImp-change photo Exception:"+e.getMessage());
            //throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);            
        }
    }
}
