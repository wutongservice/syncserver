/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.rpc;

import java.net.Socket;

/**
 * Date: 9/26/11
 * Time: 4:42 PM
 */
public class NetUtility {

    public static boolean isPortAvailable(int port){
        try {
            Socket s = new Socket("127.0.0.1", port);
            s.close();
            return false;
        } catch (Exception ex) {
            return true;
        }

    }
}
