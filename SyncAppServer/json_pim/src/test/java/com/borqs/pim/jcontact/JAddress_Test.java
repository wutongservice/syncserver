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



public class JAddress_Test {
    @Test
    public void new_test() throws JSONException{
        TypedEntity addr = new JAddress(JAddress.HOME, "street", "city", "prov", "10000");
        assertEquals(JAddress.HOME, addr.getType());
        assertEquals("street", JAddress.street(addr.getValue()));
        assertEquals("city", JAddress.city(addr.getValue()));
        assertEquals("prov", JAddress.province(addr.getValue()));
        assertEquals("10000", JAddress.zipcode(addr.getValue()));
    }
    
    @Test
    public void parase_test() throws JSONException{
        JSONObject jaddr = new JSONObject("{WORK:{ST:mystreet, CITY:Qingdao, PRO:Shandong, ZC:10000}}");
        JAddress addr = new JAddress();
        addr.parse(jaddr);
        
        TypedEntity enity = (TypedEntity)addr;
        assertEquals(JAddress.WORK, enity.getType());
        assertEquals("mystreet", JAddress.street(enity.getValue()));
        assertEquals("Qingdao", JAddress.city(enity.getValue()));
        assertEquals("Shandong", JAddress.province(enity.getValue()));
        assertEquals("10000", JAddress.zipcode(enity.getValue())); 
    }
}
