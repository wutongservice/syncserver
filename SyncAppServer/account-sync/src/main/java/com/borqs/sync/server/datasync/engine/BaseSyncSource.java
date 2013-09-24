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
public abstract class BaseSyncSource {
    private TimeRange mSyncRange;
    private String mUserId;
    
    public BaseSyncSource(String userId){
        mUserId = userId;
    }

    /**
     * called before the sync, souce can do some initialization by override it
     * @param target  - the target source would sync to of change in this source
     * @param tr - time range of sync in
     */
    public void beginSyncTo(BaseSyncSource target, TimeRange tr) throws AccountSyncException {
        mSyncRange = tr;
    }

    /**
     * called after sync, is to save the anchor of this source
     * @param result
     */
    public void endSync(boolean result) throws AccountSyncException {
        if(result == true){
            SyncAnchor anchor = getSyncAnchor();
            anchor.setAnchor(nextAnchor());
            anchor.commit();
        }
    }

    /**
     * get the associated user id
     * @return
     */
    public String getUserId(){
        return mUserId;
    }

    protected TimeRange getSyncRange(){
        return mSyncRange;
    }

    /**
     * fetch all the sync items in this source without condition.
     * @return
     * @throws AccountSyncException
     */
    public abstract SyncItemList getAllItems() throws AccountSyncException;

    /**
     * add a new item to this source
     * @param item - sync item, should be instanced by target source
     * @return
     * @throws AccountSyncException
     */
    public abstract boolean addItem(BaseSyncItem item, long timestamp) throws AccountSyncException;

    /**
     * delete a item from the source by the id in "item"
     * @param item - item to be deleted
     * @return
     * @throws AccountSyncException
     */
    public abstract boolean deleteItem(BaseSyncItem item, long timestamp) throws AccountSyncException;

    /**
     * update the data to this source
     * @param item - sync item have the updated values
     * @return
     * @throws AccountSyncException
     */
    public abstract boolean updateItem(BaseSyncItem item, long timestamp) throws AccountSyncException;

    /**
     * get the changed items since last sync
     * @return
     * @throws AccountSyncException
     */
    public abstract SyncItemList getUpdatedItems() throws AccountSyncException;

    /**
     * get the new items since last sync
     * @return
     * @throws AccountSyncException
     */
    public abstract SyncItemList getAddedItems() throws AccountSyncException;

    /**
     * get the deleted items since last sync
     * @return
     * @throws AccountSyncException
     */
    public abstract SyncItemList getDeletedItems() throws AccountSyncException;

    /**
     * update the sync id to this source for the 'item'
     * @param item  - item to be updated
     * @param syncId - assigned sync id
     * @return
     */
    public abstract boolean updateSyncId(BaseSyncItem item, String syncId);

    /**
     * get the sync anchor associated with this source
     * @return
     */
    public abstract SyncAnchor getSyncAnchor();

    /**
     * next sync anchor basing the current syncing, it is to be
     * saved for next sync
     * @return
     */
    protected abstract long nextAnchor();
}
