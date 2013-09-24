/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.contact.dao;

import com.borqs.sync.avro.IContactSyncMLProvider;
import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.contact.BCSConfig;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;
import com.borqs.sync.server.rpc.base.naming.NotFoundException;
import com.borqs.sync.server.rpc.base.naming.RemoteService;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.json.dao.JsonDAO;
import com.funambol.json.domain.JsonResponse;
import com.funambol.json.exception.HttpException;

import java.io.IOException;

/**
 * Date: 2/20/12
 * Time: 3:47 PM
 */
public class JsonDAOAvroImpl implements JsonDAO {
    private String mSyncTypeAndTime;
    private Sync4jPrincipal mPrincipal;

    public JsonDAOAvroImpl(Sync4jPrincipal principal) throws RPCException, NotFoundException {
        mPrincipal = principal;
    }

    @Override
    public JsonResponse beginSync(String token, String jsonObject) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            mSyncTypeAndTime = jsonObject;
            XResponse resp = contactDataProvider.beginSync(token, jsonObject);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse endSync(String token) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.endSync(token,mSyncTypeAndTime
                    ,String.valueOf(mPrincipal.getId()),mPrincipal.getDeviceId());
            int statusCode = resp.status_code;
            String content = resp.content.toString();
            return new JsonResponse(statusCode, content);
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse addItem(String token, String jsonObject, long since) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.addItem(token, jsonObject, since);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse getItem(String token, String id) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.getItem(token, id);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse updateItem(String token, String id, String jsonObject, long since) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.updateItem(token, id, jsonObject, since);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse removeItem(String token, String id, long since) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.removeItem(token, id, since);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse removeAllItems(String token, long since) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.removeAllItems(token, since);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse getAllItemKeys(String token) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.getAllItemKeys(token);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse getNewItemKeys(String token, long since, long until) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.getNewItemKeys(token, since, until);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse getUpdatedItemKeys(String token, long since, long until) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.getUpdatedItemKeys(token, since, until);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse getDeletedItemKeys(String token, long since, long until) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.getDeletedItemKeys(token, since, until);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse getItemKeysFromTwin(String token, String jsonObject) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.getItemKeysFromTwin(token, jsonObject);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    @Override
    public JsonResponse getTimeConfiguration(String token) throws HttpException, IOException {
        RemoteService rs = null;
        try {
            rs = createAccountSyncService();
            IContactSyncMLProvider contactDataProvider = rs.asInterface();
            XResponse resp = contactDataProvider.getTimeConfiguration(token);
            return new JsonResponse(resp.status_code, resp.content.toString());
        } catch (RPCException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            if(rs != null){
                rs.destroy();
            }
        }
//        return new JsonResponse(200, new String());
        throw new IOException();
    }

    private RemoteService createAccountSyncService() throws RPCException, NotFoundException {
        String naming_host = BCSConfig.getConfigString(BCSConfig.NAMING_HOST);
        int naming_port = BCSConfig.getConfigInt(BCSConfig.NAMING_PORT);

        NamingServiceProxy ns = new NamingServiceProxy(naming_host, naming_port);
        RemoteService rs = new RemoteService(IContactSyncMLProvider.class,ns);
        return rs;
    }

}
