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

@Deprecated
 //temp,should remove the class after the client upgrade and do use configfile rest interface
public class ConfigfileServlet extends HttpServletDelegate {
	public Logger mLogger;

    //for configuration file query
    private static final String REQUEST_PARMETER_FILENAME = "filename";

	public ConfigfileServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }


    /**
     * http://..../configfile/query?filename=borqs_plus.xml
     * @throws AccountException
     */
    @Deprecated
    @WebMethod("query")
    public void queryConfigurationFile(QueryParams qp,ResponseWriter writer) throws AccountException {
        String fileName = qp.checkGetString(REQUEST_PARMETER_FILENAME);
        ServletImp.queryConfigurationFile(mContext,fileName,writer);
    }
}
