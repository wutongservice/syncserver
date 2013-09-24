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
import org.apache.avro.AvroRemoteException;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Date: 9/22/11
 * Time: 6:06 PM
 */
public class RPCService_test {

    @Test
    public void run_test() throws RPCException, NotFoundException, JSONException, AvroRemoteException {
        Context context = new MockContext();
        SystemResource.init(context);
        //start naming
        NamingService ns = new NamingService(context);
        String namingService = "{service:NamingService, enable:true, priority:0, desc:naming}";
        ns.init(ServiceDescriptor.from(new JSONObject(namingService)));

        ns.start(null);
        assertTrue(ns.isRunning());

        //start rpcservice
        String providerService = "{service:RPCService, enable:true, priority:1, desc:rpc_test_service, interface:com.borqs.sync.server.framework.services.rpc.ITestAvroService, impl:com.borqs.sync.server.framework.services.rpc.RPCService_test$TestAvroServiceImpl, schema:avro}";
        ServiceDescriptor descriptor = ServiceDescriptor.from(new JSONObject(providerService));
        RPCService service = new RPCService(context);
        service.init(descriptor);
        assertTrue(service.isEnabled());
        assertEquals("1", service.getPriority());
        assertEquals("rpc_test_service", service.getDescriptor());

        service.start(null);

        assertEquals(true, service.isRunning());

        NamingServiceProxy nsp = new NamingServiceProxy(context.getConfig().getNamingHost(),
                context.getConfig().getNamingPort());
        RemoteService rs = new RemoteService(ITestAvroService.class, nsp);
        ITestAvroService testService = rs.asInterface();

        assertNotNull(testService);

        assertEquals("input", testService.foo("input").toString());
        assertEquals("input2", testService.foo("input2").toString());
        rs.destroy();
        ns.stop();
     }

    public static class TestAvroServiceImpl implements ITestAvroService{
        public TestAvroServiceImpl(Context context){
        }
        @Override
        public CharSequence foo(CharSequence input) throws AvroRemoteException {
            return input;
        }
    }
}
