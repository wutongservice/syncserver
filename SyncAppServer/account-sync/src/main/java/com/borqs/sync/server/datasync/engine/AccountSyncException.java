/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

/**
 * User: b251
 * Date: 1/9/12
 * Time: 3:45 PM
 * Borqs project
 */
public class AccountSyncException extends Exception{
    public AccountSyncException() {
    }

    public AccountSyncException(String message) {
        super(message);
    }

    public AccountSyncException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountSyncException(Throwable cause) {
        super(cause);
    }
}
