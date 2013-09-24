/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.rpc;

import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.RemoteService;
import com.borqs.sync.server.rpc.base.naming.DuplicatedException;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;
import com.borqs.sync.server.rpc.base.transport.RpcServiceURI;
import org.apache.avro.ipc.Ipc;
import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Date: 9/16/11
 * Time: 6:03 PM
 */
public class RPCServerLauncher {
    private String mServiceToken;
    private Class mServiceInterface;
    private Object mServiceImpl;
    private String mSchema;
    private String mLocalHost;
    private int mPort;
    private Server mServiceContainer;
    private RemoteService mServiceAgent;

    public RPCServerLauncher(Class iface, Object impl, String schema){
        mServiceImpl = impl;
        mServiceInterface = iface;
        mSchema = schema;
        if(mSchema == null || mSchema.isEmpty()){
            mSchema = RpcServiceURI.URI_SCHEMA_SOCKET;
        }
    }

    public boolean start(NamingServiceProxy namingService){
        Responder responder =
           new SpecificResponder(mServiceInterface, mServiceImpl);

        mPort = SystemResource.acquirePort();
        mLocalHost = SystemResource.getHost();

        try {
            URI uri = new URI(mSchema, null, mLocalHost, mPort, null,null, null);
            mServiceContainer = Ipc.createServer(responder, uri);
            mServiceContainer.start();
            Logger.getLogger("RPC").info("Start rpc server [" + uri.toString() +"]");

            mServiceAgent = new RemoteService(mServiceInterface, namingService);
            try {
                return mServiceAgent.register(mSchema, mLocalHost, mPort, true);
            } catch (RPCException e) {
                e.printStackTrace();
                return false;
            } catch (DuplicatedException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (RPCException e){
            e.printStackTrace();
            return false;
        }
    }

    public void waitForCompleted(){
        if(mServiceContainer != null){
            try {
                mServiceContainer.join();
            } catch (InterruptedException e) {}
        }
    }

    public void destroy(){
        if(mServiceContainer != null){
            mServiceContainer.close();
            try {
                mServiceAgent.unregister();
                mServiceAgent.destroy();
            } catch (RPCException e) {
                e.printStackTrace();
            }
        }
    }

    private String getShortName(){
        return "_S_" + mServiceImpl.getClass().getName();
    }
}
