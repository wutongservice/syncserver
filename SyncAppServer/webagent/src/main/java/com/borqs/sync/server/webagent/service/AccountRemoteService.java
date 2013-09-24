/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.webagent.service;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.base.AccountErrorCode;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import com.borqs.sync.server.webagent.util.TextUtil;
import com.borqs.sync.server.webagent.util.WebLog;

import java.io.IOException;
import java.util.logging.Logger;

public class AccountRemoteService {
    private static Logger LOG;
    private static AccountRemoteService mInstance;
    private AccountManager mAccountManager;
    
    public static synchronized AccountRemoteService getInstance(Context context) {
        LOG = WebLog.getLogger(context);
        if (mInstance == null) {
            mInstance = new AccountRemoteService(context);
        }
        return mInstance;
    }

    private AccountRemoteService(Context context) {
        mAccountManager = new AccountManager(context);
    }

    // TODO
    public boolean verifyTicket(String ticket) {
        return true;
    }

    public void registerAccountByMobile(QueryParams qp,ResponseWriter writer) throws
            AccountException {
        String email = qp.getString("email", "");
        String mobile = qp.getString("mobile", "");
        LOG.info("login mobile is " + mobile);
        if (TextUtil.isEmpty(email) && TextUtil.isEmpty(mobile))
            throw new AccountException(AccountErrorCode.PARAM_ERROR,
                    "Must have parameter 'email' or 'mobile'");

        String pwd = qp.checkGetString("password");
        String displayName = qp.checkGetString("displayname");
        String gender = qp.getString("gender", "m");
        String imei = qp.getString("imei", "");
        String imsi = qp.getString("imsi", "");
        String ua = qp.getString("User-Agent", "");
        String loc = qp.getString("location", "");
        try {
            String userId = mAccountManager.createAccount(email, mobile, pwd, displayName, gender, imei,
                    imsi, ua, loc);
            LOG.info("create account successfully,the userid is :" + userId);

            if (Long.parseLong(userId) > 0) {
                // success
                ResponseWriterUtil.writeStringJson("result", "true", writer);
            } else {
                ResponseWriterUtil.writeStringJson("result", "false", writer);
            }
        }catch (IOException error) {
            LOG.info("=========IOException" );
            throw AccountException.create(error);
        }
    }

    public void resetPasswordByMobile(String mobile, String password, ResponseWriter writer) throws AccountException {
        try {
            String userId = mAccountManager.findUserIdByUserName(mobile);
            boolean result = mAccountManager.updatePassword(userId, password);
            ResponseWriterUtil.writeStringJson("result", String.valueOf(result), writer);
        } catch (IOException error) {
            LOG.info("=========Throwable");
            throw AccountException.create(error);
        }
    }
    
}
