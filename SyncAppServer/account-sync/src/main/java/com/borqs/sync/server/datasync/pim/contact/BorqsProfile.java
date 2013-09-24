package com.borqs.sync.server.datasync.pim.contact;

import com.borqs.sync.server.common.account.ProfileRecord;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.util.Utility;
import com.borqs.sync.server.datasync.engine.AccountSyncException;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.IData;

public class BorqsProfile extends BaseSyncItem {
	private long mLastUpdate;
	private long mId;
	private String mSyncId;
	private BorqsContactSource mSource;
	
	public BorqsProfile(long lastUpdate, long id, String syncId, BorqsContactSource source) {
		mLastUpdate = lastUpdate;
		mId = id;
		mSyncId = syncId;
		mSource = source;
	}
	
	/**
     * get the last update time of this sync item
     * @return
     */
	@Override
    public long getLastUpdateTime() {
    	return mLastUpdate;
    }

    /**
     * get the id, unique in this source
     * @return
     */
    @Override
    public long getID() {
    	return mId;
    }

    /**
     * get the sync id, unique in global in this data type
     * @return
     */
    @Override
    public String getSyncID() {
    	return mSyncId;
    }

    /**
     * set a sync id
     * @param syncId
     */
    @Override
    public void setSyncID(String syncId) {
    	mSyncId = syncId;
    }

    /**
     * get the sync content associated with this sync item
     * @return
     */
    @Override
    public IData getData() throws AccountSyncException {
		String syncId = getSyncID();
		if (Utility.isEmpty(syncId)) {
			return null;
		}

        BorqsContactStore store = (BorqsContactStore)mSource.getDataStore();
        try{
            ProfileRecord record = store.queryCompletedItem(syncId);
		    return new SyncContactData(record.asContact());
        } catch (DataAccessError e){
            throw new AccountSyncException(e);
        }
    }

	@Override
	public boolean isSyncable() {
		return true;
	}
}
