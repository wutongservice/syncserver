/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.webagent.account;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.PostData;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.providers.ContactMapping;
import com.borqs.sync.server.common.providers.ContactsMappingDao;
import com.borqs.sync.server.common.providers.IContactsMappingDao;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.ServletImp;
import com.borqs.sync.server.webagent.base.AccountErrorCode;
import com.borqs.sync.server.webagent.contact.ContactServletImpl;
import com.borqs.sync.server.webagent.contacts.mapping.logic.ContactsMappingService;
import com.borqs.sync.server.webagent.contacts.mapping.logic.IContactsMappingService;
import com.borqs.sync.server.webagent.fastlogin.LoginImpl;
import com.borqs.sync.server.webagent.profile.ProfileSyncService;
import com.borqs.sync.server.webagent.service.ConfigService;
import com.borqs.sync.server.webagent.service.ContactService;
import com.borqs.sync.server.webagent.util.DBUtility;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import com.borqs.sync.server.webagent.util.WebLog;
import org.apache.avro.AvroRemoteException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AccountServlet extends HttpServletDelegate {

    private Logger mLogger;

    public AccountServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }

    private static final long serialVersionUID = 1L;

    // for mobile query
    public static final String REQUEST_PARMETER_GUID = "guid";
    // password for mobile register
    private static final String REQUEST_PARMETER_PASSWORD = "password";
    // displayname for mobile register
    private static final String REQUEST_PARMETER_DISPLAY_NAME = "displayname";
    
    // for change log query
    private static final String REQUEST_PARMETER_LAST_SYNC = "last_sync";
    private static final String REQUEST_PARMETER_REMOTE_URI = "remote_uri";
    private static final String REQUEST_PARMETER_MAPPING_ID = "mapping_id";
    private static final String REQUEST_PARMETER_DEVICE_ID = "device_id";
    
    //changer request parameter
    private static final String CHANGE_REQUEST_ITEM_TYPE = "item_type";
    private static final String CHANGE_REQUEST_ITEM_VALUE = "item_value";

    /**
     * query mobile by uid
     * 
     * @param qp
     * @return the mobile of uid
     * @throws AvroRemoteException
     */
    @WebMethod("account_query")
    public void queryMobile(QueryParams qp, ResponseWriter writer) throws AccountException {
        String guid = qp.checkGetString(REQUEST_PARMETER_GUID);
        mLogger.info("query guid is: " + guid);
        ServletImp.writeMobileResponse(guid, writer,mContext);
    }


    /**
     * register account by mobile
     * 
     * @param qp register params:guid(get mobile by guid),password,displayname
     * @return
     * @throws JSONException
     * @throws AvroRemoteException
     */
    @WebMethod("mobile_register")
    public void registerByMobile(QueryParams qp, ResponseWriter writer) throws AccountException {
        ServletImp.registerAccountByMobile(qp, writer,mContext);
    }

    /**
     * @param qp reset params:guid(get mobile by guid),password
     * @return
     * @throws AvroRemoteException
     */
    @WebMethod("reset_password_by_mobile")
    public void resetPasswordByMobile(QueryParams qp, ResponseWriter writer) throws AccountException {
        String guid = qp.checkGetString(REQUEST_PARMETER_GUID);
        mLogger.info("reset password,guid is: " + guid);
        String password = qp.checkGetString(REQUEST_PARMETER_PASSWORD);
        mLogger.info("reset password,password is: " + password);
        ServletImp.resetPasswordByMobile(guid, password, writer,mContext);
    }
}
