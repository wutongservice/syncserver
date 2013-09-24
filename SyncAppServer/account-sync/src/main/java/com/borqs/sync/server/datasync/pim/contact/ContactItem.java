/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.pim.contact;

import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.IData;

/**
 * User: b251
 * Date: 2/17/12
 * Time: 3:30 PM
 * Borqs project
 */
public class ContactItem extends BaseSyncItem {
    private long mLastUpdateTime;
    private long mID;
    private String mSyncId;
    private String mStatus;

    public ContactItem(long id, long lastUpdatTime, String syncId, String status,FunambolContactDatabase store){
    	mLastUpdateTime = lastUpdatTime;
    	mID = id;
    	mSyncId = syncId;
        mStatus = status;
    }

    /**
     * get the last update time of this sync item
     *
     * @return
     */
    @Override
    public long getLastUpdateTime() {
        return mLastUpdateTime;
    }

    /**
     * get the id, unique in this source
     *
     * @return
     */
    @Override
    public long getID() {
        return mID;
    }

    /**
     * get the sync id, unique in global in this data type
     *
     * @return
     */
    @Override
    public String getSyncID() {
        return mSyncId;
    }

    /**
     * set a sync id
     *
     * @param syncId
     */
    @Override
    public void setSyncID(String syncId) {
        mSyncId = syncId;
    }

    /**
     * get the sync content associated with this sync item
     *
     * @return
     */
    @Override
    public IData getData() {
        return null;  //TODO
    }

    /**
     * check if this item is syncable or not
     *
     * @return
     */
    @Override
    public boolean isSyncable() {
        return true;
    }
    
    public String getStatus(){
        return mStatus;
    }
}
