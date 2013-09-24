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



public class JEmail_Test {
    @Test
    public void new_test() throws JSONException{
        //case 1
        TypedEntity email = new JEMail(JEMail.OTHER, "test@borqs.com", true);
        assertEquals(JEMail.OTHER, email.getType());
        assertEquals("test@borqs.com", email.getValue());
        assertTrue(JEMail.isPrimary(email));
        
        //case 2
        email = new JEMail(JEMail.WORK, "test@borqs.com", false);
        assertEquals(JEMail.WORK, email.getType());
        assertEquals("test@borqs.com", email.getValue());
        assertFalse(JEMail.isPrimary(email));
    }
    
    @Test
    public void parase_test() throws JSONException{
        //case 1
        JSONObject jemail = new JSONObject("{WORK:sss@borqs.com, EXTRA:[PRIMARY]}");
        JEMail email = new JEMail();
        email.parse(jemail);
        
        TypedEntity entity = (TypedEntity)email;
        assertEquals(JEMail.WORK, entity.getType());
        assertEquals("sss@borqs.com", entity.getValue());
        assertTrue(JEMail.isPrimary(entity));
        
        //case 2
        jemail = new JSONObject("{MOBILE:sss@borqs.com, EXTRA:[OTHER]}");
        email = new JEMail();
        email.parse(jemail);
        
        entity = (TypedEntity)email;
        assertEquals(JEMail.MOBILE, entity.getType());
        assertEquals("sss@borqs.com", entity.getValue());
        assertFalse(JEMail.isPrimary(entity)); 
        
        //case 3
        jemail = new JSONObject("{OTHER:sss@borqs.com}");
        email = new JEMail();
        email.parse(jemail);
        
        entity = (TypedEntity)email;
        assertEquals(JEMail.OTHER, entity.getType());
        assertEquals("sss@borqs.com", entity.getValue());
        assertFalse(JEMail.isPrimary(entity)); 
    }
}
