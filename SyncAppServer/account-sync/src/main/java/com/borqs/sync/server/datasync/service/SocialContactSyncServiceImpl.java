/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.service;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.DSLog;
import com.borqs.sync.server.datasync.engine.AccountSyncException;
import com.borqs.sync.server.datasync.engine.SyncEngine;
import com.borqs.sync.server.datasync.engine.policy.Policy;
import com.borqs.sync.server.datasync.pim.contact.BorqsContactSource;
import com.borqs.sync.server.datasync.pim.contact.FunambolContactSource;
import com.borqs.sync.server.datasync.pim.group.GroupSyncHandler;

/**
 * User: b251
 * Date: 1/30/12
 * Time: 11:52 AM
 * Borqs project
 */
public class SocialContactSyncServiceImpl {
    private Context mContext;
    private SyncEngine mSyncEngine;

    public SocialContactSyncServiceImpl(Context context){
        mContext = context;
        mSyncEngine = new SyncEngine(context);
    }

    /**
     * interface function to trigger a sync from Account to SyncML database
     * @param userId - the user to be sync
     * @param syncTimestamp - the time stamp sync 'since' in SyncML server
     * @param to    - the time stamp sync 'to' in SyncML server
     */
    public void syncFromAccount(String userId, long syncTimestamp, long to) {
        BorqsContactSource src = new BorqsContactSource(userId, mContext);
        FunambolContactSource dest = new FunambolContactSource(userId, mContext);

        Policy policy = new Policy() {
            @Override
            public boolean forceUpdate() {
                return true;
            }
        };

        DSLog.getInstnace(mContext).info("syncFromAccount,the since is :" + syncTimestamp);
//        long syncTimestamp = caculateSynctimestamp(since);

        try {
            boolean successful = mSyncEngine.sync(src, dest, policy, syncTimestamp);
            //sync groups info
            if(successful){
                GroupSyncHandler groupHandler = new GroupSyncHandler(mContext);
                groupHandler.refreshContactGroup(userId, syncTimestamp);
            }
        } catch (AccountSyncException e) {
            e.printStackTrace();
        }
    }

    /**
     * interface function to trigger a sync from SyncML database to Account
     * @param userId - the user to be sync
     * @param since - the time stamp sync 'since' in SyncML server
     * @param syncTimestamp    - the time stamp sync 'to' in SyncML server
     */
    public void syncToAccount(String userId, long since, long syncTimestamp) {
        FunambolContactSource src = new FunambolContactSource(userId, mContext);
        //if the operation is from SyncML to Account,we should use the mTimestamOfsync as SyncAnchor(since) so that
        //we can query the changed contct sync from device (do not contain the contacts from Account)
        src.setSyncAnchor(since);
        BorqsContactSource dest = new BorqsContactSource(userId, mContext);

        Policy policy = new Policy() {
            @Override
            public boolean forceUpdate() {
                return true;
            }
        };
//        long syncTimestamp = caculateSynctimestamp(since);
        try {
            boolean result = mSyncEngine.sync(src, dest, policy, since);
        } catch (AccountSyncException e) {
            e.printStackTrace();
        }
    }

}
