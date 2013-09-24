package com.borqs.sync.server.webagent.profile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.io.IOUtils;
import org.mortbay.util.UrlEncoded;

import com.borqs.sync.server.common.account.adapters.BPCAccountHttpAdapter;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.Photo;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.fastlogin.MD5;
import com.borqs.sync.server.webagent.util.TextUtil;
import com.borqs.sync.server.webagent.util.WebLog;


public class ProfileHttpAdapter extends BPCAccountHttpAdapter{
    private static final String PROFILE_QUERY = "v2/internal/getUsers?userIds=%s"
                                               +"&viewerId=%s&ticket=%s&cols=#full"
                                               +"&sign_method=md5&sign=%s&appid=%s";
    private static final String PROFILE_UPDATE = "v2/internal/updateAccount?user=%s&ticket=%s"
                                                +"&sign_method=md5&sign=%s&appid=%s";
    private static final String PROFILE_UPDATE_PHOTO = "account/upload_profile_image";
    
    // sync appid
    private static final String SYNC_APP_ID = "10";
    //sync appSecret
    private static final String SYNC_APP_SECRET = "appSecret10";
    
    private Context mContext;
    private String mServerHost;
    
    public ProfileHttpAdapter(Context ctx){
        super(null);
        mContext = ctx;
        mServerHost = mContext.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
    }
    
    /**
     * 
     * @param userId
     * @param ticket
     * @return JContactString
     * @throws Exception
     */
    public String getProfileOfUser(String userId, String ticket) throws Exception {
        String sign = md5Sign(SYNC_APP_SECRET, Arrays.asList("users", "columns"));        
        String cmd = String.format(PROFILE_QUERY, userId, userId, ticket, sign, SYNC_APP_ID);
        String cmdURL = mServerHost + "/" + cmd;        
        
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();        
        connection.setConnectTimeout(HTTP_CONNECTION_TIME_OUT);
        connection.setReadTimeout(HTTP_READ_TIME_OUT);
        //logD("get profile url:" + cmdURL);
        String result = null;
        try{
            Reader reader = getResponseAsReader(connection);
            String response = IOUtils.toString(reader);
            //logD("get profile resposne:" + resp.substring(0, 10));
            result = parseGetProfileResult(response);
            result = convertToJContactString(result);
        }finally {
            connection.disconnect();
        }
        
        return result;
    }    
    
    private String convertToJContactString(String data) throws Exception{
        JSONObject jOrgData =  new JSONObject(data);
        Contact profile = JProfileConverter.toProfileStruct(jOrgData);
        
        // photo data
        //TODO: a tricky, see JProfileConvert.toProfileStruct
        String photoUrl = profile.getManager();
        profile.setManager(null);
        if (!TextUtil.isEmpty(photoUrl)){                
            byte[] photoData = getProfilePhoto(photoUrl);
            if (photoData != null){
                profile.setPhotoType(Photo.PHOTO_IMAGE);
                profile.setPhoto(new Photo(0, "JPEG", photoData, null));
            }
        }
        
        String result = JProfileConverter.toContactJson(profile);
        //logD("get profile result" + result);
        // must include change time
        JSONObject jData = new JSONObject(result);
        jData.put("modify_time", jOrgData.opt("status_updated_time"));
        result = jData.toString();
        return result;
    }
    
    public boolean updateProfileData(String userId, String ticket, String data) throws Exception {
        Contact profile = JProfileConverter.toContactProfile(data);        
        boolean result = updateProfileInfo(userId, ticket, profile);
        logD("updateProfileInfo:" + result);
        if (result) {
            Photo photo = profile.getPhoto();
            if (photo != null){
                byte[] imageData = photo.getImage();
                if (imageData != null){
                    result = updateProfilePhoto(ticket, imageData);
                }
            }
        }
        return result;
    }    
    
    private byte[] getProfilePhoto(String photoUrl) throws Exception{
        //photoUrl = photoUrl.replace("apitest.borqs.com", "192.168.5.22");
        logD("getProfilePhoto:" + photoUrl);
        HttpURLConnection connection = (HttpURLConnection) new URL(photoUrl).openConnection();        
        connection.setConnectTimeout(HTTP_CONNECTION_TIME_OUT);
        connection.setReadTimeout(HTTP_READ_TIME_OUT);
        
        byte[] data = null;
        try{     
            InputStream input = connection.getInputStream();
            int totalLen = 300*1024;
            ByteBuffer buffer = ByteBuffer.allocate(totalLen+10);
            int readLen = 1024*5;
            
            data = new byte[readLen+10];
            int read = 0;
            
            while (read != -1){
                read = input.read(data, 0, readLen);
                if (read > 0){
                    if ((buffer.position() + read) > totalLen){
                        break;
                    }
                    buffer.put(data, 0, read);
                }                
            }
            data = buffer.array();
            logD("getProfilePhoto read data:" + buffer.position());
        } finally {
            connection.disconnect();
        }
        
        return data;
    }
    
    private boolean updateProfileInfo(String userId, String ticket, Contact profile) throws Exception {
        String sign = md5Sign(SYNC_APP_SECRET, Arrays.asList("users", "columns"));        
        String cmd = String.format(PROFILE_UPDATE, userId, ticket, sign, SYNC_APP_ID);
        String cmdURL = mServerHost + "/" + cmd + getUpdateParams(profile);        
        
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();        
        connection.setConnectTimeout(HTTP_CONNECTION_TIME_OUT);
        connection.setReadTimeout(HTTP_READ_TIME_OUT);
        
        //logD("sync profile url:" + cmdURL);
        boolean result = false;
        try{            
            Reader reader = getResponseAsReader(connection);
            String response = IOUtils.toString(reader);
            logD("update profile resposne:" + response);
            result = parseBooleanResult(response);
        } finally {
            connection.disconnect();
        }
        
        return result;
    }    
    
