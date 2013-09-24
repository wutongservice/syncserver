/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.service;

import com.borqs.sync.server.common.runtime.Context;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Date: 5/14/12
 * Time: 3:25 PM
 * Borqs project
 */
public class ContactDataMiningAdapter {
    //temporarily use hard code
    private final static String SERVER_HOST = "http://api.borqs.com/dm";
    private final static String COUNT_NAME_BY_EMAIL = "contacts/namecount/byemail/%s.json";
    private final static String COUNT_NAME_BY_PHONE = "contacts/namecount/bymobile/%s.json";

    private Logger mLogger;

    public ContactDataMiningAdapter(Context context){

    }

    public String lookupRealNameByEmail(String email) {
        String cmd = String.format(COUNT_NAME_BY_EMAIL, email);
        String cmdURL = SERVER_HOST + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(cmdURL).openConnection();

            Reader reader = getResponseAsReader(connection);
            return IOUtils.toString(reader);
        }catch (IOException e) {
                e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return null;
    }

    public String lookupRealNameByMobile(String mobile) {
        String cmd = String.format(COUNT_NAME_BY_PHONE, mobile);
        String cmdURL = SERVER_HOST + "/" + cmd;

        logD("Http request: " + cmdURL);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(cmdURL).openConnection();

            Reader reader = getResponseAsReader(connection);
            return IOUtils.toString(reader);
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return null;
    }

    private void logD(String msg){
        if(mLogger != null){
            mLogger.log(Level.INFO, msg);
        } else {
            System.out.println(msg);
        }
    }

    private Reader getResponseAsReader(HttpURLConnection conn) throws IOException {
        InputStream input = conn.getInputStream();
        if("gzip".equalsIgnoreCase(conn.getContentEncoding())){
            input = new GZIPInputStream(input);
        }
        return new InputStreamReader(input);
    }
}
