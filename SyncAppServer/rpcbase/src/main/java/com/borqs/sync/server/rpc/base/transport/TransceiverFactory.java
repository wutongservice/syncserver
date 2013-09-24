/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.base.transport;

import org.apache.avro.ipc.HttpTransceiver;
import org.apache.avro.ipc.Ipc;
import org.apache.avro.ipc.SaslSocketTransceiver;
import org.apache.avro.ipc.Transceiver;

import java.io.IOException;
import java.net.*;

/**
 * Date: 9/16/11
 * Time: 4:27 PM
 */
public class TransceiverFactory {

    public static Transceiver getHttpTransceiver(URL url){
        return new HttpTransceiver(url);
    }

    public static Transceiver getSocketTransceiver(URI socketUri) throws IOException {
        return new SaslSocketTransceiver(
                new InetSocketAddress(socketUri.getHost(), socketUri.getPort()));
    }

    //http schema: http://127.0.0.1:1000
    //socket schema: avro://127.0.0.1:8000
    public static Transceiver createTransceiver(URI serverUri) throws IOException {
        return Ipc.createTransceiver(serverUri);
    }

    public static void destroyTransceiver(Transceiver transceiver){
        if(transceiver != null){
            try {
                transceiver.close();
            } catch (IOException e) {}
        }
    }
}


