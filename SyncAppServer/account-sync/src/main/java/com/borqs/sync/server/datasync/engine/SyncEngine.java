/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.DSLog;
import com.borqs.sync.server.datasync.engine.policy.Policy;

import java.util.Date;

/**
 * Date: 1/9/12
 * Time: 2:17 PM
 * Borqs project
 */
public final class SyncEngine {
    private Context mContext;
    private DSLog mLogger;
    private long mTimestampOfsync;

    public SyncEngine(Context context){
        mContext = context;
        mLogger = DSLog.getInstnace(mContext);
    }

    /**
     * one-way sync from source to destination, any change on source against to destination is to
     * apply to destination, the result is destination is superset of source.
     * @param source    : data from
     * @param destination          : data to
     * @param timestamp : timestamp for the change during sync, on deletion, adding, or update
     * @return  successful or not
     * @throws AccountSyncException
     */
    public boolean sync(BaseSyncSource source, BaseSyncSource destination, Policy policy, long timestamp) throws AccountSyncException{
        mLogger.info("Begin sync for : " + source.getUserId());
        try{
            Policy conflictPolicy = policy==null?generateConflictPolicy():policy;
            mTimestampOfsync = timestamp;

            SyncAnchor sourceAnchor = source.getSyncAnchor();
            TimeRange tr = generateSyncRange(sourceAnchor);
            mLogger.info("Sync anchor of source: " + sourceAnchor.getAnchor() + " with since: " + new Date(mTimestampOfsync).toLocaleString());
            source.beginSyncTo(destination, tr);
            mLogger.info("beginSyncTo......");
            if(!oneWaySync(source, destination, conflictPolicy)){
                mLogger.info("oneWaySync is false......");
                source.endSync(false);
                return false;
            }
            source.endSync(true);
        }finally {
            mLogger.info("End sync for : " + source.getUserId());
        }

        return true;
    }

    private boolean oneWaySync(final BaseSyncSource from, final BaseSyncSource to,Policy policy)
         throws AccountSyncException{
        //sync addition
        SyncItemList additions = from.getAddedItems();
        mLogger.info("Addition count : " + additions.size());
        for(BaseSyncItem item : additions){
            if(!item.isSyncable()){
                continue;
            }
            if(isConflict(item, to, ChangeType.ADD)){
                handleAdditionWithConflict(from, to, item, policy);
            }
            
            syncAddedItem(item, from, to);
        }

        //sync deletion
        SyncItemList deletion = from.getDeletedItems();
        mLogger.info("Deletion count : " + deletion.size());
        for(BaseSyncItem item : deletion){
            if(!item.isSyncable()){
                continue;
            }
            if(isConflict(item, to, ChangeType.DELETION)){
                handleDeletionWithConflict(from, to, item, policy);
            }
            syncDeletedItem(item, from, to);
        }

        //sync update
        SyncItemList update = from.getUpdatedItems();
        mLogger.info("Update count : " + update.size());
        for(BaseSyncItem item : update){
            if(!item.isSyncable()){
                continue;
            }
            if(isConflict(item, to, ChangeType.UPDATE)){
                handleUpdateWithConflict(from, to, item, policy);
            }
            syncUpdatedItem(item, from, to);
        }
        return  true;
    }

    private void handleUpdateWithConflict(BaseSyncSource from, BaseSyncSource to, BaseSyncItem item, Policy policy) throws AccountSyncException {
        boolean isConflictInAccount = to instanceof ComparableSource;
        if(policy.forceUpdate() && isConflictInAccount){
            syncUpdatedItem(item, from, to);
        }

    }

    private void handleDeletionWithConflict(BaseSyncSource from, BaseSyncSource to, BaseSyncItem item, Policy policy) throws AccountSyncException {
        syncDeletedItem(item, from, to);
    }

    private void handleAdditionWithConflict(BaseSyncSource from, BaseSyncSource to, BaseSyncItem item, Policy policy) throws AccountSyncException {
        syncAddedItem(item, from, to);
    }

    private boolean isConflict(BaseSyncItem item, BaseSyncSource compareWith, ChangeType type) {
        return false;
    }

    private void syncAddedItem(BaseSyncItem item, BaseSyncSource from, BaseSyncSource to) throws AccountSyncException {
        if(item.isSyncable()){
            to.addItem(item, mTimestampOfsync);
        }else{
            mLogger.info("do not add a unsyncable item: " + item.getID());
        }
    }

    private void syncUpdatedItem(BaseSyncItem item, BaseSyncSource from, BaseSyncSource to) throws AccountSyncException {
        if(item.isSyncable()){
            to.updateItem(item, mTimestampOfsync);
        }else{
            mLogger.info("do not update a unsyncable item: " + item.getID());
        }
    }

    private void syncDeletedItem(BaseSyncItem item, BaseSyncSource from, BaseSyncSource to) throws AccountSyncException {
        if(item.isSyncable()){
            to.deleteItem(item, mTimestampOfsync);
        }else{
            mLogger.info("do not delete a unsyncable item: " + item.getID());
        }
    }

    private Policy generateConflictPolicy() {
        return new Policy(){
            @Override
            public boolean forceUpdate() {
                return true;
            }
        };
    }

    private TimeRange generateSyncRange(SyncAnchor lastAnchor) {
    	long begin = lastAnchor.getAnchor();
    	long end = begin == 0 ? Long.MAX_VALUE : System.currentTimeMillis();
        return new TimeRange(begin, end);
    }
}
