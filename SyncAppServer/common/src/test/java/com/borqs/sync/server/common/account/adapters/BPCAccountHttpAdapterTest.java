/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account.adapters;

import org.junit.Test;

import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;

/**
 * Date: 10/10/12
 * Time: 11:37 AM
 * Borqs project
 */
public class BPCAccountHttpAdapterTest {
    @Test
    public void test_parseCircles() throws Exception {
        String circlesInfo = "[ {" +
                "user_id: 10222," +
                "display_name : \"Jiang Yu\"," +
                "in_circles : [ {" +
                "    circle_id : \"5\"," +
                "    circle_name : \"Address Book\"" +
                "  }, {" +
                "    circle_id : \"101\",\n" +
                "    circle_name : \"同事\"\n" +
                "  } ]," +
                "  pedding_requests: [ ],\n" +
                "  social_contacts_username: \"YU,Jiang Yu,Yu Jiang\",\n" +
                "  who_suggested : [ ]\n" +
                "}, {" +
                "  user_id : 10384,\n" +
                "  display_name : \"Austin\",\n" +
                "  in_circles : [ {\n" +
                "    circle_id : \"5\",\n" +
                "    circle_name : \"Address Book\"\n" +
                "  } ]," +
                "  pedding_requests : [ \"1\" ],\n" +
                "  social_contacts_username : \"\",\n" +
                "  who_suggested\" : [ ]" +
                "} ]";

        StringBufferInputStream input = new StringBufferInputStream(circlesInfo);

        Map<String, List<String>> result = BPCAccountHttpAdapter.parseCircles(new InputStreamReader(input));

        assertEquals(2, result.size());
        List<String> c1 = result.get("10222");
        assertNotNull(c1);
        assertEquals(2, c1.size());
        assertTrue(c1.indexOf("5")!=-1);
        assertTrue(c1.indexOf("101")!=-1);
        assertTrue(c1.indexOf("6")==-1);

        List<String> c2 = result.get("10384");
        assertNotNull(c2);
        assertEquals(1, c2.size());
        assertTrue(c2.indexOf("5")!=-1);
        assertTrue(c2.indexOf("6")==-1);
    }

    @Test
    public void test_toArgumentString() throws Exception {
        ArrayList<String> list = new ArrayList<String>(3);
        list.add("a1");
        list.add("b2");
        list.add("c3");

        String args = BPCAccountHttpAdapter.toArgumentString(list);
        assertEquals("a1,b2,c3", args);
    }
}
