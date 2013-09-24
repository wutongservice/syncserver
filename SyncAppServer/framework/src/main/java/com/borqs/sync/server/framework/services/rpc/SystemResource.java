/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.framework.services.rpc;

import com.borqs.sync.server.common.runtime.Context;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Date: 9/19/11
 * Time: 6:15 PM
 */
public final class SystemResource {
    private static final int MAX_PORT = 10000;
    private static SystemResource sResource;
    private List<Integer> mUsedPort = Collections.synchronizedList(new LinkedList<Integer>());
    private int mLastPort = -1;

    public static void init(Context context){
        sResource = new SystemResource(context.getConfig().getNamingPort()+1);
    }

    public SystemResource(int rpcBasePort){
        mLastPort = rpcBasePort;
    }

    public static int acquirePort(){
        return sResource.nextPort();
    }

    public static String getHost(){
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                if(ni.isLoopback()){
                    continue;
                }
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ia = ips.nextElement();
                    if(ia instanceof Inet4Address){
                        return ia.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    private int nextPort(){
        synchronized (sResource){
            int nextPort = mLastPort;
            while( !isPortAvailable(nextPort) ){
                mLastPort ++;
                if(mLastPort >= MAX_PORT){
                    mLastPort = 9100;
                }
                nextPort = mLastPort;
            }

            mLastPort++;
            mUsedPort.add(nextPort);

            return nextPort;
        }
    }

    private boolean isPortAvailable(int port){
        return !mUsedPort.contains(port) && NetUtility.isPortAvailable(port);
    }
}
