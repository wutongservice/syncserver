/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.service.datasync;

import com.borqs.sync.avro.IAccountSyncService;
import com.borqs.sync.server.datasync.service.SocialContactSyncServiceImpl;
import com.borqs.sync.server.common.runtime.Context;
import org.apache.avro.AvroRemoteException;

/**
 * User: b251
 * Date: 1/30/12
 * Time: 11:50 AM
 * Borqs project
 */
public class SocialContactSyncService implements IAccountSyncService{
    private SocialContactSyncServiceImpl mImpl;
    public SocialContactSyncService(Context context){
        mImpl = new SocialContactSyncServiceImpl(context);
    }

    @Override
    public Void syncFromAccount(CharSequence userId, long since, long to) throws AvroRemoteException {
        mImpl.syncFromAccount(userId.toString(), since, to);
        return null;
    }

    @Override
    public Void syncToAccount(CharSequence userId, long since, long to) throws AvroRemoteException {
        mImpl.syncToAccount(userId.toString(), since, to);
        return null;
    }
}
