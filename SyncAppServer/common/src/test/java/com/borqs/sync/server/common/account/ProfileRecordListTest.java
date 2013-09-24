/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;

import static junit.framework.Assert.assertEquals;


/**
 * Date: 2/28/12
 * Time: 3:13 PM
 * Borqs project
 */
public class ProfileRecordListTest {
    @Test
    public void test_parse() throws Exception {
        String profiles = "[ {\n" +
                "  \"user_id\" : 5,\n" +
                "  \"basic_updated_time\" : 1326873921861,\n" +
                "  \"profile_updated_time\" : 0,\n" +
                "  \"contact_info_updated_time\" : 1324520935145,\n" +
                "  \"address_updated_time\" : 1326873921861,\n" +
                "  \"display_name\" : \"刘华东\",\n" +
                "  \"profile_privacy\" : false,\n" +
                "  \"pedding_requests\" : [ ]\n" +
                "}, {\n" +
                "  \"user_id\" : 9,\n" +
                "  \"basic_updated_time\" : 0,\n" +
                "  \"profile_updated_time\" : 0,\n" +
                "  \"contact_info_updated_time\" : 1316416828718,\n" +
                "  \"address_updated_time\" : 1316416828718,\n" +
                "  \"display_name\" : \"zw\",\n" +
                "  \"profile_privacy\" : true,\n" +
                "  \"pedding_requests\" : [ \"1\" ]\n" +
                "}, {\n" +
                "  \"user_id\" : 10,\n" +
                "  \"basic_updated_time\" : 1328005196398,\n" +
                "  \"profile_updated_time\" : 1328003488830,\n" +
                "  \"contact_info_updated_time\" : 1328005196398,\n" +
                "  \"address_updated_time\" : 1323945719098,\n" +
                "  \"display_name\" : \"姜长胜\",\n" +
                "  \"profile_privacy\" : false,\n" +
                "  \"pedding_requests\" : [ ]\n" +
                "} ]";


        StringBufferInputStream contentReader = new StringBufferInputStream(profiles);
        ProfileRecordList prl = ProfileRecordList.parseProfileList(new InputStreamReader(contentReader));

        assertEquals(3, prl.size());
    }
}
