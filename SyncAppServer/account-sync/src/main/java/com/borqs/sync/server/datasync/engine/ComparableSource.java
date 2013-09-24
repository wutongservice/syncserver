/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

/**
 * User: b251
 * Date: 1/12/12
 * Time: 4:00 PM
 * Borqs project
 */
public abstract class ComparableSource extends BaseSyncSource{
    private SyncItemList mSyncItemList;
    private SyncItemList mPeerSyncItemList;

    public ComparableSource(String userId) {
        super(userId);
    }

    @Override
    public void beginSyncTo(BaseSyncSource target,TimeRange tr) throws AccountSyncException {
        super.beginSyncTo(target, tr);
        mSyncItemList = getAllItems();
        
        if (target != null) {
        	mPeerSyncItemList = target.getAllItems();
        }
    }

    @Override
    public SyncItemList getUpdatedItems() throws AccountSyncException {
        SyncItemList updated = new SyncItemList();
        for(BaseSyncItem item : mSyncItemList){
            String syncId = item.getSyncID();
            long id = item.getID();

            if(isEmpty(syncId)){
                //error, should not have a NULL sync_id in account databases source
            }

            BaseSyncItem itemInPeer = findItemBySyncid(mPeerSyncItemList, syncId);
            //check if the item is in both sides
            if(itemInPeer != null){
                TimeRange tr = getSyncRange();
                //check if it was modified during in this sync time range.(last_anchor, now)
                if(tr.isCover(item.getLastUpdateTime())){
                    updated.add(item);
                }
            }
        }
        return updated;
    }

    @Override
    public SyncItemList getAddedItems() throws AccountSyncException {
        SyncItemList added = new SyncItemList();
        for(BaseSyncItem item : mSyncItemList){
            String syncId = item.getSyncID();
            long id = item.getID();

            if(isEmpty(syncId)){
                //error, should not have a NULL sync_id in account databases source
            }

            BaseSyncItem itemInPeer = findItemBySyncid(mPeerSyncItemList, syncId);
            if(itemInPeer == null){
                added.add(item);
            }
        }
        return added;
    }

	@Override
    public SyncItemList getDeletedItems() throws AccountSyncException {
         SyncItemList deleted = new SyncItemList();
        for(BaseSyncItem item : mPeerSyncItemList){
            String syncId = item.getSyncID();
            long id = item.getID();

            if(isEmpty(syncId)){
                //error, why there is a item not synced from SyncML to account?
            }

            BaseSyncItem localItem = findItemBySyncid(mSyncItemList, syncId);
            if(localItem == null){
                deleted.add(item);
            }
        }
        return deleted;
    }

    @Override
    protected long nextAnchor() {
        // Known issue, if some change happens after of getAllItems(),
        // the change will be synced next time though it is sync-ed
        // to this session.

        long maxLastUpdate = getSyncAnchor().getAnchor();
        for(BaseSyncItem item : mSyncItemList){
            if(item.getLastUpdateTime() > maxLastUpdate){
                maxLastUpdate = item.getLastUpdateTime();
            }
        }
        return maxLastUpdate;
    }

    private static BaseSyncItem findItemBySyncid(SyncItemList list, String syncId){
        for(BaseSyncItem i : list){
            String i_syncId = i.getSyncID();
            if(!isEmpty(i_syncId) && i_syncId.equals(syncId)){
                return i;
            }
        }

        return null;
    }

    private static boolean isEmpty(String str){
        return str==null || str.length()==0;
    }
}
