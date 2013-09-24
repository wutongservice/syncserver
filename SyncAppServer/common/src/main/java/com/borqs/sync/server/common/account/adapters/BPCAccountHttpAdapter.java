/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account.adapters;

import com.borqs.sync.server.common.account.CircleList;
import com.borqs.sync.server.common.account.ProfileRecord;
import com.borqs.sync.server.common.account.ProfileRecordList;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.util.Utility;
import org.apache.commons.io.IOUtils;
import org.mortbay.util.UrlEncoded;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Date: 2/28/12
 * Time: 3:31 PM
 * Borqs project
 */
public class BPCAccountHttpAdapter {
    
    public static final int HTTP_CONNECTION_TIME_OUT = 60*1000;
    public static final int HTTP_READ_TIME_OUT = 60*1000;
    
    private static final String USER_QUERY = "internal/getUsers?viewerId=%s&userIds=%s&cols=%s&privacyEnabled=%s";
    private static final String FRIENDS_QUERY = "internal/getFriends?viewerId=%s&userId=%s&circleIds=%s&cols=%s&page=0&count=10000";
    private static final String CREATE_ACCOUNT = "internal/createAccount?loginEmail1=%s&loginPhone1=%s&pwd=%s&displayName=%s&gender=%s&imei=%s&imsi=%s&device=%s&location=%s";
    private static final String FIND_USER = "internal/findUserIdByUserName?username=%s";
    private static final String UPDATE_PASSWORD = "internal/updateAccount?userId=%s&user=%s";
    private static final String CIRCLE_QUERY = "internal/getCircles?user=%s&circleIds=%s&withUsers=%s&with_public_circles=%s";
    private static final String GET_PWD_BY_PHONE = "account/reset_password_for_phone?phone=%s";
    private static final String GET_PWD_BY_MAIL = "account/reset_password?login_name=%s";

    private static final String DETAILED_INFO = "login_phone1,login_phone2,login_phone3,login_email1,login_email2,login_email3,domain_name,profession,image_url,office_address,middle_name,last_name,languages,job_title,interests,gender,first_name,contact_info,display_name,department,company,about_me,birthday,address";
    private static final String BRIEF_INFO = "user_id, basic_updated_time, profile_updated_time, contact_info_updated_time, address_updated_time";
    private static final String CIRCLE_RELATION_INFO = "in_circles";

    private static final String USER_LOGIN = "internal/login?name=%s&password=%s&appId=%s";
    
    private static final boolean DEBUG = false;

    private String mAccountServerHost;
    private Logger mLogger;
    
    public BPCAccountHttpAdapter(String serverHost){
        mAccountServerHost = serverHost;
    }

    public void setLogger(Logger logger){
        mLogger = logger;
    }

    //internal/getUsers?viewerId=xx&userIds=xx&cols=xx&privacyEnabled=true
    public ProfileRecord getProfileOfUser(String myId, String userId, boolean privacy) throws Exception {
        String cmd = String.format(USER_QUERY, myId, userId,  UrlEncoded.encodeString(DETAILED_INFO), String.valueOf(privacy));
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        connection.setConnectTimeout(HTTP_CONNECTION_TIME_OUT);
        connection.setReadTimeout(HTTP_READ_TIME_OUT);
        logD("openConnection finished ");
        try{
            Reader result = getResponseAsReader(connection);
            if(DEBUG){
                result = copyAnddump(result);
            }
            ProfileRecordList profiles = ProfileRecordList.parseProfileList(result,mLogger);
            if(profiles.size()>0){
                return profiles.get(0);
            } else {
                return null;
            }

        }finally {
            connection.disconnect();
        }
    }

    //internal/getUsers?viewerId=xx&userIds=xx&cols=in_circles&privacyEnabled=true
    public Map<String, List<String>> getRelationWithUser(String myId, List<String> userIds) throws Exception{
        String cmd = String.format(USER_QUERY, myId, toArgumentString(userIds),  UrlEncoded.encodeString(CIRCLE_RELATION_INFO), String.valueOf(true));
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        connection.setConnectTimeout(HTTP_CONNECTION_TIME_OUT);
        connection.setReadTimeout(HTTP_READ_TIME_OUT);
        logD("openConnection finished ");
        try{
            Reader result = getResponseAsReader(connection);
            if(DEBUG){
                result = copyAnddump(result);
            }
            return parseCircles(result);
        }finally {
            connection.disconnect();
        }
    }

