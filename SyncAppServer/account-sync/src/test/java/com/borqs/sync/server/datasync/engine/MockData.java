/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

/**
 * User: b251
 * Date: 1/12/12
 * Time: 10:35 AM
 * Borqs project
 */

 public class MockData implements IData {
    public String mData;
    public long mLastUpdate;
    public String mSyncId;
    public MockData(String data, String syncId, long lastUpdate){
        mData = data;
        mSyncId = syncId;
        mLastUpdate = lastUpdate;
    }
 }