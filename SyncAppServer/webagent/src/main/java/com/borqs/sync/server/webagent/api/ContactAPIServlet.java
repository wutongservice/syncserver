/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.api;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.mortbay.log.Log;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.PostData;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;

/**
 * Contacts Open API Servlet, WWW root path: sync/api/contacts
 * @author b534
 *
 */
public class ContactAPIServlet extends HttpServletDelegate {
    private IContactsAPIService service;
    
    public ContactAPIServlet(Context context) {
        super(context);
        service = new ContactsAPIServiceImpl(context);
    }

    /**
     * [{JPIM},{JPIM}]
     * @param in
     * @param out
     * http://.../sync/api/contacts/add?uid=1000
     */
    @WebMethod("add")
    public void add(QueryParams params, PostData in, ResponseWriter out) throws AccountException {
        try {
        	String callback = params.getString("callback", null);
            String userid = params.getString("uid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeResultJsonp(out, 420, "", callback);
                return;
            }
            List<String> contactList = ContactsAPIServiceImpl.toContactList(in.asString(Charset.defaultCharset()));
            service.addContacts(userid, contactList, out, callback);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }

    /**
     *
     * @param in
     * @param out
     * @throws AccountException
     * http://.../sync/api/contacts/query?uid=1000&contactids=100,1002
     */
    @WebMethod("query")
    public void query(QueryParams in, ResponseWriter out) throws AccountException {
        try{
        	// get request parameters
            String userid = in.getString("uid", null);
            
            String sOffset = in.getString("offset", null);
            String sCount = in.getString("count", null);
            
            String fields = in.getString("f", null);
            Log.info("output fields is "+fields);
            
            String contactIds = in.getString("cid", null);
            String callback = in.getString("callback", null);
            
            // when parameters are invalid
            if((userid==null || "".equals(userid)) && null == contactIds){
                ResponseWriterUtil.writeResultJsonp(out, 420, "", callback);
                return;
            }
            
            
            if(contactIds==null || "".equals(contactIds)){
            	if(null == sOffset && null == sCount) {
            		service.loadContacts(userid, fields, out, callback);
            	} else {
            		int offset = (null == sOffset)?0:Integer.valueOf(sOffset);
            		int count = (null == sCount)?-1:Integer.valueOf(sCount);
            		Log.info("offset = "+offset+",count="+count);
            		service.loadContactsWithLimit(userid, fields, offset, count, out, callback);
            	}
            } else {
                service.loadContact(userid, contactIds.split(","), fields, out, callback);
            }
        } catch (IOException e){
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }

    /**
     *
     * @param in
     * @param out
     * @throws AccountException
     * http://.../sync/api/contacts/update?uid=1000&cid=10
     */
    @WebMethod("update")
    public void update(QueryParams in, PostData contacts, ResponseWriter out) throws AccountException {
        try{
        	String callback = in.getString("callback", null);
        	
            String userid = in.getString("uid", null);
            String contactId = in.getString("cid", callback);
            if(userid==null || "".equals(userid)
            		|| contactId==null || "".equals(contactId)){
                ResponseWriterUtil.writeResultJsonp(out, 420, "", callback);
                return;
            }

            service.updateContact(userid, contactId, contacts.asString(Charset.defaultCharset()), out, callback);
            ResponseWriterUtil.writeResultJsonp(out, 200, contactId, callback);
        } catch (IOException e){
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }

    /**
     *
     * @param in
     * @param out
     * @throws AccountException
     * http://.../sync/api/contacts/del?uid=1000&contactids=1,2
     * result: {count:100}
     */
    @WebMethod("del")
    public void delete(QueryParams in, ResponseWriter out) throws AccountException {
        try{
            String userid = in.getString("uid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("code", "420", out);
                return;
            }
            
            String callback = in.getString("callback", null);

            String contactIds = in.getString("cid", callback);
            if(contactIds==null || "".equals(contactIds)){
                service.deleteContacts(userid, out, callback);
                return;
            } else {
//                service.deleteContact(userid, contactIds.split(","), out);
            	service.batchDeleteContact(userid, contactIds, out, callback);
            }
        } catch (IOException e){
            e.printStackTrace();
            throw AccountException.create(e);
        }

    }


    /**
     *
     * @param in
     * @param out
     * @throws AccountException
     * http://.../sync/api/contacts/count?uid=1000
     * result: {count:100}
     */
    @WebMethod("count")
    public void count(QueryParams in, PostData contacts, ResponseWriter out) throws AccountException {
        try{
            String userid = in.getString("uid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("code", "420", out);
                return;
            }
            
            String callback = in.getString("callback", null);

            service.count(userid, out, callback);
        } catch (IOException e){
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }
}
