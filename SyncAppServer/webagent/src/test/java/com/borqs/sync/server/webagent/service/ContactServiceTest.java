/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.service;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Date: 5/30/12
 * Time: 3:05 PM
 * Borqs project
 */
public class ContactServiceTest {

    @Test
    public void test_isChineseName(){
        ContactService impl = new ContactService(new MockContext());

        assertTrue(impl.isChineseName("潇洒哥"));
        assertFalse(impl.isChineseName("潇洒哥a"));
        assertFalse(impl.isChineseName("Mike"));
        assertFalse(impl.isChineseName("张。］"));
        assertTrue(impl.isChineseName("张三"));
        assertFalse(impl.isChineseName("张"));
        assertFalse(impl.isChineseName("欧阳一二三"));
    }
}
