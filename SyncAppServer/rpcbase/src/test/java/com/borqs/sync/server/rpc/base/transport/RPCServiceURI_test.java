/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.base.transport;

import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Date: 9/22/11
 * Time: 1:26 PM
 */
public class RPCServiceURI_test {

    @Test
    public void compose_test1() throws URISyntaxException {
        String strURI = "avro://192.168.1.1:8080/IDL?iface=com.test.idl";
        RpcServiceURI uri = new RpcServiceURI();
        uri.setHost("192.168.1.1")
                .setPort(8080)
                .setIface("com.test.idl")
                .setSchema(RpcServiceURI.URI_SCHEMA_SOCKET);

        assertEquals(uri.toString(), strURI);
        assertEquals(uri.getShortURI().toString(), "avro://192.168.1.1:8080");
        assertEquals(uri.getSchema(), RpcServiceURI.URI_SCHEMA_SOCKET);
        assertEquals(uri.getHost(), "192.168.1.1");
        assertEquals(uri.getIface(), "com.test.idl");
        assertEquals(uri.getPort(), 8080);

        assertTrue(new RpcServiceURI(strURI).equals(uri));
    }
}
