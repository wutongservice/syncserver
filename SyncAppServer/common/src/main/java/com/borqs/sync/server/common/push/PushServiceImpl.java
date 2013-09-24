package com.borqs.sync.server.common.push;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.LogHelper;
import com.borqs.sync.server.common.util.Utility;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Logger;

public class PushServiceImpl implements IPushService {

    private static final String APP_ID = "appid";
    private static final String ADDRESS = "address";

    private String mAppId;
    private String mAddress;
    private Logger mLogger;

    public PushServiceImpl(Context context){
        Properties pushProperties = context.getConfig().getPushSettings();
        mAppId = pushProperties.getProperty(APP_ID);
        mAddress = pushProperties.getProperty(ADDRESS);
    }

    @Override
    public void setLogger(Logger logger){
        mLogger = logger;
    }

	/* (non-Javadoc)
	 * @see com.borqs.information.push.IPushService#push(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean push(String from, String to, String data) {
		HttpURLConnection httpConn = null;
		InputStreamReader input = null;
		OutputStream output = null;

		try {
			URL url = new URL(mAddress);
			LogHelper.logD(mLogger,url.toString());

			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //append the http request parameter
			StringBuffer reqBuf = new StringBuffer();
            appendParamerter(reqBuf,"from_jid=",from);
            appendParamerter(reqBuf,"jid=",to);
            appendParamerter(reqBuf,"app_id=",mAppId);
            appendParamerter(reqBuf,"data=",data);

			String req = reqBuf.toString();
            LogHelper.logD(mLogger,req);
//			httpConn.setRequestProperty( "Content-Length",String.valueOf(req.getBytes().length));
			output = httpConn.getOutputStream();
			output.write(req.getBytes());

			// read result
			input = new InputStreamReader(httpConn.getInputStream(), "utf-8");
			BufferedReader in = new BufferedReader(input);
			String inputLine;
			StringBuffer sb = new StringBuffer();
			while (null != (inputLine = in.readLine())) {
				sb.append(inputLine);
			}
            LogHelper.logD(mLogger,"to " + to + " result: " +sb.toString());

            return "OK".equals(sb.toString().trim());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
				try {
                    if(null!=output) {
                        output.close();
                    }
                    if(null!=input) {
                       input.close();
                    }
                    if(null!=httpConn) {
                        httpConn.disconnect();
                    }
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return false;
	}

    private void appendParamerter(StringBuffer req,String key,String value) throws UnsupportedEncodingException {
        if(!Utility.isEmpty(value)){
            if(req.length() > 0){
              req.append("&");
            }
            req.append(key).append(URLEncoder.encode(value,"UTF-8"));
        }
    }
}
