/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.datamining;

import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.LogHelper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private static final String HOST_GET_INTEGRATION_PROFILE = "/dm/contacts/integrator/byborqsid/%s";

    private static final int HTTP_CONNECTION_TIME_OUT = 60*1000;
    private static final int HTTP_READ_TIME_OUT = 60*1000;

    private Logger mLogger;
    private Context mContext;

    public ContactDataMiningAdapter(Context context){
        mContext = context;
    }

    public void setLogger(Logger logger){
        mLogger = logger;
    }

    /**
     * @param borqsId
     * @return null if can not get the profile by borqsid
     */
    public IntegrationProfileOperation getIntegrationProfile(String borqsId){
        //TODO the host
        String host = mContext.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
        String cmdURL = String.format("http://api.borqs.com" + HOST_GET_INTEGRATION_PROFILE, borqsId);
        LogHelper.logD(mLogger,"Http request: " + cmdURL);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(cmdURL).openConnection();
            connection.setConnectTimeout(HTTP_CONNECTION_TIME_OUT);
            connection.setReadTimeout(HTTP_READ_TIME_OUT);
            Reader reader = getResponseAsReader(connection);

            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(true);
            return parseJsonReader(jsonReader);
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }

    /**
     * public for test
     * @param reader
     * @return
     * @throws IOException
     */
    public IntegrationProfileOperation parseJsonReader(JsonReader reader) throws IOException {
        IntegrationProfileOperation integrationProfile = new IntegrationProfileOperation();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (IntegrationProfileOperation.INTEGRATION_PROFILE_ITEM_NAMES.equals(name)) {
                integrationProfile.parseName(reader);
            } else if (IntegrationProfileOperation.INTEGRATION_PROFILE_ITEM_PHONES.equals(name)) {
                integrationProfile.parsePhones(reader);
            } else if (IntegrationProfileOperation.INTEGRATION_PROFILE_ITEM_IMS.equals(name)) {
                integrationProfile.parseIMs(reader);
            } else if (IntegrationProfileOperation.INTEGRATION_PROFILE_ITEM_MAILS.equals(name)) {
                integrationProfile.parseEmails(reader);
            } else if (IntegrationProfileOperation.INTEGRATION_PROFILE_ITEM_WEBS.equals(name)) {
                integrationProfile.parseWebs(reader);
            } else if (IntegrationProfileOperation.INTEGRATION_PROFILE_ITEM_ADDRESSES.equals(name)) {
                integrationProfile.parseAddressField(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return integrationProfile;
    }

    public String lookupRealNameByEmail(String email) {
        String cmd = String.format(COUNT_NAME_BY_EMAIL, email);
        String cmdURL = SERVER_HOST + "/" + cmd;

        LogHelper.logD(mLogger,"Http request: " + cmdURL);
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

        LogHelper.logD(mLogger,"Http request: " + cmdURL);
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

    private Reader getResponseAsReader(HttpURLConnection conn) throws IOException {
        InputStream input = conn.getInputStream();
        if("gzip".equalsIgnoreCase(conn.getContentEncoding())){
            input = new GZIPInputStream(input);
        }
        return new InputStreamReader(input);
    }
}
