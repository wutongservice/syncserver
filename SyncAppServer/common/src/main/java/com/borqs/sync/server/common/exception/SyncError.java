/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.exception;

/**
 * User: b251
 * Date: 12/28/11
 * Time: 4:48 PM
 * Borqs project
 */
public class SyncError extends Error{
    public int errno = -1;

    public SyncError(String message){
        super(message);
    }

    public SyncError(Throwable cause){
        super(cause);
    }

    public SyncError(String message, Throwable cause){
        super(message, cause);
    }

    public SyncError(int errno, String message){
        super(message);
        this.errno = errno;
    }
}
