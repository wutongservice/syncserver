/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

import java.util.ArrayList;

/**
 * User: b251
 * Date: 1/12/12
 * Time: 5:56 PM
 * Borqs project
 */
public class SimpleSyncSource extends BaseSyncSource {
    private SyncItemList mDatas;

    public SimpleSyncSource() {
        super("userId");
    }

    public void expect_syncAnchor(long anchor){
        mSyncAnchor.setAnchor(anchor);
    }

    public void expect_Items(ArrayList<MockData> datas){
        mDatas = new SyncItemList();
        int index = 0;
        for(MockData data : datas){
            mDatas.add(new MockSimpleSyncItem(data, index, data.mSyncId, ""));
            index ++;
        }
    }

    public void expect_deletedItems(ArrayList<MockData> datas){
        mDatas = new SyncItemList();
        int index = 0;
        for(MockData data : datas){
            mDatas.add(new MockSimpleSyncItem(data, index, data.mSyncId, "D"));
            index ++;
        }
    }


    public void expect_addedItems(ArrayList<MockData> datas){
        mDatas = new SyncItemList();
        int index = 0;
        for(MockData data : datas){
            mDatas.add(new MockSimpleSyncItem(data, index, data.mSyncId, "A"));
            index ++;
        }
    }


    public void expect_updatedItems(ArrayList<MockData> datas){
        mDatas = new SyncItemList();
        int index = 0;
        for(MockData data : datas){
            mDatas.add(new MockSimpleSyncItem(data, index, String.valueOf(index), "U"));
            index ++;
        }
    }

    @Override
    public SyncItemList getAllItems() throws AccountSyncException {
        return mDatas;
    }

    @Override
    public boolean addItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        mDatas.add(item);
        return true;
    }

    @Override
    public boolean deleteItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        for(BaseSyncItem i : mDatas){
            if(i.getSyncID().equals(item.getSyncID())){
                mDatas.remove(i);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean updateItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        for(BaseSyncItem i : mDatas){
            if(i.getSyncID().equals(item.getSyncID())){
                MockData md = (MockData)i.getData();
                MockData data = (MockData)item.getData();
                md.mData = data.mData;
                md.mLastUpdate = data.mLastUpdate;
            }
        }
        return true;
    }

    @Override
    public SyncItemList getUpdatedItems() throws AccountSyncException {
        SyncItemList updated = new SyncItemList();
        for(BaseSyncItem i : mDatas){
            MockSimpleSyncItem mi =(MockSimpleSyncItem)i;
            if("U".equals(mi.mStatus)){
                updated.add(mi);
            }
        }
        return updated;
    }

    @Override
    public SyncItemList getAddedItems() throws AccountSyncException {
        SyncItemList added = new SyncItemList();
        for(BaseSyncItem i : mDatas){
            MockSimpleSyncItem mi =(MockSimpleSyncItem)i;
            if("A".equals(mi.mStatus)){
                added.add(mi);
            }
        }
        return added;
    }

    @Override
    public SyncItemList getDeletedItems() throws AccountSyncException {
        SyncItemList deleted = new SyncItemList();
        for(BaseSyncItem i : mDatas){
            MockSimpleSyncItem mi =(MockSimpleSyncItem)i;
            if("D".equals(mi.mStatus)){
                deleted.add(mi);
            }
        }
        return deleted;
    }

    @Override
    public boolean updateSyncId(BaseSyncItem item, String syncId) {
        MockSimpleSyncItem mi =(MockSimpleSyncItem)item;
        mi.updateSyncId(syncId);
        return true;  //TODO
    }

    @Override
    public SyncAnchor getSyncAnchor() {
        return mSyncAnchor;
    }


    @Override
    protected long nextAnchor() {
        return 0;  //TODO
    }

    private SyncAnchor mSyncAnchor = new SyncAnchor() {
        @Override
        public boolean commit() throws AccountSyncException {
            return true;
        }
    };

    public static class MockSimpleSyncItem extends BaseSyncItem{
        private MockData mData;
        private long mId;
        private String mSyncId;
        private String mStatus;
        public MockSimpleSyncItem(MockData data, long id, String syncId, String status){
            mData = data;
            mId = id;
            mSyncId = syncId;
            mStatus = status;
        }
        public void updateSyncId(String syncId){
            mSyncId = syncId;
        }

        @Override
        public long getLastUpdateTime() {
            return mData.mLastUpdate;
        }

        @Override
        public long getID() {
            return mId;
        }

        @Override
        public String getSyncID() {
            return mSyncId;
        }

        @Override
        public void setSyncID(String syncId) {
            mSyncId = syncId;
        }

        @Override
        public IData getData() {
            return mData;
        }

        @Override
        public boolean isSyncable() {
            return true;
        }
    }
}
