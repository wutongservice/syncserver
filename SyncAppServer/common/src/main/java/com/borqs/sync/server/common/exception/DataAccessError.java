/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.exception;

/**
 * Date: 6/5/12
 * Time: 12:50 PM
 * Borqs project
 */
public class DataAccessError extends Exception {
    public DataAccessError() {
    }

    public DataAccessError(String message) {
        super(message);
    }

    public DataAccessError(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessError(Throwable cause) {
        super(cause);
    }
}
