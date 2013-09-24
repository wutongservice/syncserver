/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.pim.contact;

import com.borqs.sync.server.common.providers.*;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.Utility;
import com.borqs.sync.server.datasync.DSLog;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.engine.IData;
import com.borqs.sync.server.datasync.engine.syncML.SyncMLDataStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * User: b251
 * Date: 1/30/12
 * Time: 1:24 PM
 * Borqs project
 */
public class FunambolContactDatabase extends SyncMLDataStore {

    private Context mContext;
    private ContactProvider mContactProvider;
    private ContactMerge mContactMerge;
    private Logger mLogger;

	public FunambolContactDatabase(Context context){
    	mContext = context;
        mLogger = DSLog.getInstnace(mContext).getLogger();
        mContactProvider = new ContactProvider(mContext);
        mContactProvider.useLogger(mLogger);
        mContactMerge = new ContactMerge(mContext);
        mContactMerge.setLogger(mLogger);
    }
	
	public long queryContactAccountAnchor(String borqsId) {
		long anchor = 0;
		
		Connection conn = mContext.getSqlConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("select anchor from borqs_contact_account_anchor where borqsid=? order by anchor desc");
			ps.setString(1, borqsId);
			rs = ps.executeQuery();
			
			if (rs.first()) {
				anchor = rs.getLong("anchor");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.close(conn, ps, rs);
		}
		
    	return anchor;
    }
	
	public boolean updateContactAccountAnchor(String borqsId, long anchor) {
		if (Utility.isEmpty(borqsId)) {
			return false;
		}
		
		Connection conn = mContext.getSqlConnection();
		PreparedStatement ps = null;
		
		try {
            conn.setAutoCommit(false);
			ps = conn.prepareStatement("delete from borqs_contact_account_anchor where borqsid=?");
			ps.setString(1, borqsId);
			ps.executeUpdate();
			DBUtility.close(null, ps, null);
			
			ps = conn.prepareStatement("insert into borqs_contact_account_anchor values (?,?)");
			ps.setString(1, borqsId);
			ps.setLong(2, anchor);
			
			ps.executeUpdate();
			DBUtility.close(null, ps, null);

            conn.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            mLogger.info("updateContactAccountAnchor error!!!!!!rollback the update." + e.getMessage());
		} finally {
			DBUtility.close(conn, ps, null);
		}
		
		return false;
	}

    /**
     * delete a item from SyncML database
     *
     * @param item - item to be deleted
     * @return
     */
    @Override
    public boolean deleteItem(BaseSyncItem item, long timestamp) {
    	if (item == null) {
			return false;
		}
    	return mContactProvider.updateItemAsPrivate(item.getID(),timestamp);
//    	return mContactProvider.deleteItemWithTimestamp(item.getID(), timestamp);
    }

    /**
     * updatea a item to SyncML database
     *
     * @param item - item to be modified
     * @param data - new data
     * @return
     */
    @Override
    public boolean updateItem(BaseSyncItem item, IData data, long timestamp) {
    	if (mContext == null) {
			return false;
		}
    	
    	if (data == null) {
    		return false;
    	}
    	
    	if (!(data instanceof SyncContactData)) {
    		return false;
    	}
    	
    	Contact c = ((SyncContactData) data).getContact();
        long original_last_update_time = item.getLastUpdateTime();
    	
    	if (c == null) {
    		return false;
    	}
    	
    	String userId = c.getOwnerId();
    	String borqsId = c.getBorqsId();
    	
    	long contactId = mContactProvider.findContact(userId, borqsId);
        if(contactId != -1){
            //use original time from Account as it's last update time
            c.setLastUpdate(timestamp);
            c = mContactMerge.mergeFromAccount(c,mContactProvider.getItem(String.valueOf(contactId)),timestamp);
            return mContactProvider.updateItem(contactId, c);
        }
    	return false;
    }

    /**
     * query the all sync item in SyncML database, reflect them by the handler
     *
     * @param handler - handler to receive the result
     * @return
     */
    @Override
    public boolean queryItemList(String borqsId, CursorResultHandler handler) {
        return mContactProvider.querySocialContacts(borqsId, handler);
    }

    /**
     * insert the new data item into SyncML database
     *
     * @param data - data item
     * @return the source id for the new item
     */
    @Override
    public long insertItem(BaseSyncItem item, IData data, long timestamp) {
    	if (!(data instanceof SyncContactData)) {
    		return -1;
    	}
    	
    	SyncContactData contactData = (SyncContactData) data;
    	Contact c = contactData.getContact();
        c.setLastUpdate(timestamp);


        Contact sameContact = mContactMerge.getSameContact(c);
        if(sameContact == null){
            //for added item from account to syncserver,we should copy bname to name.
            mContactMerge.fillItemTimeAndPrivate(c, timestamp,false);
            c = copyContactBNameToName(c);
            mLogger.info("no same contact with  " + c.getFirstName() + c.getLastName()  + c.getMiddleName());
            return mContactProvider.insertItem(c);
        }else{
            c = mContactMerge.mergeFromAccount(c,sameContact,timestamp);
            mLogger.info("exist same contact with: " + c.getFirstName() + c.getLastName()  + c.getMiddleName());
            mContactProvider.updateItem(sameContact.getId(),c);
            return sameContact.getId();
        }
    }

    /**
     * update the sync ID for a sync item
     *
     * @param id     - SyncML database id
     * @param syncId - sync id
     * @return
     */
    @Override
    public boolean updateSyncId(long id, String syncId) {
        return false;  //TODO
    }

    private Contact copyContactBNameToName(Contact c){
        c.setFirstName(c.getBFirstName());
        c.setMiddleName(c.getBMiddleName());
        c.setLastName(c.getBLastName());
        return c;
    }
}
