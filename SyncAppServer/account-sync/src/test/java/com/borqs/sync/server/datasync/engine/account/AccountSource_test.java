/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine.account;

import com.borqs.sync.server.datasync.engine.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: b251
 * Date: 1/29/12
 * Time: 11:00 AM
 * Borqs project
 */
public class AccountSource_test {
    private static final int BASE_TIME = 0;

    @Test
    public void no_change_test() throws AccountSyncException {
        SimpleSyncSource source1 = new SimpleSyncSource();
        MockData a_d1 = new MockData("d1", "1", BASE_TIME+105);
        MockData a_d2 = new MockData("d2", "2", BASE_TIME+100);
        ArrayList<MockData> datas = new ArrayList<MockData>();
        datas.add(a_d1);
        datas.add(a_d2);
        source1.expect_Items(datas);
        source1.expect_syncAnchor(BASE_TIME+105);

        MockSyncSource.MockDataStore dataStore2 = new MockSyncSource.MockDataStore();
        MockData b_d1 = new MockData("d1", "1", BASE_TIME+105);
        MockData b_d2 = new MockData("d2", "2", BASE_TIME+100);
        dataStore2.expectRecordSet(new MockData[]{b_d1, b_d2});
        MockSyncSource source2 = new MockSyncSource("borqsId",  dataStore2);
        source2.expect_syncAnchor(BASE_TIME+105);

        TimeRange tr = new TimeRange(source2.getSyncAnchor().getAnchor(), BASE_TIME+1000);
        source2.beginSyncTo(source1, tr);
        SyncItemList updated = source2.getUpdatedItems();
        assertEquals(0, updated.size());

        SyncItemList added = source2.getAddedItems();
        assertEquals(0, added.size());

        SyncItemList deleted = source2.getDeletedItems();
        assertEquals(0, deleted.size());
    }

    @Test
    public void modify_test() throws AccountSyncException {
        SimpleSyncSource source1 = new SimpleSyncSource();
        MockData a_d1 = new MockData("d1", "1", BASE_TIME+80);
        MockData a_d2 = new MockData("d2", "2", BASE_TIME+90);
        MockData a_d3 = new MockData("d3", "3", BASE_TIME+99);
        ArrayList<MockData> datas = new ArrayList<MockData>();
        datas.add(a_d1);
        datas.add(a_d2);
        datas.add(a_d3);
        source1.expect_Items(datas);
        source1.expect_syncAnchor(BASE_TIME+99);

        MockSyncSource.MockDataStore dataStore2 = new MockSyncSource.MockDataStore();
        MockData b_d1 = new MockData("d1-2", "1", BASE_TIME+80);   //no changed
        MockData b_d2 = new MockData("d2-2", "2", BASE_TIME+100);  //updated
//        MockData a_d3 = new MockData("d3", "2", baseTime+99)    //deleted
        MockData b_d4 = new MockData("d4", "4", BASE_TIME+100);    //added
        MockData b_d5 = new MockData("d5", "5", BASE_TIME+101);    //added
        dataStore2.expectRecordSet(new MockData[]{b_d1, b_d2, b_d4, b_d5});
        MockSyncSource source2 = new MockSyncSource("borqsId",  dataStore2);
        source2.expect_syncAnchor(BASE_TIME+99);

        TimeRange tr = new TimeRange(source2.getSyncAnchor().getAnchor(), BASE_TIME+1000);
        source2.beginSyncTo(source1, tr);

        //test updated
        SyncItemList updated = source2.getUpdatedItems();
        assertEquals(1, updated.size());
        MockData md1 = (MockData)updated.get(0).getData();
        assertEquals("d2-2", md1.mData);
        assertEquals("2", md1.mSyncId);

        //test added
        SyncItemList added = source2.getAddedItems();
        assertEquals(2, added.size());
        for(BaseSyncItem i : added){
            assertTrue("4".equals(i.getSyncID()) || "5".equals(i.getSyncID()));
        }

        //test deleted
        SyncItemList deleted = source2.getDeletedItems();
        assertEquals(1, deleted.size());
        MockData md2 = (MockData)deleted.get(0).getData();
        assertEquals("d3", md2.mData);
        assertEquals("3", md2.mSyncId);
    }
}
