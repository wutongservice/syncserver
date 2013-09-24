/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

/**
 * Date: 1/9/12
 * Time: 2:17 PM
 * Borqs project
 */
public abstract class SyncAnchor {
    protected long mAnchor;
    public long getAnchor(){
        return mAnchor;
    }

    public void setAnchor(long anchor){
        mAnchor = anchor;
    }

    /**
     * save the 'mAnchor' as new anchor
     * @return
     * @throws AccountSyncException
     */
    public abstract boolean commit() throws AccountSyncException;
}
