/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.naming;

import com.borqs.sync.avro.IRPCNamingService;
import com.borqs.sync.server.framework.BaseService;
import com.borqs.sync.server.rpc.base.RPCStatus;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.rpc.base.transport.RpcServiceURI;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.Ipc;
import org.apache.avro.ipc.Responder;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Date: 9/16/11
 * Time: 4:10 PM
 */
public final class NamingService extends BaseService {
    private static final String IDENTIFIER = "Naming_Service";

    private Logger mLogger;
    private Server mServer;

    public NamingService(Context context) {
        super(context);
        mLogger = context.getLogger();
    }

    @Override
    public boolean isRunning() {
        return mServer != null;
    }

    @Override
    public void stop() {
        mServer.close();
    }

    @Override
    protected void runSynchronized(Context context) {
         Responder responder =
                new SpecificResponder(IRPCNamingService.class, new SimpleNativeNaming());

        int port = context.getConfig().getNamingPort();
        String localHost = context.getConfig().getNamingHost();

        try {
            URI uri = new URI(RpcServiceURI.URI_SCHEMA_SOCKET, null, localHost, port, null,null, null);
            mServer = Ipc.createServer(responder, uri);
            mServer.start();
            mLogger.info("Naming service is running on " + localHost + ":" + port);
            mServer.join();
            mLogger.info("Naming service abort");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally{
            mServer = null;
        }
    }

    @Override
    protected String getIdentifier() {
        return IDENTIFIER;
    }

    class SimpleNativeNaming implements IRPCNamingService {
        private List<RpcServiceURI> mServices;

        SimpleNativeNaming(){
            LinkedList<RpcServiceURI> rawList = new LinkedList<RpcServiceURI>();
            mServices = Collections.synchronizedList(rawList);
        }

        @Override
        public CharSequence register(CharSequence serviceURI, boolean multiInstance) throws AvroRemoteException {
            mLogger.info("Naming:register() : " + serviceURI +"," +multiInstance);
            try {
                RpcServiceURI uri = new RpcServiceURI(serviceURI.toString());
                synchronized (mServices){
                    if(hasService(uri.getIface()) && !multiInstance){
                        return RPCStatus.RPC_RESPONSE_DUP_SERVICE;
                    }
                    String token = createToken(uri);
                    uri.setToken(token);
                    mServices.add(uri);
                }
                mLogger.info("Naming:register() Token=" + uri.toString());
                return uri.toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            mLogger.info("Naming:register() failed!");
            return RPCStatus.RPC_REQUEST_BAD_DATA;
        }

        @Override
        public void unregister(CharSequence serviceURI) {
            mLogger.info("Naming:unregister() : " + serviceURI);
            try {
                RpcServiceURI uri = new RpcServiceURI(serviceURI.toString());
                removeService(uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            mLogger.info("Naming:unregister() end");
        }

        @Override
        public CharSequence lookup(CharSequence serviceProtocol) throws AvroRemoteException {
            mLogger.info("Naming:lookup() :" + serviceProtocol);
            for(RpcServiceURI service : mServices){
                if(service.getIface().equals(serviceProtocol.toString())){
                    mLogger.info("Naming:lookupService() result:" + service.toString());
                    return service.toString();
                }
            }
            mLogger.info("Naming:lookup() Not found!");
            return null;
        }

        private boolean hasService(String serviceProtocol){
            for(RpcServiceURI service : mServices){
                if(service.getIface().equals(serviceProtocol)){
                    return true;
                }
            }
            return false;
        }


        private boolean removeService(RpcServiceURI uri){
            synchronized (mServices){
                int index = findService(uri);
                if(index != -1){
                    mServices.remove(index);
                }
            }
            return true;
        }

        private String createToken(RpcServiceURI uri){
            return uri.getIface() + String.valueOf(System.currentTimeMillis());
        }

        private int findService(RpcServiceURI uri){
            for(RpcServiceURI service : mServices){
                if(service.equals(uri)){
                    return mServices.indexOf(service);
                }
            }
            return -1;
        }
    }
}
