/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.pim.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.borqs.sync.server.datasync.DSLog;
import com.borqs.sync.server.datasync.engine.syncML.SyncMLSource;
import com.borqs.sync.server.datasync.engine.AccountSyncException;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.SyncAnchor;
import com.borqs.sync.server.datasync.engine.SyncItemList;
import com.borqs.sync.server.common.runtime.Context;

/**
 * User: b251
 * Date: 1/30/12
 * Time: 12:04 PM
 * Borqs project
 */
public class FunambolContactSource extends SyncMLSource {

    private long mSince;
    private Context mContext;

    public FunambolContactSource(String userId, Context context) {
        super(userId, new FunambolContactDatabase(context));
        mContext = context;
    }

    /**
     * parse the SQL ResultSet to a sync item
     *
     * @param result
     * @return
     */
    @Override
    protected List<BaseSyncItem> parseResult(ResultSet result) {
    	if (result == null) {
    		return null;
    	}
    	
    	List<BaseSyncItem> items = new ArrayList<BaseSyncItem>();
    	
    	try {
			while (result.next()) {
				long contactId = result.getLong("id");
				String syncId = result.getString("borqsid");
				long lastUpdate = result.getLong("last_update");
				String status = result.getString("status");
//                DSLog.getInstnace(mContext).info("contactId :" + contactId + "," + "syncId :"
//                        + syncId + "," + "lastUpdate :" + lastUpdate + "," + "status :" + status);
				ContactItem contact = new ContactItem(contactId, lastUpdate, syncId,status,
                        (FunambolContactDatabase)getDataStore());
				items.add(contact);
			}
			
		} catch (SQLException e) {
            DSLog.getInstnace(mContext).error("SqlException for FunambolContactSource.parseResult:" + e.getMessage());
			e.printStackTrace();
		}
    	
        return items;  //TODO
    }

    /**
     * get the changed items since last sync
     *
     * @return
     * @throws com.borqs.sync.server.datasync.engine.AccountSyncException
     *
     */
    @Override
    public SyncItemList getUpdatedItems() throws AccountSyncException {
        //collect the contactItem whose borqsid is not null and status is 'U' ,from mSince to System.currentTimeMillis.
        SyncItemList itemList = getAllItems();
        SyncItemList updateItemList = new SyncItemList();
        DSLog.getInstnace(mContext).info("FunambolContactSource->getUpdatedItems syncToAccount,mSince: " + mSince);
        for(BaseSyncItem baseSyncItem:itemList){
            ContactItem contactItem = (ContactItem)baseSyncItem;
            if(contactItem.getLastUpdateTime() > mSince
                    && "U".equalsIgnoreCase(contactItem.getStatus())){
                DSLog.getInstnace(mContext).info(contactItem.getID() + " is a updated item");
                updateItemList.add(contactItem);
            }
        }
        DSLog.getInstnace(mContext).info("FunambolContactSource->getUpdatedItems syncToAccount,the size :" + updateItemList.size());
        return updateItemList;
    }

    /**
     * get the new items since last sync
     *
     * @return
     * @throws com.borqs.sync.server.datasync.engine.AccountSyncException
     *
     */
    @Override
    public SyncItemList getAddedItems() throws AccountSyncException {
        DSLog.getInstnace(mContext).info("FunambolContactSource->getAddedItems syncToAccount,the size :" + 0);
        return SyncItemList.EMPTY;  //TODO
    }

    /**
     * get the deleted items since last sync
     *
     * @return
     * @throws com.borqs.sync.server.datasync.engine.AccountSyncException
     *
     */
    @Override
    public SyncItemList getDeletedItems() throws AccountSyncException {
        DSLog.getInstnace(mContext).info("FunambolContactSource->getDeletedItems syncToAccount,the size :" + 0);
        return SyncItemList.EMPTY;  //TODO
    }

    /**
     * get the sync anchor associated with this source
     * @return
     */
    @Override
    public SyncAnchor getSyncAnchor() {
        return new ContactAnchor(mSince);
    }

    public void setSyncAnchor(long since){
        mSince = since;
    }



    private class ContactAnchor extends SyncAnchor {
        public ContactAnchor(long anchor) {
            mAnchor = anchor;
        }

        @Override
        public boolean commit() throws AccountSyncException {
           return false;
        }
    }

    /**
     * next sync anchor basing the current syncing, it is to be
     * saved for next sync
     *
     * @return
     */
    @Override
    protected long nextAnchor() {
        return 0;  //TODO
    }
}
