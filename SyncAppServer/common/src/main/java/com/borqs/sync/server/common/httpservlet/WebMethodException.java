/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.httpservlet;

/**
 * User: b251
 * Date: 12/28/11
 * Time: 2:19 PM
 * Borqs project
 */
public class WebMethodException extends RuntimeException{
    public static WebMethodException from(String format, String... args){
        return new WebMethodException(String.format(format, args));
    }

    public WebMethodException(Exception e){
        super(e);
    }

    private WebMethodException(String msg){
        super(msg);
    }
}
