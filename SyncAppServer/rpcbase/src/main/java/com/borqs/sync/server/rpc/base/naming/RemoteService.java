/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.base.naming;

import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.RPCStatus;
import com.borqs.sync.server.rpc.base.transport.RpcServiceURI;
import com.borqs.sync.server.rpc.base.transport.TransceiverFactory;
import org.apache.avro.ipc.Ipc;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.URI;

/**
 * Date: 9/16/11
 * Time: 4:02 PM
 */
public final class RemoteService{
    private NamingServiceProxy mNamingService = null;
    private Class mIface;
    private RpcServiceURI mRemoteURI;
    private Transceiver mTransceiver;

    public RemoteService(Class iface, NamingServiceProxy naming) throws RPCException {
        mIface = iface;
        mNamingService = naming;
        if(mNamingService == null){
            throw new RPCException("Null naming.");
        }
        mTransceiver = null;
    }

    public void destroy(){
        TransceiverFactory.destroyTransceiver(mTransceiver);
        mNamingService.destroy();
        mTransceiver = null;
    }


    public <T> T asInterface() throws RPCException, NotFoundException{
        try {
            RpcServiceURI remoteUri = mNamingService.findRPCService(mIface.getName());
            URI uri = remoteUri.getShortURI();

            if(uri == null){
                throw new NotFoundException();
            }
            mTransceiver = Ipc.createTransceiver(uri);
			T proxy=
				(T) SpecificRequestor.getClient(mIface, mTransceiver);
            return proxy;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPCException(RPCStatus.RPC_REQUEST_IO_ERROR, e);
        }
    }

    public boolean register(String schema, String host, int port, boolean supportMulti) throws RPCException, DuplicatedException {
        RpcServiceURI uri = new RpcServiceURI();
        uri.setSchema(schema)
                .setHost(host)
                .setPort(port)
                .setIface(mIface.getName());

        if(mNamingService == null){
            mNamingService = NamingServiceProxy.getLocalNaming();
        }
        String token = mNamingService.registerRPCService(uri, supportMulti);
        mRemoteURI = uri;
        mRemoteURI.setToken(token);
        return true;
    }


    public void unregister() throws RPCException {
        if(mRemoteURI == null){
            throw new RPCException("Not the owner of this RPC service.");
        }
        if(mNamingService == null){
            mNamingService = NamingServiceProxy.getLocalNaming();
        }
        mNamingService.unregisterRPCService(mRemoteURI);
    }
}
