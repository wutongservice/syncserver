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


public class JXTag_Test {
    @Test
    public void new_test() throws JSONException{
        TypedEntity x = new JXTag("myXName", "MyValue");
        assertEquals("myXName", x.getType());
        assertEquals("MyValue", x.getValue());
    }
    
    @Test
    public void parase_test() throws JSONException{
        JSONObject jx = new JSONObject("{myXName:MyValue}");
        JXTag x = new JXTag();
        x.parse(jx);
        
        TypedEntity enity = (TypedEntity)x;
        assertEquals("myXName", enity.getType());
        assertEquals("MyValue", enity.getValue());
    }
}
