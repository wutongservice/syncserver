/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

import java.util.Iterator;

/**
 * User: b251
 * Date: 1/11/12
 * Time: 4:58 PM
 * Borqs project
 */
public class MockSyncSource extends ComparableSource {
    private MockDataStore mDataStore;
    public MockSyncSource(String borqsId, MockDataStore dataStore) {
        super(borqsId);
        mDataStore = dataStore;
    }

    @Override
    public SyncItemList getAllItems() throws AccountSyncException {
        return mDataStore.queryItemList();
    }

    @Override
    public boolean addItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mDataStore.insertItem((MockData)item.getData())>0;
    }

    @Override
    public boolean deleteItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mDataStore.deleteItem(item);
    }

    @Override
    public boolean updateItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mDataStore.updateItem(item, (MockData)item.getData());
    }

    @Override
    public boolean updateSyncId(BaseSyncItem item, String syncId) {
        return false;
    }

    public void expect_syncAnchor(long anchor){
        System.setProperty("MockSyncMLSyncSource_Anchor_B", String.valueOf(anchor));
    }

    @Override
    public SyncAnchor getSyncAnchor() {
        return new MockSyncAnchor();
    }

    private static class MockSyncAnchor extends SyncAnchor{
        public MockSyncAnchor(){
            mAnchor = Long.valueOf(System.getProperty("MockSyncMLSyncSource_Anchor_B"));
        }
        @Override
        public boolean commit() throws AccountSyncException {
            System.setProperty("MockSyncMLSyncSource_Anchor_B", String.valueOf(mAnchor));
            return true;
        }
    }

    public static class MockBSyncItem extends BaseSyncItem{
        private long mId;
        private MockData mData;
        private MockDataStore mDataStore;

        public MockBSyncItem(long id, MockData data, MockDataStore store){
            mId = id;
            mData = data;
            mDataStore = store;
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
            return mData.mSyncId;
        }

        @Override
        public void setSyncID(String syncId) {
            mData.mSyncId = syncId;
        }

        @Override
        public IData getData() {
            return mDataStore.queryData(mId);
        }

        @Override
        public boolean isSyncable() {
            return true;
        }
    }

    public static class MockDataStore implements IDataStore {
        private SyncItemList mSet = new SyncItemList();
        private int mMaxID = 0;

        public MockDataStore() {
        }

        public void expectRecordSet(MockData[] datas){
            for(MockData d : datas){
                MockBSyncItem r = new MockBSyncItem(mMaxID, d, this);
                mSet.add(r);
                mMaxID ++;
            }
        }

        public IData queryData(long id){
            MockBSyncItem r = findById(id);
            if(r != null){
                return r.mData;
            }
            return null;
        }

        public SyncItemList queryItemList() {
            return mSet;
        }

        public long insertItem(MockData item) {
            MockData data = new MockData(item.mData, item.mSyncId, item.mLastUpdate);
            MockBSyncItem newone = new MockBSyncItem(mMaxID, data, this);
            mSet.add(newone);
            int ret = mMaxID;
            mMaxID ++;
            return ret;
        }

        public boolean deleteItem(BaseSyncItem item) {
            MockBSyncItem r = findById(item.getID());
            if(r != null){
                mSet.remove(r);
                return true;
            }
            return false;
        }

        public boolean updateItem(BaseSyncItem item, final MockData data) {
            MockBSyncItem r = findById(item.getID());
            if(r != null){
                r.mData.mData = data.mData;
                return true;
            }
            return false;
        }

        private MockBSyncItem findById(long id){
            Iterator<BaseSyncItem> it = mSet.iterator();
            while(it.hasNext()){
                MockBSyncItem r = (MockBSyncItem)it.next();
                if(id == Long.valueOf(r.getID())){
                    return r;
                }
            }
            return null;
        }
    }
}
