/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.exception;

/**
 * User: b251
 * Date: 12/28/11
 * Time: 4:16 PM
 * Borqs project
 */
public class SyncServerRuntimeException extends RuntimeException{

    public SyncServerRuntimeException(String message){
        super(message);
    }

    public SyncServerRuntimeException(Throwable cause){
        super(cause);
    }

    public SyncServerRuntimeException(String message, Throwable cause){
        super(message, cause);
    }
}
