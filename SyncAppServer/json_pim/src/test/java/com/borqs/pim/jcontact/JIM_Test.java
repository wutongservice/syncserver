/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.pim.jcontact;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import com.borqs.pim.jcontact.JContact.TypedEntity;


public class JIM_Test {
    @Test
    public void new_test() throws JSONException{
        TypedEntity im = new JIM(JIM.QQ, "1111223");
        assertEquals(JIM.QQ, im.getType());
        assertEquals("1111223", im.getValue());
    }
    
    @Test
    public void parase_test() throws JSONException{
        JSONObject jim = new JSONObject("{MSN:gg@gmail.com}");
        JIM im = new JIM();
        im.parse(jim);
        
        TypedEntity enity = (TypedEntity)im;
        assertEquals(JIM.MSN, enity.getType());
        assertEquals("gg@gmail.com", enity.getValue());
    }
}
