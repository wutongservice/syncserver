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

public class ContactsMappingServlet extends HttpServletDelegate {

    private Logger mLogger;

    public ContactsMappingServlet(Context context) {
        super(context);
        mLogger = WebLog.getLogger(context);
    }

    /**
     * get Borqs IDs by owner ID
     * @param qp
     * @param writer
     * @throws AccountException
     */
    @WebMethod("borqsids")
    public void queryContactBorqsIDMapping(QueryParams qp,ResponseWriter writer) throws AccountException{
    	String ownerid = qp.checkGetString("oid");
		String formated = qp.getString("formated",null);
		String cols = qp.getString("cols",null);
		
		if(null == ownerid) {
			WebLog.getLogger(mContext).info("lose 'oid' parameter!");
			try {
				ResponseWriterUtil.writeStringJson("result", "{\"success\":\"false\", \"message\":\"lose oid parameter\"}", writer);
			} catch (IOException e) {
				WebLog.getLogger(mContext).info(e.getMessage());
				e.printStackTrace();
				throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
			}
			return;
		}
		
		// fetch mappings by owner
		Connection conn = mContext.getSqlConnection();
		
		IContactsMappingService service = null;
		List<ContactMapping> mappings = null;
		try {
			IContactsMappingDao dao = new ContactsMappingDao(conn);
			service = new ContactsMappingService(mContext, dao);
			mappings = service.fetchMappingByOID(ownerid);
			
			// convert to JSON
			String content = service.convertToJson(mappings, cols);
			if("false".equals(formated)) {
				CharSequence newline = System.getProperty("line.separator");
				content = content.replace(" ", "").replace(newline, "");
			}
			
			ResponseWriterUtil.writeObjectJson(content,writer);
		} catch (Exception e) {
			e.printStackTrace();
			WebLog.getLogger(mContext).info(e.getMessage());
			throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
		} finally {
			DBUtility.close(conn, null, null);
		}
    }
    
    @WebMethod("contactids")
    public void queryContactIDMapping(QueryParams qp,ResponseWriter writer) throws AccountException{
    	String borqsId = qp.checkGetString("bid");
		String formated = qp.getString("formated",null);
		String cols = qp.getString("cols",null);
		
		if(null == borqsId) {
			WebLog.getLogger(mContext).info("lose 'bid' parameter!");
			try {
				ResponseWriterUtil.writeStringJson("result", "{\"success\":\"false\", \"message\":\"lose oid parameter\"}", writer);
			} catch (IOException e) {
				WebLog.getLogger(mContext).info(e.getMessage());
				e.printStackTrace();
				throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
			}
			return;
		}
		
		// fetch mappings by owner
		Connection conn = mContext.getSqlConnection();
		
		IContactsMappingService service = null;
		List<ContactMapping> mappings = null;
		try {
			IContactsMappingDao dao = new ContactsMappingDao(conn);
			service = new ContactsMappingService(mContext, dao);
			mappings = service.fetchMappingByBID(borqsId);
			
			// convert to JSON
			String content = service.convertToJson(mappings, cols);
			if("false".equals(formated)) {
				CharSequence newline = System.getProperty("line.separator");
				content = content.replace(" ", "").replace(newline, "");
			}
			
			ResponseWriterUtil.writeObjectJson(content,writer);
		} catch (Exception e) {
			e.printStackTrace();
			WebLog.getLogger(mContext).info(e.getMessage());
			throw new AccountException(AccountErrorCode.UNKNOWN_ERROR, e);
		} finally {
			DBUtility.close(conn, null, null);
		}
    }
}
