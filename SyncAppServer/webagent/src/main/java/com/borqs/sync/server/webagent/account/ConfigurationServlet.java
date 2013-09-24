package com.borqs.sync.server.webagent.account;

import java.io.IOException;
import java.util.logging.Logger;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.ServletImp;
import com.borqs.sync.server.webagent.base.AccountErrorCode;
import com.borqs.sync.server.webagent.service.ConfigService;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import com.borqs.sync.server.webagent.util.WebLog;

public class ConfigurationServlet extends HttpServletDelegate {
	public Logger mLogger;


    //Config query command's parameters
    private static final String CONFIG_QUERY_COMMAND = "key";
    private static final String CONFIG_QUERY_RESULT = "result";

    //for static config file name
    private static final String REQUEST_PARMETER_FILENAME = "filename";
    
	public ConfigurationServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }

    /**
     * To query some dynamic configuration from server by client
     *  http://.../config/query?key=<>
      * @param qp
     * @param writer
     * @throws AccountException
     */
    @WebMethod("query")
    public void queryConfig(QueryParams qp, ResponseWriter writer) throws AccountException {
        String key = qp.checkGetString(CONFIG_QUERY_COMMAND);
        try {
            String result = new ConfigService().getConfig(mContext, key);
            ResponseWriterUtil.writeStringJson(CONFIG_QUERY_RESULT, result, writer);
        } catch (IOException e) {
            throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
        }
    }

    /**
     * http://..../query_static?filename=borqs_plus.xml
     * @throws AccountException
     */
    @WebMethod("query_static")
    public void queryConfigurationFile(QueryParams qp,ResponseWriter writer) throws AccountException {
        String fileName = qp.checkGetString(REQUEST_PARMETER_FILENAME);
        ServletImp.queryConfigurationFile(mContext, fileName, writer);
    }


}
