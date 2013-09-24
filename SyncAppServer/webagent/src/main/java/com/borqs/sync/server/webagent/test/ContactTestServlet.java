/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.test;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.HttpServletDelegate;
import com.borqs.sync.server.common.httpservlet.PostData;
import com.borqs.sync.server.common.httpservlet.QueryParams;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.httpservlet.WebMethod;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Date: 7/30/12
 * Time: 5:10 PM
 * Borqs project
 */
public class ContactTestServlet extends HttpServletDelegate {
    private ContactTestImpl mImpl;
    public ContactTestServlet(Context context) {
        super(context);
        mImpl = new ContactTestImpl(context);
    }

    /**
     * [{JPIM},{JPIM}]
     * @param in
     * @param out
     * http://.../contact/add?userid=1000
     */
    @WebMethod("contact/add")
    public void add(QueryParams params, PostData in, ResponseWriter out) throws AccountException {
        try {
            String userid = params.getString("userid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("result_code", "420", out);
                return;
            }
            List<String> contactList = ContactTestImpl.toContactList(in.asString(Charset.defaultCharset()));
            mImpl.addContacts(userid, contactList, out.asJsonWriter());
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
     * http://.../contact/query?userid=1000&contactids=100,1002
     */
    @WebMethod("contact/query")
    public void query(QueryParams in, ResponseWriter out) throws AccountException {
        try{
            String userid = in.getString("userid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("result_code", "420", out);
                return;
            }
            String contactIds = in.getString("contactids", null);
            if(contactIds==null || "".equals(contactIds)){
                mImpl.loadContacts(userid, out.asJsonWriter());
            } else {
                mImpl.loadContact(userid, contactIds.split(","), out.asJsonWriter());
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
     * http://.../contact/update?userid=1000
     */
    @WebMethod("contact/update")
    public void update(QueryParams in, PostData contacts, ResponseWriter out) throws AccountException {
        try{
            String userid = in.getString("userid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("result_code", "420", out);
                return;
            }

            mImpl.updateContacts(userid, contacts.asJsonReader(Charset.defaultCharset()), out.asJsonWriter());
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
     * http://.../contact/del?userid=1000&contactids=1,2
     * result: {count:100}
     */
    @WebMethod("contact/del")
    public void delete(QueryParams in, ResponseWriter out) throws AccountException {
        try{
            String userid = in.getString("userid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("result_code", "420", out);
                return;
            }

            String contactIds = in.getString("contactids", null);
            if(contactIds==null || "".equals(contactIds)){
                mImpl.deleteContacts(userid, out.asJsonWriter());
                return;
            } else {
                mImpl.deleteContact(userid, contactIds.split(","), out.asJsonWriter());
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
     * http://.../contact/count?userid=1000
     * result: {count:100}
     */
    @WebMethod("contact/queryids")
    public void count(QueryParams in, PostData contacts, ResponseWriter out) throws AccountException {
        try{
            String userid = in.getString("userid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("result_code", "420", out);
                return;
            }

            mImpl.queryIds(userid, out);
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
     * http://.../contact/count?userid=1000
     * result: {count:100}
     */
    @WebMethod("contact/queryfriendids")
    public void querFriendIds(QueryParams in, PostData contacts, ResponseWriter out) throws AccountException {
        try{
            String userid = in.getString("userid", null);
            if(userid==null || "".equals(userid)){
                ResponseWriterUtil.writeStringJson("result_code", "420", out);
                return;
            }

            mImpl.queryFriendIds(userid, out);
        } catch (IOException e){
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }
}
