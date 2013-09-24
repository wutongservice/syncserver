/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.base;

/**
 * Date: 9/19/11
 * Time: 5:05 PM
 */
public class RPCException extends Exception {
    private Exception mRawException;
    public RPCException(String msg){
        this(msg, null);
    }

    public RPCException(String msg, Exception e){
        super(msg,e);
    }
}
