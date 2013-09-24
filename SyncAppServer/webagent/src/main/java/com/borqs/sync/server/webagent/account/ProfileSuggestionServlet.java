package com.borqs.sync.server.webagent.account;

import java.util.logging.Logger;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.ServletImp;
import com.borqs.sync.server.webagent.util.WebLog;

public class ProfileSuggestionServlet extends HttpServletDelegate {
	public Logger mLogger;

    //changer request parameter
    private static final String CHANGE_REQUEST_BORQS_ID = "borqsid";

	public ProfileSuggestionServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }
	

    /**
     *
     * @param qp userid:who change the contact.
     *           friendid:who is the contact been updated in userid's Contact application
     * @throws AccountException
     */
    @WebMethod("query_detail")
    public void queryChangeRequestDetail(QueryParams qp,ResponseWriter writer) throws AccountException {
        mLogger.info("profilesuggestion params is : " + qp.toString());
        String borqsId = qp.checkGetString(CHANGE_REQUEST_BORQS_ID);
        ServletImp.queryChangeRequestDetail(mContext,borqsId,writer);
    }

    /**
     * http://..../profilesuggestion/ignore_item?borqsid=10040
     * @param qp :borqsid,
     * @param writer
     * @throws AccountException
     */
    @WebMethod("ignore_item")
    public void ignoreChangeItem(QueryParams qp,ResponseWriter writer) throws AccountException {
        String borqsId = qp.checkGetString(CHANGE_REQUEST_BORQS_ID);
        ServletImp.ignoreItem(mContext,borqsId,writer);
    }
}
