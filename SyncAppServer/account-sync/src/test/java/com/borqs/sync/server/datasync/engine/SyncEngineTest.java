/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

import com.borqs.sync.server.datasync.engine.policy.Policy;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * User: b251
 * Date: 1/11/12
 * Time: 4:56 PM
 * Borqs project
 */
public class SyncEngineTest {
    private static final int BASE_TIME = 0;
    @Test
    public void sync_anchor_test() throws AccountSyncException {
        SimpleSyncSource source1 = new SimpleSyncSource();
        MockData a_d1 = new MockData("d1", "1", BASE_TIME+100);
        MockData a_d2 = new MockData("d2", "2", BASE_TIME+105);
        ArrayList<MockData> datas = new ArrayList<MockData>();
        datas.add(a_d1);
        datas.add(a_d2);
        source1.expect_Items(datas);
        source1.expect_syncAnchor(105);

        MockSyncSource.MockDataStore dataStore2 = new MockSyncSource.MockDataStore();
        MockData b_d1 = new MockData("b_d1", "1", BASE_TIME+101);
        MockData b_d2 = new MockData("b_d2", "2", BASE_TIME+106);
        dataStore2.expectRecordSet(new MockData[]{b_d1, b_d2});
        MockSyncSource source2 = new MockSyncSource("borqsId",  dataStore2);
        source2.expect_syncAnchor(106);

        SyncEngine engine = new SyncEngine(new MockContext());
        boolean result = engine.sync(source2, source1, new Policy() {
            @Override
            public boolean forceUpdate() {
                return true;
            }
        }, 0);

        assertTrue(result);
        assertEquals(105, source1.getSyncAnchor().getAnchor());
        assertEquals(106, source2.getSyncAnchor().getAnchor());
    }

    @Test
    public void sync_no_update_test() throws AccountSyncException {
        SimpleSyncSource source1 = new SimpleSyncSource();
        MockData a_d1 = new MockData("d1", "1", BASE_TIME+100);
        MockData a_d2 = new MockData("d2", "2", BASE_TIME+105);
        ArrayList<MockData> datas = new ArrayList<MockData>();
        datas.add(a_d1);
        datas.add(a_d2);
        source1.expect_Items(datas);
        source1.expect_syncAnchor(105);

        MockSyncSource.MockDataStore dataStore2 = new MockSyncSource.MockDataStore();
        MockData b_d1 = new MockData("b_d1", "1", BASE_TIME+101);
        MockData b_d2 = new MockData("b_d2", "2", BASE_TIME+106);
        dataStore2.expectRecordSet(new MockData[]{b_d1, b_d2});
        MockSyncSource source2 = new MockSyncSource("borqsId",  dataStore2);
        source2.expect_syncAnchor(106);

        SyncEngine engine = new SyncEngine(new MockContext());
        boolean result = engine.sync(source2, source1, new Policy() {
            @Override
            public boolean forceUpdate() {
                return true;
            }
        }, 0);

        assertTrue(result);
        //test no update to target(source1)
        assertEquals(2, source1.getAllItems().size());
    }

    @Test
    public void sync_change_test() throws AccountSyncException {
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
        MockData b_d1 = new MockData("d1", "1", BASE_TIME+80);   //no changed
        MockData b_d2 = new MockData("d2-2", "2", BASE_TIME+100);  //updated
        //        MockData a_d3 = new MockData("d3", "2", baseTime+99)    //deleted
        MockData b_d4 = new MockData("d4", "4", BASE_TIME+100);    //added
        MockData b_d5 = new MockData("d5", "5", BASE_TIME+101);    //added
        dataStore2.expectRecordSet(new MockData[]{b_d1, b_d2, b_d4, b_d5});
        MockSyncSource source2 = new MockSyncSource("borqsId",  dataStore2);
        source2.expect_syncAnchor(BASE_TIME+99);

        SyncEngine engine = new SyncEngine(new MockContext());
        boolean result = engine.sync(source2, source1, new Policy() {
            @Override
            public boolean forceUpdate() {
                return true;
            }
        }, 0);

        assertTrue(result);
        //check update on target(source1)
        SyncItemList items = source1.getAllItems();
        //delete 1, add 2
        assertEquals(4, items.size());
        assertTrue(isIn(a_d1, items));    //no change
        assertTrue(isIn(b_d2, items));    //updated
        assertFalse(isIn(a_d3, items));
        assertTrue(isIn(b_d4, items));
        assertTrue(isIn(b_d5, items));
    }

    private boolean isIn(MockData data, SyncItemList list) throws AccountSyncException {
        for(BaseSyncItem i : list){
            MockData d = (MockData)i.getData();
            if(data.mData.equals(d.mData) && data.mSyncId.equals(d.mSyncId)){
                return true;
            }
        }

        return false;
    }
}
