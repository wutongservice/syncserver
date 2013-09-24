/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Date: 3/26/12
 * Time: 3:11 PM
 * Borqs project
 */
public class CircleTest {

    @Test
    public void test_parse_circle() throws IOException {
        String circlesInfo = "[{\n" +
                "  \"circle_id\" : 11,\n" +
                "  \"circle_name\" : \"Acquaintance\",\n" +
                "  \"member_count\" : 4,\n" +
                "  \"updated_time\" : 1329270947259,\n" +
                "  \"members\" : \"10320,13404,14893,17038\"\n" +
                "} ]";

        StringBufferInputStream contentReader = new StringBufferInputStream(circlesInfo);
        CircleList cl = CircleList.createFrom(new InputStreamReader(contentReader));
        
        assertEquals(1, cl.size());
        
        Circle c = cl.get(0);
        
        assertEquals(11, c.getId());
        assertEquals(Circle.toShortName(11), c.getName());
        assertEquals(4, c.getMemberCount());
        assertEquals(4, c.getMemebers().size());
        assertEquals(1329270947259L, c.getLastUpdate());
    }

    @Test
    public void test_parse_list() throws IOException {
        String circlesInfo = "[ {\n" +
                "  \"circle_id\" : 4,\n" +
                "  \"circle_name\" : \"Blocked\",\n" +
                "  \"member_count\" : 0,\n" +
                "  \"updated_time\" : 1316416833029\n" +
                "}, {\n" +
                "  \"circle_id\" : 5,\n" +
                "  \"circle_name\" : \"Address Book\",\n" +
                "  \"member_count\" : 70,\n" +
                "  \"updated_time\" : 1323416043215\n" +
                "}, {\n" +
                "  \"circle_id\" : 6,\n" +
                "  \"circle_name\" : \"Default\",\n" +
                "  \"member_count\" : 69,\n" +
                "  \"updated_time\" : 1322644624237\n" +
                "}, {\n" +
                "  \"circle_id\" : 9,\n" +
                "  \"circle_name\" : \"Family\",\n" +
                "  \"member_count\" : 0,\n" +
                "  \"updated_time\" : 1316416830538\n" +
                "}, {\n" +
                "  \"circle_id\" : 10,\n" +
                "  \"circle_name\" : \"Closed Friends\",\n" +
                "  \"member_count\" : 24,\n" +
                "  \"updated_time\" : 1323158282161\n" +
                "}, {\n" +
                "  \"circle_id\" : 11,\n" +
                "  \"circle_name\" : \"Acquaintance\",\n" +
                "  \"member_count\" : 1,\n" +
                "  \"updated_time\" : 1323416043214\n" +
                "} ]";

        StringBufferInputStream contentReader = new StringBufferInputStream(circlesInfo);
        CircleList cl = CircleList.createFrom(new InputStreamReader(contentReader));

        assertEquals(6, cl.size());
        for(Circle c  : cl){
            assertTrue(c.getId() > 0);
            assertNotNull(c.getName());
            assertTrue(c.getLastUpdate() > 0);

            List<String> memebers = c.getMemebers();
            assertEquals(0, memebers.size());
        }
    }

    @Test
    public void test_parse_circle_list_with_buddy() throws IOException {
        String circlesInfo = "[ {\n" +
                "  \"circle_id\" : 10,\n" +
                "  \"circle_name\" : \"Closed Friends\",\n" +
                "  \"member_count\" : 2,\n" +
                "  \"updated_time\" : 1323158282161,\n" +
                "  \"members\" : \"5,25\"" +
                "}]";
        StringBufferInputStream contentReader = new StringBufferInputStream(circlesInfo);
        CircleList cl = CircleList.createFrom(new InputStreamReader(contentReader));

        assertEquals(1, cl.size());
        Circle c = cl.get(0);

        assertEquals(10, c.getId());
        assertEquals(Circle.toShortName(10), c.getName());
        assertEquals(2, c.getMemberCount());
        assertEquals(1323158282161L, c.getLastUpdate());
        
        List<String> memebers = c.getMemebers();
        assertEquals(2, memebers.size());
        assertTrue("5".equals(memebers.get(0)) || "5".equals(memebers.get(1)));
        assertTrue("25".equals(memebers.get(0)) || "25".equals(memebers.get(1)));
    }
}
