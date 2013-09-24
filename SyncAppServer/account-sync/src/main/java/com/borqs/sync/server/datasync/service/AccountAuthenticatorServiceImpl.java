package com.borqs.sync.server.datasync.service;

import com.borqs.json.JSONObject;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.DSLog;
import org.apache.commons.io.IOUtils;
import com.borqs.sync.server.datasync.DSLog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/27/12
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccountAuthenticatorServiceImpl {
    private static final String VERIFY_TICKET = "account/who?ticket=%s&login=";

    private Context mContext;
    private String mAccountServerHost;
    private Logger mLogger;
    
    public AccountAuthenticatorServiceImpl(Context context){
        mContext = context;
        mAccountServerHost = context.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
        mLogger = DSLog.getInstnace(context).getLogger();
    }

    public void setLogger(Logger logger){
        mLogger = logger;
    }

    /**
     * return json string for login result
     * @return
     */
    public String login(String user,String password){
        String cmd = String.format(VERIFY_TICKET, password);
        String cmdURL = mAccountServerHost + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = null;
        try{
            connection = (HttpURLConnection) new URL(cmdURL).openConnection();
            String result = getResponseAsReader(connection);

            JSONObject o = new JSONObject(result);
            int uid = Integer.valueOf(o.getString("result"));
            if(uid <= 0){
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return password;
    }

    /**
     * return json string for logout result
     * @return
     */
    public String logout(String user,String password){
        return password;
    }

    private String getResponseAsReader(HttpURLConnection conn) throws IOException {
        InputStream input = conn.getInputStream();
        if("gzip".equalsIgnoreCase(conn.getContentEncoding())){
            input = new GZIPInputStream(input);
        }

        return IOUtils.toString(input);
    }


    private void logD(String msg){
        if(mLogger != null){
            mLogger.log(Level.INFO, msg);
        } else {
            System.out.println(msg);
        }
    }

}
