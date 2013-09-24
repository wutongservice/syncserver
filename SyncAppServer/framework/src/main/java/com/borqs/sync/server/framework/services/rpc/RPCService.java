/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.rpc;

import com.borqs.sync.server.framework.BaseService;
import com.borqs.sync.server.framework.ServiceDescriptor;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.rpc.base.transport.RpcServiceURI;
import com.borqs.sync.server.common.util.ReflectUtil;

import java.util.logging.Logger;

/**
 * Date: 9/8/11
 * Time: 6:50 PM
 */
public class RPCService extends BaseService {
    private static final String RPC_SERVICE_PREFIX = "rpc#";
    private static final String IDENTIFIER = "RPC_service";

    private RPCServerLauncher mServiceServant;

    public RPCService(Context context){
        super(context);
        mServiceServant = null;
    }

    @Override
    public void runSynchronized(Context context) {
        Logger logger = context.getLogger();
		ConfigurationBase config = context.getConfig();

        String namingHost = config.getNamingHost();
		int namingPort = config.getNamingPort();
        NamingServiceProxy namingService = null;
        try {
            namingService = new NamingServiceProxy(namingHost, namingPort);
        } catch (RPCException e) {
            e.printStackTrace();
            context.getLogger().info("Failed to init naming base service on " + namingHost +":" + namingPort);
            return;
        }

        mServiceServant = initServiceLancher(mDescriptor);
        if(mServiceServant != null){
            mServiceServant.start(namingService);
        }else{
            logger.info("Server exit for failed to init service list.");
            return;
        }

        //hold
        mServiceServant.waitForCompleted();
        logger.info("Server exit : " + mDescriptor.impl());
    }

    @Override
    public synchronized void stop() {
        mServiceServant.destroy();
        mServiceServant = null;
    }

    @Override
    public boolean isRunning() {
        return mServiceServant != null;
    }

    @Override
    protected String getIdentifier() {
        return mDescriptor.desc();
    }

    //refer to config/rpcserver.properties
    private RPCServerLauncher initServiceLancher(ServiceDescriptor service){
        //parse interface name
        String ifaceName = service.intf();
        //instance
        Class iface = ReflectUtil.forName(service.intf());
        Object impl = ReflectUtil.newInstance(service.impl(), getContext());

        //double check
        if(iface == null || impl == null ||
            !ReflectUtil.interfaceOf(impl, iface) ||
            !RpcServiceURI.isValidSchema(service.schema())){
                getContext().getLogger().info("Failed to bind server :<" +(service.impl()).toLowerCase() + ">");
            return null;
        }

        return new RPCServerLauncher(iface, impl, service.schema());
    }
}
