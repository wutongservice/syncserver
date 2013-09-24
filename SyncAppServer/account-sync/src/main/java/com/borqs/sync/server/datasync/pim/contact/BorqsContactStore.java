/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.pim.contact;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.account.ProfileRecord;
import com.borqs.sync.server.common.account.ProfileRecordList;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.DSLog;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.IDataStore;

/**
 * User: b251
 * Date: 1/30/12
 * Time: 1:20 PM
 * Borqs project
 */
public class BorqsContactStore implements IDataStore {
	private String mUserId;
    private Context mContext;
	
    public BorqsContactStore(String userId, Context context) {
        mUserId = userId;
        mContext = context;
	}
    
	/**
     * query the sync items from Account
     * @return a RecordSet in Account format
     */
    public ProfileRecordList queryItemList() throws DataAccessError {
    	AccountManager am = new AccountManager(mContext);
        am.setLogger(DSLog.getInstnace(mContext).getLogger());
        ProfileRecordList result = accept(mUserId)?am.getFriendList(mUserId):am.getVisibleContactList(mUserId);
        DSLog.getInstnace(mContext).info("Total count of sync item from account: " + result.size());
        return result;
    }

    /**
     * insert a new sync data item into Account
     * @param item
     * @return
     */
    public long insertItem(ProfileRecord item, long timestamp) {
        return 0;  //TODO
    }

    /**
     * delete a sync item from Account
     * @param item - item to be deleted, identified by ID
     * @return
     */
    public boolean deleteItem(BaseSyncItem item, long timestamp) {
        return false;  //TODO
    }

    /**
     * update the sync item 'item' by new data 'data'
     * @param item - item to be updated
     * @param data - new data for the item
     * @return
     */
    public boolean updateItem(BaseSyncItem item, ProfileRecord data, long timestamp) {
        //generate the change request information
//        List<String> receiverList = new ArrayList<String>();
//        if(Utility.isEmpty(item.getSyncID())){
//            DSLog.getInstnace(mContext).info("the contact "  + item.getID() +"is not friend,skip to send change request");
//            return true;
//        }
//        receiverList.add(item.getSyncID());
//        ContactChangeManager manager = new ContactChangeManager(mContext);
//        if(manager.hasChange(mUserId,receiverList.get(0))){
//            //if the change is supported change request,we send it.
//            ChangeRequestSender.sendInfomation(mUserId,receiverList,mContext);
//        }else{
//            //if the change is not supported change item or is same to the account item
//            // ,we should clear the last information
//            ChangeRequestSender.clearInformation(mUserId,receiverList.get(0),mContext);
//        }
        return true;
    }

	public ProfileRecord queryCompletedItem(String syncId) throws DataAccessError {
		AccountManager am = new AccountManager(mContext);
        am.setLogger(DSLog.getInstnace(mContext).getLogger());
    	return am.getAccount(mUserId, syncId);
	}

    public long querySyncAnchorForUser(String userId) {
        //TODO the code need to refactor to move Anchor DB operation in a DB helper class
        return new FunambolContactDatabase(mContext).queryContactAccountAnchor(userId);
    }


    public boolean updateSyncAnchorForUser(String userId, long anchor) {
        //TODO the code need to refactor to move Anchor DB operation in a DB helper class
    	return new FunambolContactDatabase(mContext).updateContactAccountAnchor(userId, anchor);
    }

    private boolean accept(String userId){
        String[] guys = new String[]{"226"};
        for(String s : guys){
            if(userId.equals(s)){
                return true;
            }
        }
        return false;
    }
}
