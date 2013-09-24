/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.pim.contact;

import com.borqs.sync.server.common.account.ProfileRecord;
import com.borqs.sync.server.common.account.ProfileRecordList;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.engine.AccountSyncException;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.ComparableSource;
import com.borqs.sync.server.datasync.engine.SyncItemList;
import com.borqs.sync.server.datasync.engine.IData;
import com.borqs.sync.server.datasync.engine.SyncAnchor;
import org.apache.commons.lang.math.NumberUtils;

/**
 * User: b251
 * Date: 1/30/12
 * Time: 12:03 PM
 * Borqs project
 */
public class BorqsContactSource extends ComparableSource {
    private Context mContext;
    private BorqsContactStore mAccountDataStore;

	public BorqsContactSource(String borqsId, Context context) {
        super(borqsId);
        mAccountDataStore = new BorqsContactStore(borqsId,context);
        mContext = context;
    }


    @Override
    public SyncItemList getAllItems() throws AccountSyncException {
        ProfileRecordList result = null;
        try {
            result = mAccountDataStore.queryItemList();
        } catch (DataAccessError e) {
            throw new AccountSyncException(e);
        }

        SyncItemList syncItemList = new SyncItemList();
        for(ProfileRecord r :result){
            BaseSyncItem item = parseRecord(r);

            if (item != null) {
                syncItemList.add(item);
            }
        }
        return syncItemList;
    }

    @Override
    public boolean addItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        long id = mAccountDataStore.insertItem(composeRecord(item,  item.getData()), timestamp);
        if( id>0 ){
            item.setSyncID(String.valueOf(id));
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSyncId(BaseSyncItem item, String syncId){
        return true;
    }

    @Override
    public boolean deleteItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mAccountDataStore.deleteItem(item, timestamp);
    }

    @Override
    public boolean updateItem(BaseSyncItem item, long timestamp) throws AccountSyncException {
        return mAccountDataStore.updateItem(item, composeRecord(item,  item.getData()), timestamp);
    }

    public BorqsContactStore getDataStore(){
        return mAccountDataStore;
    }

    public Context getContext(){
        return mContext;
    }
    /**
     * parse the Account recode to a sync item
     * @param record - data from Account
     * @return
     */
    protected BaseSyncItem parseRecord(ProfileRecord record) {
    	if (record == null) {
    		return null;
    	}

    	// get the item's borqsId as syncId
    	String syncId = record.asContact().getBorqsId();
    	
    	// basic_updated_time, profile_updated_time, contact_info_updated_time, address_updated_time
    	long basicUpdated = record.getBasicInfoLastUpdateTime();
    	long profileUpdated = record.getProfileLastUpdateTime();
    	long contactUpdated = record.getContactInfoLastUpdateTime();
    	long addressUpdated = record.getAddressLastUpdateTime();
    	
    	long lastUpdate = NumberUtils.max(
    			new long[] {
    					basicUpdated, profileUpdated, contactUpdated, addressUpdated
    			});
    	
    	return new BorqsProfile(lastUpdate, -1, syncId, this);
    }

    /**
     * compose a internal data structure into Account record
     * @param item  - base info for the sync item
     * @param data - sync data
     * @return
     */
    protected ProfileRecord composeRecord(BaseSyncItem item, IData data) {
        return null;  //TODO
    }

    /**
     * get the sync anchor associated with this source
     * @return
     */
    @Override
    public SyncAnchor getSyncAnchor() {
        BorqsContactStore store = (BorqsContactStore)getDataStore();
    	long anchor = store.querySyncAnchorForUser(getUserId());
        return new ContactAnchor(anchor);
    }
    
    private class ContactAnchor extends SyncAnchor {
    	public ContactAnchor(long anchor) {
    		mAnchor = anchor;
    	}
    	
		@Override
		public boolean commit() throws AccountSyncException {
            BorqsContactStore store = (BorqsContactStore)getDataStore();
            return store.updateSyncAnchorForUser(getUserId(), mAnchor);
		}
    }
}


