/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine.syncML;

import com.borqs.sync.server.common.providers.CursorResultHandler;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.IData;
import com.borqs.sync.server.datasync.engine.IDataStore;

/**
 * User: b251
 * Date: 1/9/12
 * Time: 4:53 PM
 * Borqs project
 */
public abstract class SyncMLDataStore implements IDataStore {
    /**
     * delete a item from SyncML database
     * @param item  - item to be deleted
     * @return
     */
    public abstract boolean deleteItem(BaseSyncItem item, long timestamp);

    /**
     * updatea a item to SyncML database
     * @param item - item to be modified
     * @param data - new data
     * @return
     */
    public abstract boolean updateItem(BaseSyncItem item, IData data, long timestamp);

    /**
     * query the all sync item in SyncML database, reflect them by the handler
     * @param handler - handler to receive the result
     * @return
     */
    public abstract boolean queryItemList(String borqsId, CursorResultHandler handler);

    /**
     * insert the new data item into SyncML database
     * @param data - data item
     * @return the source id for the new item
     */
    public abstract long insertItem(BaseSyncItem item, IData data, long timestamp);

    /**
     * update the sync ID for a sync item
     * @param id - SyncML database id
     * @param syncId   - sync id
     * @return
     */
    public abstract boolean updateSyncId(long id, String syncId);
}
