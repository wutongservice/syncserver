package com.borqs.sync.server.webagent.account;

import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.apache.avro.AvroRemoteException;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.PostData;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.ServletImp;
import com.borqs.sync.server.webagent.fastlogin.LoginImpl;
import com.borqs.sync.server.webagent.util.WebLog;

public class AccountLoginServlet extends HttpServletDelegate {
	private Logger mLogger;
	
    private static final String REQUEST_PARMETER_DATA = "data";
    
    private static final String REQUEST_PARMETER_MOBILE = "mobile";
    private static final String REQUEST_PARMETER_MESSAGE = "message";
    private static final String REQUEST_PARMETER_TICKET = "ticket";

    public AccountLoginServlet(Context context) {
		super(context);
		mLogger = WebLog.getLogger(context);
	}

	@WebMethod("verifybysim")
    public void verifyBySim(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("verifybysim params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.verifyBySim(mContext, jsonstr,writer);
    }

    @WebMethod("verifynosim")
    public void verifyNoSim(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("verifynosim params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.verifyNoSim(mContext, jsonstr,writer);
    }

    @WebMethod("getguidbysim")
    public void getGUIDBySim(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("getguidbysim params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.getGUIDBySim(mContext, jsonstr,writer);
    }

    @WebMethod("getguidbycode")
    public void getGUIDByVerifyCode(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("getguidbycode params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.getGUIDByVerifyCode(mContext, jsonstr,writer);
    }

    @WebMethod("fastlogin")
    public void fastLogin(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("accountRequest params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.fastLogin(mContext, jsonstr,writer);
    }

    @WebMethod("normallogin")
    public void normalLogin(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("accountRequest params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.normalLogin(mContext, jsonstr,writer);
    }

    @WebMethod("changepassword")
    public void changePassword(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("accountRequest params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.changePassword(mContext, jsonstr,writer);
    }

    @WebMethod("modifyfield")
    public void modifyField(QueryParams qp, ResponseWriter writer) throws AccountException {
        mLogger.info("accountRequest params is : " + qp.toString());

        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.modifyField(mContext, jsonstr,writer);
    }
    
    @WebMethod("getnewpwd")
    public void getNewPassword(QueryParams qp, ResponseWriter writer) throws AccountException {
        String jsonstr = qp.getString(REQUEST_PARMETER_DATA, null);
        LoginImpl.getNewPassword(mContext, jsonstr, writer);
    }
    
    @WebMethod("changephoto")
    public void changePhoto(QueryParams qp, PostData postData, ResponseWriter writer) throws AccountException {
        try{
            String ticket = qp.getString(REQUEST_PARMETER_TICKET, null);
            String data = postData.asString(Charset.defaultCharset());
            LoginImpl.changePhoto(mContext, ticket, data, writer);
        } catch (Exception exp){
            throw AccountException.create(exp.getCause());
        }
    }

    /**
     * insert the uid and mobile map into accountinfo.properties
     * http://apitest.borqs.com/sync/webagent/account_request?mobile=<Mobile_Number>&message=<message_encoded>&from=SMS
     * @param qp
     * @throws AvroRemoteException
     */
    @WebMethod("account_request")
    public void accountRequest(QueryParams qp) throws AccountException {
        System.out.println("accountRequest params is : " + qp.toString());
        mLogger.info("accountRequest params is : " + qp.toString());
        String mobile = qp.getString(REQUEST_PARMETER_MOBILE, null);
        String message = qp.getString(REQUEST_PARMETER_MESSAGE, null);
        ServletImp.bindMobileWithGUid(mContext,mobile, message);
    }
}
