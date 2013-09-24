package com.borqs.pim.jcontact;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import com.borqs.pim.jcontact.JContact.TypedEntity;

/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

public class JWebpage_Test {
    @Test
    public void new_test() throws JSONException{
        TypedEntity webpage = new JWebpage(JWebpage.PROFILE, "http://192.168.1.1/220?ud=2");
        assertEquals(JWebpage.PROFILE, webpage.getType());
        assertEquals("http://192.168.1.1/220?ud=2", webpage.getValue());
    }
    
    @Test
    public void parase_test() throws JSONException{
        JSONObject jwebpage = new JSONObject("{HOMEPAGE:\"http:\\/\\/192.168.1.1\\/220?ud=20\"}");
        JWebpage webpage = new JWebpage();
        webpage.parse(jwebpage);
        
        TypedEntity enity = (TypedEntity)webpage;
        assertEquals(JWebpage.HOMEPAGE, enity.getType());
        assertEquals("http://192.168.1.1/220?ud=20", enity.getValue());
    }
}
