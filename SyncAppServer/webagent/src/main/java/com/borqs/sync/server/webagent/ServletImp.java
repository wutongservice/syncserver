/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.webagent;

import com.borqs.sync.server.common.httpservlet.PostData;
import com.borqs.sync.server.common.profilesuggestion.ProfileSuggestionGenerator;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.account.AccountRequestOperation;
import com.borqs.sync.server.webagent.account.AccountServlet;
import com.borqs.sync.server.webagent.base.AccountErrorCode;
import com.borqs.sync.server.webagent.dao.ContactDAO;
import com.borqs.sync.server.webagent.dao.IgnoreItem;
import com.borqs.sync.server.webagent.service.AccountRemoteService;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import com.borqs.sync.server.webagent.util.SMSRequest;
import com.borqs.sync.server.webagent.util.TextUtil;
import com.borqs.sync.server.webagent.util.WebLog;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.net.URLDecoder;

public class ServletImp {

    private static final String FILE_ACCOUNT_INFO = "accountinfo.properties";

    private static final String MOBILE_RESPONSE_KEY = "mobile";
    private static final String NEED_SYNC_RESPONSE_KEY = "need_sync";
    private static final String CHANGE_REQUEST_DATA_KEY = "changerequest_data";

    public static void bindMobileWithGUid(Context context, String mobile, String message) throws AccountException {
        WebLog.getLogger(context).info("mobile:" + mobile + " message:" + message);
        SMSRequest smsRequest = SMSRequest.from(message,context);
        WebLog.getLogger(context).info("parsed smsRequest:" + smsRequest.toString());
        // if the type is NUMBERLOOKUP,bind the mobile with global guid
        WebLog.getLogger(context).info("is number bind?" + smsRequest.getType().is(SMSRequest.RequestType.NumberBind));

        if (smsRequest.getType().is(SMSRequest.RequestType.NumberBind)) {
            String guid = smsRequest.castToNumberBindRequest().getGuid();
            AccountRequestOperation accountRequestOperation = new AccountRequestOperation(context,
                    FILE_ACCOUNT_INFO);
            // save the uid and mobile info ,url from SMS gateway
            WebLog.getLogger(context).info("mobile:" + mobile + ",guid:" + guid);
            if (!TextUtil.isEmpty(mobile) && !TextUtil.isEmpty(guid)) {
                String readedMobile = accountRequestOperation.readMobileByUid(guid);
                WebLog.getLogger(context).info("has mobile " + readedMobile + ":" + !TextUtil.isEmpty(readedMobile));
                if (TextUtil.isEmpty(readedMobile)) {
                    accountRequestOperation.writeMobile(guid, mobile);
                }
            }
        }
    }

    private static String queryMobile(String guid, Context context) {
        try {
            if (!TextUtil.isEmpty(guid)) {
                AccountRequestOperation fileOperation = new AccountRequestOperation(context,
                        FILE_ACCOUNT_INFO);
                return fileOperation.readMobileByUid(guid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeMobileResponse(String guid, ResponseWriter writer, Context context) throws AccountException {
        try {
            String mobile = queryMobile(guid, context);
            if (!TextUtil.isEmpty(mobile)) {
                ResponseWriterUtil.writeStringJson(MOBILE_RESPONSE_KEY, mobile, writer);
                return ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new AccountException(AccountErrorCode.NOT_FOUND_ERROR, "Not Found");
    }

    public static void registerAccountByMobile(QueryParams qp, ResponseWriter writer,
            Context context) throws AccountException {
        String guid = qp.getString(AccountServlet.REQUEST_PARMETER_GUID, null);
        WebLog.getLogger(context).info("register account ,the guid is :" + guid);
        String mobile = queryMobile(guid, context);
        WebLog.getLogger(context).info("register account ,the queried mobile is :" + mobile);
        qp.put("mobile", mobile);
        AccountRemoteService as = AccountRemoteService.getInstance(context);
        as.registerAccountByMobile(qp, writer);
    }

    public static void resetPasswordByMobile(String guid, String password, ResponseWriter writer,
            Context context) throws AccountException {
        String mobile = queryMobile(guid, context);
        WebLog.getLogger(context).info("query mobile by guid :" + guid + ",mobile:" + mobile);
        if (TextUtil.isEmpty(mobile)) {
            throw new AccountException(AccountErrorCode.NO_USER_EXIST_ERROR,
                    "Please make sure that the user is registered!");
        }
        AccountRemoteService as = AccountRemoteService.getInstance(context);
        as.resetPasswordByMobile(mobile, password, writer);
    }

    public static void checkNeedSync(String uid, String deviceId, ResponseWriter writer,
            Context context) {
        ContactDAO contactSyncDao = new ContactDAO(context);
        long syncVersion = contactSyncDao.getSyncVersion(uid, deviceId);
        long syncSourceVersion = contactSyncDao.getSyncSourceVersion(uid);
        boolean needSync = (syncSourceVersion != syncVersion);
        try {
            ResponseWriterUtil.writeStringJson(NEED_SYNC_RESPONSE_KEY,
                    String.valueOf(needSync), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * response the change request detail
     * @param context
     * @param borqsId  who want to get the change request
     * @param writer
     */
    public static void queryChangeRequestDetail(Context context,String borqsId,ResponseWriter writer) throws AccountException {
        ProfileSuggestionGenerator generator = new ProfileSuggestionGenerator(context);
        generator.setLogger(WebLog.getLogger(context));
        String changeRequestData = generator.generateChangeRequest(borqsId);
        try {
            ResponseWriterUtil.writeStringJson(CHANGE_REQUEST_DATA_KEY,changeRequestData == null?"":changeRequestData,writer);
        } catch (IOException e) {
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        }
    }

    public static void ignoreItem(Context context, String borqsId, ResponseWriter writer) throws AccountException {
        ContactDAO dao = new ContactDAO(context);
        try {
            boolean ignoreResult  = dao.ignoreItem(borqsId);
            ResponseWriterUtil.writeStringJson("result", String.valueOf(ignoreResult), writer);
        } catch (IOException e) {
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        }
    }

    /**
     * query the configuration bytes by file name
     * @param context
     * @param filename
     * @param writer
     * @throws AccountException
     */
    public static void queryConfigurationFile(Context context, String filename,ResponseWriter writer) throws AccountException {
        InputStream is = context.getConfig().getStaticConfigFile(filename);
        if(is == null){
            throw new AccountException(AccountErrorCode.DATA_ERROR,
                    new Exception("read configuration file error,the InputStream is null"));
        }
        try {
            byte[] configuration = new byte[is.available()];
            is.read(configuration);
            BufferedOutputStream bos = new BufferedOutputStream(writer.asStream());
            bos.write(configuration);
            bos.flush();
            bos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
