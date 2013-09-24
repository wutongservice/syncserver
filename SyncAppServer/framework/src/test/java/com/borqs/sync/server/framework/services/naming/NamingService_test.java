/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.naming;

import com.borqs.sync.server.framework.MockContext;
import com.borqs.sync.server.framework.services.naming.NamingService;
import com.borqs.sync.server.rpc.base.RPCStatus;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.rpc.base.transport.RpcServiceURI;
import org.apache.avro.AvroRemoteException;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Date: 9/21/11
 * Time: 5:26 PM
 */
public class NamingService_test {
    private static final String TEST_IF1 = "com.borqs.sync.TestService1";
    private static final String TEST_IF2 = "com.borqs.sync.TestService2";
    private static final String TEST_IF3 = "com.borqs.sync.TestService3";
    @Test
    public void test_all() throws AvroRemoteException, URISyntaxException {
        Context context = new MockContext();
        NamingService ns = new NamingService(context);
        NamingService.SimpleNativeNaming snn = ns.new SimpleNativeNaming();

        RpcServiceURI uri1 = new RpcServiceURI();
        uri1.setHost("192.168.1.1")
                .setIface(TEST_IF1)
                .setPort(8080)
                .setSchema(RpcServiceURI.URI_SCHEMA_SOCKET);

        //case 1
        {
        String result = snn.register(uri1.toString(), true).toString();
        RpcServiceURI rURI = new RpcServiceURI(result);
        assertNotNull(rURI.getToken());
        }

        {
        String result = snn.lookup(uri1.getIface()).toString();
        RpcServiceURI rURI = new RpcServiceURI(result);
        assertEquals(rURI.getIface(), uri1.getIface());
        assertEquals(rURI.getHost(), uri1.getHost());
        assertEquals(rURI.getPort(), uri1.getPort());
        assertEquals(rURI.getSchema(), uri1.getSchema());
        }

        //case 2
        {
        RpcServiceURI uri2 = new RpcServiceURI();
        uri1.setHost("192.168.1.1")
                .setIface(TEST_IF1)
                .setPort(8080)
                .setSchema(RpcServiceURI.URI_SCHEMA_SOCKET);
        String result3 = snn.register(uri1.toString(), true).toString();
        RpcServiceURI rURI3 = new RpcServiceURI(result3);
        assertNotNull(rURI3.getToken());
        }

        //case 3
        {
        RpcServiceURI uri2 = new RpcServiceURI();
        uri1.setHost("192.168.1.1")
                .setIface(TEST_IF1)
                .setPort(8080)
                .setSchema(RpcServiceURI.URI_SCHEMA_SOCKET);
        String result3 = snn.register(uri1.toString(), false).toString();
        assertEquals(result3, RPCStatus.RPC_RESPONSE_DUP_SERVICE);

        String result = snn.lookup(TEST_IF1).toString();
        assertNotNull(result);
        }

        //case 4
        {
        RpcServiceURI uri2 = new RpcServiceURI();
        uri1.setHost("192.168.1.1")
                .setIface(TEST_IF2)
                .setPort(8080)
                .setSchema(RpcServiceURI.URI_SCHEMA_SOCKET);
        String result3 = snn.register(uri1.toString(), true).toString();
        RpcServiceURI rURI3 = new RpcServiceURI(result3);
        assertNotNull(rURI3.getToken());

        String result = snn.lookup(TEST_IF1).toString();
        assertNotNull(result);
        result = snn.lookup(TEST_IF2).toString();
        assertNotNull(result);
        }

        //case 4
        {
        RpcServiceURI uri2 = new RpcServiceURI();
        uri1.setHost("192.168.1.1")
                .setIface(TEST_IF3)
                .setPort(8080)
                .setSchema(RpcServiceURI.URI_SCHEMA_SOCKET);
        String result3 = snn.register(uri1.toString(), true).toString();
        RpcServiceURI rURI3 = new RpcServiceURI(result3);
        assertNotNull(rURI3.getToken());

        String result = snn.lookup(TEST_IF1).toString();
        assertNotNull(result);
        result = snn.lookup(TEST_IF2).toString();
        assertNotNull(result);
        result = snn.lookup(TEST_IF3).toString();
        assertNotNull(result);
        }

    }
}
