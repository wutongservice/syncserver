/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

/**
 * Date: 1/9/12
 * Time: 2:18 PM
 * Borqs project
 */
public abstract class BaseSyncItem {

	/**
     * get the last update time of this sync item
     * @return
     */
    public abstract long getLastUpdateTime();

    /**
     * get the id, unique in this source
     * @return
     */
    public abstract long getID();

    /**
     * get the sync id, unique in global in this data type
     * @return
     */
    public abstract String getSyncID();

    /**
     * set a sync id
     * @param syncId
     */
    public abstract void setSyncID(String syncId);

    /**
     * get the sync content associated with this sync item
     * @return
     */
    public abstract IData getData() throws AccountSyncException;

    /**
     * check if this item is syncable or not
     * @return
     */
    public abstract boolean isSyncable();
}
