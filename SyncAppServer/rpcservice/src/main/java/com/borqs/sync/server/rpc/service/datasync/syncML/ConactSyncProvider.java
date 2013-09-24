/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.service.datasync.syncML;

import com.borqs.sync.avro.IContactSyncMLProvider;
import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.rpc.service.datasync.RpcServiceLogger;
import org.apache.avro.AvroRemoteException;

import java.util.List;
import java.util.logging.Logger;

/**
 * Date: 3/12/12
 * Time: 6:33 PM
 * Borqs project
 */
public class ConactSyncProvider implements IContactSyncMLProvider {
    private Logger mLog;
    private SyncService mSyncService;//sync logic service

    public ConactSyncProvider(Context context){
        mLog = RpcServiceLogger.getLogger(context);
        mSyncService = new SyncService(context);
    }

    @Override
    public XResponse beginSync(CharSequence token, CharSequence jsonObject) throws AvroRemoteException {
        String syncMode = Protocols.parseSyncMode(jsonObject);
        long since = Protocols.parseSince(jsonObject);
        String userId = token.toString();

        mLog.info("beginSync,the syncMode:" + syncMode + ",since:" + since + ",userid:" + userId) ;

        mSyncService.beginSync(userId, syncMode, since);

        return Protocols.OK;
    }

    @Override
    public XResponse endSync(CharSequence token,CharSequence jsonObject,CharSequence principalId, CharSequence deviceId) throws AvroRemoteException {
        String syncMode = Protocols.parseSyncMode(jsonObject);
        long since = Protocols.parseSince(jsonObject);
        String userId = token.toString();
        long principal = Long.parseLong(principalId.toString());
        String device =  deviceId.toString();

        mSyncService.endSync(userId,syncMode,since,principal,device);

        return Protocols.OK;
    }

    @Override
    public XResponse addItem(CharSequence token, CharSequence jsonObject, long since) throws AvroRemoteException {
        String userid = token.toString();
        String contactjson  = jsonObject.toString();

        mLog.info("contact inserted,userid : " + userid + ",contactJson:" + contactjson  + ",since:" + since);

        long contactId = mSyncService.addItem(userid,contactjson,since);

        mLog.info("contact inserted,the id is : " + contactId);
        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateKeyJson(contactId);
        return response;
    }

    @Override
    public XResponse getItem(CharSequence token, CharSequence id) throws AvroRemoteException {
        String contactId = id.toString();
        mLog.info("getitem contactid:" + contactId + ",userid:" + token.toString());

        String contactJson =  mSyncService.getItem(contactId);

        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateContactJson(contactJson);
        return response;
    }

    @Override
    public XResponse updateItem(CharSequence token, CharSequence id, CharSequence jsonObject, long since) throws AvroRemoteException {
        String userid = token.toString();
        String contactId = id.toString();
        String contactJson = jsonObject.toString();

        mLog.info("updateItem,userid:" + userid + ",contactId" + contactId + ",contactJson :" + contactJson);

        boolean updated = mSyncService.updateItem(userid,contactId,contactJson,since);

        mLog.info("contact: " + id + "  updated,the result is : " + updated);
        return Protocols.OK;
    }

    @Override
    public XResponse removeItem(CharSequence token, CharSequence id, long since) throws AvroRemoteException {
        boolean deleted = mSyncService.deleteItem(token.toString(),Long.parseLong(id.toString()),since);
        mLog.info("contact :" + id + " removed,the result is : " + deleted);
        return Protocols.OK;
    }

    @Override
    public XResponse removeAllItems(CharSequence token, long since) throws AvroRemoteException {
        boolean deleted = mSyncService.deleteAllItemsByUser(token.toString(),since);
        mLog.info("all contacts of " + token.toString() + " have been update to 'D',result :" + deleted);
        return Protocols.OK;
    }

    @Override
    public XResponse getAllItemKeys(CharSequence token) throws AvroRemoteException {
        List<Long> keys = mSyncService.getAllItemsKeys(token.toString());
        mLog.info("get all keys: " + keys.toString());
        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateKeysJson(keys);
        return response;
    }

    @Override
    public XResponse getNewItemKeys(CharSequence token, long since, long until) throws AvroRemoteException {
        mLog.info("getNewItemKeys,the sicne :" + since + ",the until :" + until);
        List<Long> keys = mSyncService.getNewItemKeys(token.toString(),since,until);
        mLog.info("get added keys: " + keys.toString());
        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateKeysJson(keys);
        return response;
    }

    @Override
    public XResponse getUpdatedItemKeys(CharSequence token, long since, long until) throws AvroRemoteException {
        mLog.info("getUpdatedItemKeys,the sicne :" + since + ",the until :" + until);
        List<Long> keys = mSyncService.getUpdateItemKeys(token.toString(), since, until);
        mLog.info("get updated keys: " + keys.toString());
        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateKeysJson(keys);
        return response;
    }

    @Override
    public XResponse getDeletedItemKeys(CharSequence token, long since, long until) throws AvroRemoteException {
        mLog.info("getDeletedItemKeys,the sicne :" + since + ",the until :" + until);
        List<Long> keys = mSyncService.getDeletedItemKeys(token.toString(), since, until);
        mLog.info("get deleted keys: " + keys.toString());
        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateKeysJson(keys);
        return response;
    }

    @Override
    public XResponse getItemKeysFromTwin(CharSequence token, CharSequence jsonObject) throws AvroRemoteException {
        List<Long> keys = mSyncService.getItemForTwins(jsonObject.toString(),token.toString());
        mLog.info("get getItemKeysFromTwin keys: " + keys.toString());
        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateKeysJson(keys);
        return response;
    }

    @Override
    public XResponse getTimeConfiguration(CharSequence token) throws AvroRemoteException {
        //TODO how to get the time and tzid
        XResponse response = new XResponse();
        response.status_code = Protocols.StatusCode.OK;
        response.content = Protocols.generateTimeJson(System.currentTimeMillis(),"Asia/Shanghai");
        return response;
    }


}
