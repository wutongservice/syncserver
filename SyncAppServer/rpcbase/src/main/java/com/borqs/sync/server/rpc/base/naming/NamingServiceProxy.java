/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.base.naming;

import com.borqs.sync.avro.IRPCNamingService;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.RPCStatus;
import com.borqs.sync.server.rpc.base.transport.RpcServiceURI;
import com.borqs.sync.server.rpc.base.transport.TransceiverFactory;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Date: 9/16/11
 * Time: 3:55 PM
 */
public final class NamingServiceProxy {
    private final static String TAG = "Naming_Service";

    public final static String DEFAULT_SCHEMA = RpcServiceURI.URI_SCHEMA_SOCKET;
    public final static String DEFAULT_HOST = "127.0.0.1";
    public final static int DEFAULT_PORT = 8999;

    private Logger mLogger = Logger.getLogger(TAG);
    private Transceiver mTransceiver;
    private IRPCNamingService mServiceStub;

    public static NamingServiceProxy getLocalNaming() throws RPCException {
        return new NamingServiceProxy();
    }

    public NamingServiceProxy(String host, int port) throws RPCException {
        try {
            URI serviceUrl = new URI(DEFAULT_SCHEMA, null, host, port, null, null,null);

            mTransceiver = TransceiverFactory.createTransceiver(serviceUrl);
            mServiceStub = (IRPCNamingService)
                    SpecificRequestor.getClient(IRPCNamingService.class, mTransceiver);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            mLogger.info("Failed to connect naming service on : " + host +":" + port);
            throw new RPCException(RPCStatus.RPC_REQUEST_BAD_DATA, e);
        } catch (IOException e) {
            e.printStackTrace();
            mLogger.info("Failed to connect naming service on : " + host +":" + port);
            throw new RPCException(RPCStatus.RPC_REQUEST_IO_ERROR, e);
        }
    }

    private NamingServiceProxy() throws RPCException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public void destroy(){
        TransceiverFactory.destroyTransceiver(mTransceiver);
        mTransceiver = null;
    }

    public String registerRPCService(RpcServiceURI serviceURI, boolean multiInstance) throws RPCException, DuplicatedException {
        if(mServiceStub == null){
            throw new RPCException("Runtime: null parameter");
        }

        String reqest_uri = serviceURI.toString();
        try {
            String remoteURI = mServiceStub.register(reqest_uri, multiInstance).toString();
            RpcServiceURI response_uri = new RpcServiceURI(remoteURI);
            String token = response_uri.getToken();
            if(token != null && !token.isEmpty()){
                if(RPCStatus.RPC_RESPONSE_DUP_SERVICE.equals(token)){
                    throw new DuplicatedException();
                }
                return token;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RPCException(RPCStatus.RPC_REQUEST_BAD_DATA, e);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
            throw new RPCException(RPCStatus.RPC_RESPONSE_SERVER_ERROR, e);
        }

        throw new RPCException(RPCStatus.RPC_RESPONSE_SERVER_ERROR);
    }


    public void unregisterRPCService(RpcServiceURI serviceURI){
        if(mServiceStub != null){
             mServiceStub.unregister(serviceURI.toString());
        }
    }

    public RpcServiceURI findRPCService(String ifcae) throws RPCException, NotFoundException{
        try{
            if(mServiceStub != null){
                String serviceURI = mServiceStub.lookup(ifcae).toString();
                if(serviceURI==null || serviceURI.isEmpty()){
                    throw new NotFoundException();
                }
                return new RpcServiceURI(serviceURI);
            }
        } catch (AvroRemoteException e){
            e.printStackTrace();
            throw new RPCException(RPCStatus.RPC_RESPONSE_SERVER_ERROR, e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RPCException(RPCStatus.RPC_REQUEST_BAD_DATA, e);
        }
        throw new RPCException(RPCStatus.RPC_REQUEST_BAD_DATA);
    }
}
