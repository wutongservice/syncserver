/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.rpc;

import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.framework.MockContext;
import com.borqs.sync.server.framework.ServiceDescriptor;
import com.borqs.sync.server.framework.services.naming.NamingService;
import com.borqs.sync.server.rpc.base.RPCException;
import com.borqs.sync.server.rpc.base.naming.NamingServiceProxy;
import com.borqs.sync.server.rpc.base.naming.NotFoundException;
import com.borqs.sync.server.rpc.base.naming.RemoteService;
import com.borqs.sync.server.rpc.base.transport.RpcServiceURI;
import org.apache.avro.AvroRemoteException;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Date: 9/21/11
 * Time: 6:35 PM
 */
public class RPCServerLauncher_test {
    private static final String NS_DES = "{service:com.borqs.sync.server.framework.services.naming.NamingService,\n" +
            "   enable:true,\n" +
            "   priority:0,\n" +
            "   desc:naming\n" +
            "  }";
    
    @Test
    public void run_test() throws RPCException, NotFoundException, URISyntaxException, AvroRemoteException, JSONException {
        Context context = new MockContext();
        SystemResource.init(context);
        NamingService ns = new NamingService(context);
        ns.init(ServiceDescriptor.from(new JSONObject(NS_DES)));

        try{
            ns.start(null);

            // start service impl
            TestAvroServiceImpl nsImpl = new TestAvroServiceImpl(context);
            RPCServerLauncher launcher = new RPCServerLauncher(ITestAvroService.class, nsImpl, RpcServiceURI.URI_SCHEMA_SOCKET);

            NamingServiceProxy nsproxy = new NamingServiceProxy(
                    context.getConfig().getNamingHost(), context.getConfig().getNamingPort()
            );
            assertTrue(launcher.start(nsproxy));

            //verify as client
            RemoteService rs = new RemoteService(ITestAvroService.class,nsproxy);
            ITestAvroService tas = rs.asInterface();
            String input = "for test";
            String result = tas.foo(input).toString();

            assertEquals(result, input);
            rs.destroy();
        } finally {
            ns.stop();
        }
    }

    private class TestAvroServiceImpl implements ITestAvroService{
        public TestAvroServiceImpl(Context context){
        }
        @Override
        public CharSequence foo(CharSequence input) throws AvroRemoteException {
            return input;
        }
    }
}
