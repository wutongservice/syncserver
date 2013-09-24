/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.pim.group;
/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */

import com.borqs.sync.server.common.account.Circle;
import com.borqs.sync.server.common.account.CircleList;
import com.borqs.sync.server.common.providers.ContactGroup;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Date: 3/27/12
 * Time: 4:54 PM
 * Borqs project
 */
public class GroupSyncHandlerTest {

    @Test
    public void isSameWith_test(){
        List<ContactGroup> cg1 = new ArrayList<ContactGroup>();
        List<ContactGroup> cg2 = new ArrayList<ContactGroup>();

        cg1.add(new ContactGroup("g1"));
        cg1.add(new ContactGroup("g2"));
        cg2.add(new ContactGroup("g1"));
        cg2.add(new ContactGroup("g2"));
        assertTrue(GroupSyncHandler.isSameWith(cg1, cg2));
        assertEquals(2, cg1.size());
        assertEquals(2, cg2.size());

        cg1.clear();
        cg2.clear();
        cg1.add(new ContactGroup("g1"));
        cg2.add(new ContactGroup("g1"));
        cg2.add(new ContactGroup("g2"));
        assertFalse(GroupSyncHandler.isSameWith(cg1, cg2));
        assertEquals(1, cg1.size());
        assertEquals(2, cg2.size());

        cg1.clear();
        cg2.clear();
        cg1.add(new ContactGroup("g1"));
        cg1.add(new ContactGroup("g2"));
        cg2.add(new ContactGroup("g"));
        cg2.add(new ContactGroup("g2"));
        assertFalse(GroupSyncHandler.isSameWith(cg1, cg2));
        assertEquals(2, cg1.size());
        assertEquals(2, cg2.size());
    }

    @Test
    public void getGroupInfoOfBuddy_test() throws IOException {
        String circlesInfo = "[ {\n" +
                "  \"circle_id\" : 4,\n" +
                "  \"circle_name\" : \"Blocked\",\n" +
                "  \"member_count\" : 0,\n" +
                "  \"updated_time\" : 1329270947202,\n" +
                "  \"members\" : \"\"\n" +
                "}, {\n" +
                "  \"circle_id\" : 5,\n" +
                "  \"circle_name\" : \"Address Book\",\n" +
                "  \"member_count\" : 87,\n" +
                "  \"updated_time\" : 1329270947231,\n" +
                "  \"members\" : \"10000,10001,10004,10005,10006,10008,10009,10012,10014,10015,10016,10018,10020,10025,10027,10033,10036,10040,10041,10042,10043,10046,10051,10055,10056,10058,10125,10178,10212,10222,10255,10259,10288,10317,10320,10328,10356,10357,10358,10362,10363,10364,10368,10384,10392,10405,10408,10420,10425,10430,10439,10454,10498,10502,10518,10523,10524,10720,11361,12196,12210,12450,12468,12481,12581,12651,13026,13404,13811,14468,14835,14851,14853,14890,14893,14939,14940,14971,14991,14993,14996,15000,15096,15116,15123,15141,17038\"\n" +
                "}, {\n" +
                "  \"circle_id\" : 6,\n" +
                "  \"circle_name\" : \"Default\",\n" +
                "  \"member_count\" : 0,\n" +
                "  \"updated_time\" : 1329270947229,\n" +
                "  \"members\" : \"\"\n" +
                "}, {\n" +
                "  \"circle_id\" : 9,\n" +
                "  \"circle_name\" : \"Family\",\n" +
                "  \"member_count\" : 0,\n" +
                "  \"updated_time\" : 1329270947237,\n" +
                "  \"members\" : \"\"\n" +
                "}, {\n" +
                "  \"circle_id\" : 10,\n" +
                "  \"circle_name\" : \"Closed Friends\",\n" +
                "  \"member_count\" : 0,\n" +
                "  \"updated_time\" : 1329270947244,\n" +
                "  \"members\" : \"\"\n" +
                "}, {\n" +
                "  \"circle_id\" : 11,\n" +
                "  \"circle_name\" : \"Acquaintance\",\n" +
                "  \"member_count\" : 4,\n" +
                "  \"updated_time\" : 1329270947259,\n" +
                "  \"members\" : \"10320,13404,14893,17038\"\n" +
                "}]";
        StringBufferInputStream contentReader = new StringBufferInputStream(circlesInfo);
        CircleList cl = CircleList.createFrom(new InputStreamReader(contentReader));

        List<ContactGroup> lgroup0 = GroupSyncHandler.getGroupInfoOfBuddy("2", cl);
        assertEquals(0, lgroup0.size());

        List<ContactGroup> lgroup1 = GroupSyncHandler.getGroupInfoOfBuddy("10000", cl);
        assertEquals(0, lgroup1.size());

        List<ContactGroup> lgroup2 = GroupSyncHandler.getGroupInfoOfBuddy("10320", cl);
        assertEquals(1, lgroup2.size());
        assertEquals(Circle.toShortName(11), lgroup2.get(0).name());
    }
        
}
