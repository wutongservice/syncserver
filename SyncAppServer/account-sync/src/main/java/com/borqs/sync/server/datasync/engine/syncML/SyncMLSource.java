/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine.syncML;

import com.borqs.sync.server.common.providers.CursorResultHandler;
import com.borqs.sync.server.datasync.engine.AccountSyncException;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.BaseSyncSource;
import com.borqs.sync.server.datasync.engine.SyncItemList;

import java.sql.ResultSet;
import java.util.List;

/**
 * User: b251
 * Date: 1/9/12
 * Time: 2:20 PM
 * Borqs project
 */
public abstract class SyncMLSource extends BaseSyncSource {
    private SyncMLDataStore mDataStore;

    public SyncMLSource(String borqsId, SyncMLDataStore dataStore){
        super(borqsId);
        mDataStore = dataStore;
    }

    @Override
    public SyncItemList getAllItems() throws AccountSyncException {
        final SyncItemList syncItemList = new SyncItemList();
        boolean result = mDataStore.queryItemList(getUserId(), new CursorResultHandler(){
            @Override
            public void onResult(ResultSet items) {
				syncItemList.addAll(parseResult(items));
            }
        });

        if(!result){
            return SyncItemList.EMPTY;
        }

        return syncItemList;
    }

    @Override
    public boolean addItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mDataStore.insertItem(item, item.getData(), timestamp)>0;

    }

    @Override
    public boolean deleteItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mDataStore.deleteItem(item, timestamp);
    }

    @Override
    public boolean updateItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mDataStore.updateItem(item, item.getData(), timestamp);
    }

    @Override
    public boolean updateSyncId(BaseSyncItem item, String syncId){
        return mDataStore.updateSyncId(item.getID(), syncId);
    }

    protected SyncMLDataStore getDataStore(){
        return mDataStore;
    }

    /**
     * parse the SQL ResultSet to a sync item
     * @param result
     * @return
     */
    protected abstract List<BaseSyncItem> parseResult(ResultSet result);
}
