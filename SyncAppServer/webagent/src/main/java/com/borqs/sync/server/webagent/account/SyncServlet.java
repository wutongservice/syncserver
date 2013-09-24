package com.borqs.sync.server.webagent.account;

import java.util.logging.Logger;

import org.apache.avro.AvroRemoteException;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.ServletImp;
import com.borqs.sync.server.webagent.util.WebLog;

public class SyncServlet extends HttpServletDelegate {
	public Logger mLogger;
	
    private static final String REQUEST_PARMETER_UID = "uid";
    private static final String REQUEST_PARMETER_DEVICE_ID = "device_id";

	public SyncServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }
	

    /**
     * query the sync version by uid and device_id
     * 
     * @param qp (uid,device_id)
     * @return
     * @throws AvroRemoteException
     * @throws JSONException
     */
    @WebMethod("needsync")
    public void checkNeedSync(QueryParams qp, ResponseWriter writer) throws AccountException {
        String uid = qp.checkGetString(REQUEST_PARMETER_UID);
        mLogger.info("querySyncVersion,the queryParams uid :" + uid);
        String deviceId = qp.checkGetString(REQUEST_PARMETER_DEVICE_ID);
        mLogger.info("querySyncVersion,the queryParams deviceId :" + deviceId);
        ServletImp.checkNeedSync(uid, deviceId, writer, mContext);
    }
}