package com.borqs.sync.server.webagent.account;

import java.nio.charset.Charset;
import java.util.logging.Logger;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.PostData;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.base.AccountErrorCode;
import com.borqs.sync.server.webagent.profile.ProfileSyncService;
import com.borqs.sync.server.webagent.util.WebLog;

public class ProfileServlet extends HttpServletDelegate {
	public Logger mLogger;
	
    private static final String REQUEST_PARMETER_UID = "uid";
    private static final String REQUEST_PARMETER_TICKET = "ticket";
   
	public ProfileServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }

    
    @WebMethod("getdata")
    /**
     * get me info(profile)
     * qp params like "user=xxx&&ticket=xxxx"
     */
    public void getProfileData(QueryParams qp,ResponseWriter writer) throws AccountException{
        String userId = qp.checkGetString(REQUEST_PARMETER_UID);
        String ticket = qp.checkGetString(REQUEST_PARMETER_TICKET);
        
        ProfileSyncService.getProfileData(mContext, userId, ticket, writer);
    }
    
    @WebMethod("syncdata")
    public void syncProfileData(QueryParams qp, PostData postData, ResponseWriter writer) throws AccountException {
        String userId = qp.checkGetString(REQUEST_PARMETER_UID);
        String ticket = qp.checkGetString(REQUEST_PARMETER_TICKET);
        
        try {
            String data = postData.asString(Charset.defaultCharset());
            ProfileSyncService.syncProfileData(mContext, userId, ticket, data, writer);
        } catch (Exception e) {
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        } 
    }
}
