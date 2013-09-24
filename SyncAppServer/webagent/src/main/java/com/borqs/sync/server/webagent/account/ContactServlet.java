package com.borqs.sync.server.webagent.account;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.PostData;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.base.AccountErrorCode;
import com.borqs.sync.server.webagent.contact.ContactServletImpl;
import com.borqs.sync.server.webagent.service.ContactService;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import com.borqs.sync.server.webagent.util.WebLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Logger;

public class ContactServlet extends HttpServletDelegate {
	public Logger mLogger;

    //Contact servlet
    private static final String CONTACT_REAL_NAME_USER_IDS = "userids";

    //contact server info servelet
    private static final String CONTACT_QUERY_SOURCE_ID_USER_ID = "userid";
    private static final String CONTACT_QUERY_SOURCE_ID_DEVICE = "device";

    private static final String REQUEST_PARMETER_UID = "uid";

	public ContactServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }

    /**
     * Query the real name for the given borqsIds
     * http://.../contact/real_name?uids="1001,1002"
     */
    @WebMethod("real_name")
    public void queryRealName(QueryParams qp, ResponseWriter writer) throws AccountException {
        String uids = qp.checkGetString(CONTACT_REAL_NAME_USER_IDS);
        try {
            String[] idList = uids.split(",");
            Map<String,String> result = new ContactService(getContext()).queryRealName(idList);
            ResponseWriterUtil.writeMapJson(result, writer);
        } catch (IOException e) {
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        }
    }

    @WebMethod("query_sourceid")
    public void queryGuidByLuid(QueryParams qp,PostData postData,ResponseWriter writer) throws AccountException {
        String userid = qp.checkGetString(CONTACT_QUERY_SOURCE_ID_USER_ID);
        String device = qp.checkGetString(CONTACT_QUERY_SOURCE_ID_DEVICE);
        try {
            String luids = postData.asString(Charset.defaultCharset());
            ContactServletImpl.queryGuidByLuid(mContext, userid, device, luids, writer);

        } catch (IOException e) {
            e.printStackTrace();
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        }


    }

    /**
     * http://..../contact/query_unconnected?uid=10040
     * @throws AccountException
     */
    @WebMethod("query_unconnected")
    public void queryUnconnectedBuddy(QueryParams qp,ResponseWriter writer) throws AccountException {
        String uid = qp.checkGetString(REQUEST_PARMETER_UID);
        ContactService service = new ContactService(mContext);
        service.fetchUnconnectedContacts(uid, writer);
    }

    /**
     * http://..../contact/count?userid=10040
     * @throws AccountException
     */
    @WebMethod("count")
    public void queryCount(QueryParams qp,ResponseWriter writer) throws AccountException {
        String uid = qp.checkGetString(CONTACT_QUERY_SOURCE_ID_USER_ID);
        ContactService service = new ContactService(mContext);
        long count = service.queryCount(uid);
        try {
            ResponseWriterUtil.writeStringJson("result", String.valueOf(count), writer);
        } catch (IOException e) {
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        }
    }
}
