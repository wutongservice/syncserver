/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.test;

import com.borqs.sync.avro.IContactSyncMLProvider;
import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.json.JsonWriter;
import com.borqs.sync.server.common.providers.ContactProvider;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;
import com.borqs.sync.server.rpc.base.naming.NotFoundException;
import com.borqs.sync.server.rpc.base.naming.RemoteService;
import com.borqs.sync.server.webagent.dao.ContactDAO;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;
import org.apache.avro.AvroRemoteException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Date: 7/30/12
 * Time: 5:25 PM
 * Borqs project
 */
public class ContactTestImpl {
    private Context mContext;
    private Logger mLogger;
    public ContactTestImpl(Context context){
        mContext =context;
        mLogger = context.getLogger("ContactTestImpl");
    }

    public void addContacts(String userId, List<String> contacts, JsonWriter writer) throws AccountException {
        RemoteService rs = null;
        try {
            NamingServiceProxy ns =  NamingServiceProxy.getLocalNaming();
            rs = new RemoteService(IContactSyncMLProvider.class, ns);
            IContactSyncMLProvider provider = rs.asInterface();
            long now = System.currentTimeMillis();
            for(String contact : contacts){
                provider.addItem(userId, contact, now);
            }
            writeResult(writer, 200);
            writer.flush();
            writer.close();
        } catch (RPCException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } finally {
            if(rs!=null) rs.destroy();
        }
    }


    public void loadContacts(String userid, JsonWriter writer) throws AccountException {
        mLogger.info("loadContacts: " + userid);
        ContactProvider provider = new ContactProvider(mContext);
        List<Long> contactList = provider.listContactIds(userid, false);

        List<String> strIds = new ArrayList<String>(contactList.size());
        for(long id : contactList){
            strIds.add(String.valueOf(id));
        }
        loadContact(userid, strIds.toArray(new String[0]), writer);
    }

    public void loadContact(String userid, String[] contactIds, JsonWriter writer) throws AccountException {
        mLogger.info("loadContact: " + contactIds.toString());
        RemoteService rs = null;
        try {
            NamingServiceProxy ns =  NamingServiceProxy.getLocalNaming();
            rs = new RemoteService(IContactSyncMLProvider.class, ns);
            IContactSyncMLProvider syncProvider = rs.asInterface();

            writer.beginArray();
            for(String id : contactIds){
//                mLogger.info("loadContact: load " + id);
                writer.beginObject();
                writer.name(String.valueOf(id));

                XResponse resp = syncProvider.getItem(userid, String.valueOf(id));
//                mLogger.info("loadContact: " + resp.content);
                writer.value(String.valueOf(resp.content));
                writer.endObject();
            }
            writer.endArray();
            writer.flush();
            writer.close();
        } catch (RPCException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } finally {
            if(rs!=null) rs.destroy();
        }
    }

    public void updateContacts(String userid, JsonReader jsonReader, JsonWriter writer) throws AccountException {
        RemoteService rs = null;
        try {
            NamingServiceProxy ns =  NamingServiceProxy.getLocalNaming();
            rs = new RemoteService(IContactSyncMLProvider.class, ns);
            IContactSyncMLProvider syncProvider = rs.asInterface();

            jsonReader.setLenient(true);
            jsonReader.beginArray();
            long now = System.currentTimeMillis();
            while(jsonReader.hasNext()){
                jsonReader.beginObject();
                String id = jsonReader.nextName();
                String content = jsonReader.nextString();
                syncProvider.updateItem(userid, id, content, now);
                jsonReader.endObject();
            }
            writeResult(writer, 200);
            writer.flush();
            writer.close();
        } catch (RPCException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } finally {
            if(rs!=null) rs.destroy();
        }
    }

    public void queryIds(String userid, ResponseWriter writer) throws AccountException {
        ContactProvider provider = new ContactProvider(mContext);
        List<Long> contactList = provider.listContactIds(userid, false);
        JSONArray idArray = new JSONArray();
        for(Long id:contactList){
            idArray.put(id);
        }
        try {
            ResponseWriterUtil.writeObjectJson(idArray.toString(),writer);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }

    public void queryFriendIds(String userid, ResponseWriter writer) throws AccountException {
        ContactDAO contactDAO = new ContactDAO(mContext);
        List<Long> contactList = contactDAO.queryFriendIds(userid);
        JSONArray idArray = new JSONArray();
        for(Long id:contactList){
            idArray.put(id);
        }
        try {
            ResponseWriterUtil.writeObjectJson(idArray.toString(),writer);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }

    public void deleteContacts(String userid, JsonWriter writer) throws AccountException {
        RemoteService rs = null;
        try {
            NamingServiceProxy ns =  NamingServiceProxy.getLocalNaming();
            rs = new RemoteService(IContactSyncMLProvider.class, ns);
            IContactSyncMLProvider syncProvider = rs.asInterface();

            long now = System.currentTimeMillis();
            syncProvider.removeAllItems(userid, now);
            writeResult(writer, 200);
            writer.flush();
            writer.close();
        } catch (RPCException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } finally {
            if(rs!=null) rs.destroy();
        }
    }

    public void deleteContact(String userid, String[] ids, JsonWriter writer) throws AccountException {
        RemoteService rs = null;
        try {
            NamingServiceProxy ns =  NamingServiceProxy.getLocalNaming();
            rs = new RemoteService(IContactSyncMLProvider.class, ns);
            IContactSyncMLProvider syncProvider = rs.asInterface();

            long now = System.currentTimeMillis();
            for(String id : ids){
                syncProvider.removeItem(userid, id, now);
            }
            writeResult(writer, 200);
            writer.flush();
            writer.close();
        } catch (RPCException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } finally {
            if(rs!=null) rs.destroy();
        }
    }

    public static List<String> toContactList(String jsonContacts){
        ArrayList<String> contactList = new ArrayList<String>();
        try {
            JSONArray contacts = new JSONArray(jsonContacts);
            for(int i=0; i<contacts.length(); i++){
                String o = contacts.getString(i);
                contactList.add(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactList;
    }

    private void writeResult(JsonWriter writer, int code) throws IOException {
        writer.beginObject();
        writer.name("result_code");
        writer.value(code);
        writer.endObject();
    }
}
