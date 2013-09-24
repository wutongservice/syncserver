/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.pim.jcontact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import com.borqs.pim.jcontact.JContact.TypedEntity;


public class JPhone_Test {
    @Test
    public void new_test() throws JSONException{
        //case 1
        TypedEntity phone = new JPhone(JPhone.OTHER, "88591012", true);
        assertEquals(JPhone.OTHER, phone.getType());
        assertEquals("88591012", phone.getValue());
        assertTrue(JPhone.isPrimary(phone));
        
        //case 2
        phone = new JPhone(JPhone.WORK, "88001100", false);
        assertEquals(JPhone.WORK, phone.getType());
        assertEquals("88001100", phone.getValue());
        assertFalse(JPhone.isPrimary(phone));
    }
    
    @Test
    public void parase_test() throws JSONException{
        //case 1
        JSONObject jphone = new JSONObject("{WORK:88001100, EXTRA:[PRIMARY]}");
        JPhone phone = new JPhone();
        phone.parse(jphone);
        
        TypedEntity entity = (TypedEntity)phone;
        assertEquals(JPhone.WORK, entity.getType());
        assertEquals("88001100", entity.getValue());
        assertTrue(JPhone.isPrimary(entity));
        
        //case 2
        jphone = new JSONObject("{MOBILE:88001101, EXTRA:[OTHER]}");
        phone = new JPhone();
        phone.parse(jphone);
        
        entity = (TypedEntity)phone;
        assertEquals(JPhone.MOBILE, entity.getType());
        assertEquals("88001101", entity.getValue());
        assertFalse(JPhone.isPrimary(entity)); 
        
        //case 3
        jphone = new JSONObject("{OTHER:88001102}");
        phone = new JPhone();
        phone.parse(jphone);
        
        entity = (TypedEntity)phone;
        assertEquals(JPhone.OTHER, entity.getType());
        assertEquals("88001102", entity.getValue());
        assertFalse(JPhone.isPrimary(entity)); 
    }
}
