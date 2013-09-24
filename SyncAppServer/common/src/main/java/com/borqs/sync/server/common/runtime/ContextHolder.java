/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.runtime;

/**
 * Date: 9/16/11
 * Time: 1:19 PM
 */
public class ContextHolder {
    protected Context mContext;

    protected ContextHolder(Context context){
        mContext = context;
    }

    public Context getContext(){
        return mContext;
    }
}