    //internal/getFriends0?userId=xx&circleIds=xx&page=0&count=20
    public ProfileRecordList getFriends(String myId) throws Exception {
        String cmd = String.format(FRIENDS_QUERY, myId, myId, "1", UrlEncoded.encodeString(BRIEF_INFO));
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader result = getResponseAsReader(connection);;
            if(DEBUG){
                result = copyAnddump(result);
            }
            ProfileRecordList profiles = ProfileRecordList.parseProfileList(result, mLogger);
            return profiles;
        }finally {
            connection.disconnect();
        }
    }

    public CircleList getCircles(String myId, int[] circleIds, boolean withBuddy) throws IOException {
        String sIds = Utility.array2String(circleIds);
        String cmd = String.format(CIRCLE_QUERY, myId, sIds, String.valueOf(withBuddy), String.valueOf(false));
        String cmdURL = mAccountServerHost + "/" + cmd;
        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader result = getResponseAsReader(connection);;
            if(DEBUG){
                result = copyAnddump(result);
            }
            CircleList circles = CircleList.createFrom(result);
            return circles;
        }finally {
            connection.disconnect();
        }
    }

    public String createAccount(String loginEmail,
                                 String loginPhone,
                                 String pwd,
                                 String displayName,
                                 String gender,
                                 String imei,
                                 String imsi,
                                 String device,
                                 String location) throws IOException, AccountException {
        String cmd = String.format(CREATE_ACCOUNT, loginEmail, loginPhone, pwd, displayName,
                gender, imei, imsi, device, location, location);
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader reader = getResponseAsReader(connection);
            String resp = IOUtils.toString(reader);
            return parseStringResult(resp);
        } finally {
            connection.disconnect();
        }
    }

    public String findUserIdByUserName(String username) throws IOException, AccountException {
        String cmd = String.format(FIND_USER, username);
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader reader = getResponseAsReader(connection);
            String resp = IOUtils.toString(reader);
            return parseStringResult(resp);
        }finally {
            connection.disconnect();
        }
    }

    public String getNewPasswordByPhone(String username) throws IOException, AccountException {
        String cmd = String.format(GET_PWD_BY_PHONE, username);
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader reader = getResponseAsReader(connection);
            String resp = IOUtils.toString(reader);
            return parseStringResult(resp);
        }finally {
            connection.disconnect();
        }
    }
    
    public String getNewPasswordByMail(String username) throws IOException, AccountException {
        String cmd = String.format(GET_PWD_BY_MAIL, username);
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader reader = getResponseAsReader(connection);
            String resp = IOUtils.toString(reader);
            return parseStringResult(resp);
        }finally {
            connection.disconnect();
        }
    }
    
    public String login(String name,String pass,String appid) throws IOException, AccountException
    {
        String cmd = String.format(USER_LOGIN, name,pass,appid);
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader reader = getResponseAsReader(connection);
            String resp = IOUtils.toString(reader);
            return resp;
        }finally {
            connection.disconnect();
        }
    }
    
    public boolean updatePassword(String userId, String password) throws IOException, AccountException {

        JSONObject pwdField = new JSONObject();
        try{
            pwdField.put("password", password);
        }catch (JSONException e){
            throw AccountException.create(e);
        }
        String cmd = String.format(UPDATE_PASSWORD, userId, UrlEncoded.encodeString(pwdField.toString()));
        String cmdURL = mAccountServerHost + "/" + cmd;

        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader reader = getResponseAsReader(connection);
            String resp = IOUtils.toString(reader);
            return parseBooleanResult(resp);
        }finally {
            connection.disconnect();
        }
    }

    public boolean updateField(String userId, String fieldsAndValue) throws IOException, AccountException {

        String cmd = String.format(UPDATE_PASSWORD, userId, UrlEncoded.encodeString(fieldsAndValue));
        String cmdURL = mAccountServerHost + "/" + cmd;

        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{
            Reader reader = getResponseAsReader(connection);
            String resp = IOUtils.toString(reader);
            return parseBooleanResult(resp);
        }finally {
            connection.disconnect();
        }
    }
    
    public boolean parseBooleanResult(String result) throws AccountException {
        try{
            JSONObject jr = new JSONObject(result);
            return jr.getBoolean("result");
        }catch (JSONException e){
            throw toAccountException(result);
        }
    }


    public String parseStringResult(String result) throws AccountException {
        try{
            JSONObject jr = new JSONObject(result);
            return jr.getString("result");
        }catch (JSONException e){
            throw toAccountException(result);
        }
    }
    
    public AccountException toAccountException(String erroMesg){
        try{
            JSONObject error = new JSONObject(erroMesg);
            int code = error.getInt("error_code");
            String msg = error.getString("error_msg");
            return new AccountException(code, msg);
        }catch(JSONException e){
            return AccountException.create(erroMesg);
        }
    }
    
    public Reader getResponseAsReader(HttpURLConnection conn) throws IOException {
        InputStream input = conn.getInputStream();
        if("gzip".equalsIgnoreCase(conn.getContentEncoding())){
            input = new GZIPInputStream(input);
        }
        return new InputStreamReader(input);
    }

    static Map<String, List<String>> parseCircles(Reader result) throws Exception{
        Map<String, List<String>> circles = new HashMap<String, List<String>>();
        JsonReader jsonReader = new JsonReader(result);
        jsonReader.setLenient(true);
        jsonReader.beginArray();
        while(jsonReader.hasNext()){
            jsonReader.beginObject();
            String uid = null;
            List<String> circleIds = new ArrayList<String>();
            while(jsonReader.hasNext()){
                String name = jsonReader.nextName();
                if("user_id".equals(name)){
                    uid = jsonReader.nextString();
                } else if("in_circles".equals(name)){
                    jsonReader.beginArray();
                    while(jsonReader.hasNext()){
                        jsonReader.beginObject();
                        while(jsonReader.hasNext()){
                            String cName = jsonReader.nextName();
                            if("circle_id".equals(cName)){
                                circleIds.add(jsonReader.nextString());
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                    }
                    jsonReader.endArray();
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            if(uid!=null && uid.length()>0){
                circles.put(uid, circleIds);
            }
        }
        jsonReader.endArray();

        return circles;
    }


    static String toArgumentString(List<String> list){
        StringBuffer buffer = new StringBuffer();
        for(String arg : list){
            if(buffer.length()>0) buffer.append(",");
            buffer.append(arg);
        }
        return buffer.toString();
    }

    private void logD(String msg){
        if(mLogger != null){
            mLogger.log(Level.INFO, msg);
        } else {
            System.out.println(msg);
        }
    }

    private Reader copyAnddump(Reader reader) throws IOException {
        String rawData = IOUtils.toString(reader);
        logD("RAW DATA:\n" + rawData);
        return new StringReader(rawData);
    }
}
