/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.pim.group;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.account.Circle;
import com.borqs.sync.server.common.account.CircleList;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.providers.*;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.DSLog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Date: 3/27/12
 * Time: 10:53 AM
 * Borqs project
 */
public final class GroupSyncHandler {
    private AccountManager mAccountManager;
    private ContactProvider mContactProvider;
    private GroupProvider mGroupProvider;
    private DSLog mDSLogger;

    public GroupSyncHandler(Context context){
        mAccountManager = new AccountManager(context);
        mContactProvider = new ContactProvider(context);
        mGroupProvider = new GroupProvider(context);
        mDSLogger = DSLog.getInstnace(context);

        mAccountManager.setLogger(mDSLogger.getLogger());
        mContactProvider.useLogger(mDSLogger.getLogger());
        mGroupProvider.useLogger(mDSLogger.getLogger());
    }

    public void refreshContactGroup(String borqsId, long timestamp) {
        //1.list the B+ contacts
        final HashMap<Long, String> buddys = new HashMap<Long, String>();
        mContactProvider.querySocialContacts(borqsId, new CursorResultHandler() {
            @Override
            public void onResult(ResultSet item) {
                addValidContact(item, buddys);
            }
        });
        mDSLogger.info("BorqsId:" + borqsId + " have B+ contacts " + buddys.size());

        //check if have some buddy from B+
        if(buddys.size() <= 0 ){
            return; //nothing to do to empty
        }

        //2.get circle from B+ for my account
        try{
        CircleList accountCircles = mAccountManager.getCirclesWithBuddy(borqsId);
        //3.compare each B+ contact's group info with B+ circle info
        for(long raw_contact_id : buddys.keySet()){
            String buddy_id = buddys.get(raw_contact_id);
            List<ContactGroup> local_groups = mGroupProvider.getGroupsOfContact(raw_contact_id);
            List<ContactGroup> b_groups = getGroupInfoOfBuddy(buddy_id, accountCircles);
            mDSLogger.info("Contact id = " + raw_contact_id +", borqs_id="+buddy_id);
             
            if(!isSameWith(local_groups, b_groups)){
                if(local_groups.isEmpty()){
                    mDSLogger.info("insert group,the last_update is :" + timestamp);
                    insertGroups(raw_contact_id, b_groups, timestamp);
                }
                mDSLogger.info("Update " + raw_contact_id + "'s group from '" + stringOf(local_groups) +"' to " + stringOf(b_groups));
                mDSLogger.info("update group,the last_update is :" + timestamp);
                updateLocalGroups(raw_contact_id, b_groups, timestamp);
            }
        }
        } catch (DataAccessError e){
            mDSLogger.error("Error : "  + e.getLocalizedMessage());
        } finally {
            mDSLogger.info("End of group sync of " + borqsId);
        }

    }

    private void updateLocalGroups(long raw_contact_id, List<ContactGroup> b_groups, long timestamp) {
        mGroupProvider.updateGroups(raw_contact_id, b_groups, timestamp);
    }

    private void insertGroups(long raw_contact_id, List<ContactGroup> b_groups, long timestamp) {
        mGroupProvider.insertGroups(raw_contact_id, b_groups, timestamp);
    }

    //package level for test
    static List<ContactGroup> getGroupInfoOfBuddy(String buddyId, CircleList accountCircles) {
        List<ContactGroup> result = new ArrayList<ContactGroup>();
        for(Circle c : accountCircles){
            if(!c.isVisibleToUser()){
                continue;
            }
            List<String> members = c.getMemebers();
            if(members.contains(buddyId)){
                result.add(new ContactGroup(c.getName()));
            }
        }
        return result;
    }

    //package level for test
    static boolean isSameWith(List<ContactGroup> local_groups, List<ContactGroup> b_groups) {
        if(local_groups.size() != b_groups.size()){
            return false;
        }

        //no duplicated item in the list
        for(ContactGroup g : local_groups){
            if(!b_groups.contains(g)){
                return false;
            }
        }
        return true;
    }

    private void addValidContact(ResultSet contacts, HashMap<Long, String> result){
        try {
            while(contacts.next()){
                long raw_contact_id = contacts.getLong(Contact.FIELD_ID);
                String status = contacts.getString(Contact.FIELD_STATUS);
                String borqsId = contacts.getString(Contact.FIELD_BORQSID);
                if(!"D".equalsIgnoreCase(status)){
                    result.put(raw_contact_id, borqsId);
                }
            }
        } catch (SQLException e) {
        }
    }
    
    private static String stringOf(List<ContactGroup> lc){
        StringBuffer sbuffer = new StringBuffer();
        for(ContactGroup g : lc){
            if(sbuffer.length()>0){
                sbuffer.append(",");
            }
            sbuffer.append(g.name());
        }
        return sbuffer.toString();
    }
}