    public boolean updateProfilePhoto(String ticket, byte[] data) throws Exception {
        boolean result = false;
        String cmdURL = mServerHost + "/" + PROFILE_UPDATE_PHOTO;    
        HttpURLConnection connection = (HttpURLConnection) new URL(cmdURL).openConnection();
        try{                    
            connection.setConnectTimeout(HTTP_CONNECTION_TIME_OUT);
            connection.setReadTimeout(HTTP_READ_TIME_OUT);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            
            String boundary = "----SYNCPROFILE-upload" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Transfer-Encoding", "chunked");
            connection.setDoOutput(true);
            
            boundary = "--" + boundary;
            OutputStream os = connection.getOutputStream();
            DataOutputStream out = new DataOutputStream(os);
            
            //parameters
            String fileName = "profile_image";
            String sign = md5Sign(SYNC_APP_SECRET, fileName);
            writeParams(out, boundary, ticket, sign);
            
            writeFile(out, boundary, fileName, data);
            
            os.flush();
            os.close();
            
            Reader reader = getResponseAsReader(connection);
            String response = IOUtils.toString(reader);
            logD("get profile resposne:" + response);
            result = parseBooleanResult(response);            
        } finally{
            connection.disconnect();
        }
        return result;
    }
    
    private String parseGetProfileResult(String result)throws AccountException {
        try{
            JSONArray jr = new JSONArray(result);
            JSONObject json = jr.getJSONObject(0);
            return json.toString();
        }catch (JSONException e){
            logD("get profile account exception:" + result);
            throw toAccountException(result);
        }
    }
        
    private String getUpdateParams(Contact profile) throws JSONException, UnsupportedEncodingException{
        Map<String,String> params = null;
        if (profile != null){
            params = JProfileConverter.toParamMap(profile);
        }
        StringBuilder builder = new StringBuilder();
        if (params != null){
            Set<String> keys = params.keySet();
            Iterator<String> it = keys.iterator();
            while(it.hasNext()){
                String key = it.next();
                builder.append("&" 
                              + URLEncoder.encode(key, "UTF-8")
                              + "=" 
                              + URLEncoder.encode(params.get(key), "UTF-8"));
            }
        }
        return builder.toString();
    }
    
    private String md5Sign(String appSecret, Collection<String> paramNames) {
        TreeSet<String> set = new TreeSet<String>(paramNames);
        String sign = appSecret + treeSetToString(set) + appSecret;
        return MD5.md5Base64(sign.getBytes());
    }   
    
    private String md5Sign(String appSecret, String param) {
        String str = appSecret + param + appSecret;
        str = MD5.md5Base64(str.getBytes()).replace("\n", "");
        return str;
    }   
    
    private void writeFile(DataOutputStream out, String boundary, String fName, byte[] data) throws IOException {
        out.writeBytes(boundary + "\r\n");
        out.writeBytes("Content-Disposition: form-data; name=\""+fName+ "\"; filename=\"screenshot0.png\"\r\n");
        out.writeBytes("Content-Type: " + getImageType(data) + "\r\n\r\n");
        
        out.write(data,0,data.length);
        out.writeBytes("\r\n" + boundary + "--\r\n\r\n");
    }
    
    private void writeParams(DataOutputStream out, String boundary, String ticket,
                   String sign) throws IOException {
        out.writeBytes(boundary + "\r\n");
        writeParam(out, "ticket", ticket);
        
        out.writeBytes(boundary + "\r\n");
        writeParam(out, "sign_method", "md5");
        
        out.writeBytes(boundary + "\r\n");
        writeParam(out, "sign", sign); 
        
        out.writeBytes(boundary + "\r\n");
        writeParam(out, "appid", "10");
    }
    
    private void writeParam(DataOutputStream out, String key, String value) throws IOException {
         out.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
         out.writeBytes("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
         out.write(value.getBytes("UTF-8"));
         out.writeBytes("\r\n");
    } 
    
    private String getImageType(byte[] image) {

        String type = "image/jpg";
        MemoryCacheImageInputStream mcis = null;

        mcis = new MemoryCacheImageInputStream(new ByteArrayInputStream(image));

        Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);

        while (itr.hasNext()) {
            ImageReader reader = (ImageReader)  itr.next();
            String imageReaderName = reader.getClass().getSimpleName();
            if ("GIFImageReader".equals(imageReaderName)) {
                type = "image/gif";
            } else if ("JPEGImageReader".equals(imageReaderName)) {
                type = "image/jpg";
            } else if ("PNGImageReader".equals(imageReaderName)) {
                type = "image/png";
            } else if ("BMPImageReader".equals(imageReaderName)) {
                type = "image/bmp";
            }
        }

        return type;
    }
    
    private String treeSetToString(TreeSet<String> set) {
        Iterator<String> it = set.iterator();
        String str = "";
        while (it.hasNext()) {
            str += it.next();
        }
        return str;
    }
    
    private void logD(String msg){
        WebLog.getLogger(mContext).info(msg);
    }
}
