/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.pim.jcontact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;



public class JName_Test {
    @Test
    public void new_test() throws JSONException{
        JName name = new JName();
        name.put(JName.FIRSTNAME, "first_name");
        name.put(JName.MIDDLENAME, "middle_name");
        name.put(JName.LASTNAME, "last_name");
        name.put(JName.FIRSTNAME_PINYIN, "fist_pinyin");
        name.put(JName.MIDDLENAME_PINYIN, "middle_pinyin");
        name.put(JName.LASTNAME_PINYIN, "last_pinyin");
        name.put(JName.NICKNAME, "nick_name");
        name.put(JName.PREFIX, "");
        name.put(JName.POSTFIX, "postfix");
        
        String json = name.toString();
        assertNotNull(json);
        
        JSONObject jn = new JSONObject(json);
        assertEquals("first_name", jn.getString(JName.FIRSTNAME));
        assertEquals("middle_name", jn.getString(JName.MIDDLENAME));
        assertEquals("last_name", jn.getString(JName.LASTNAME));
        assertEquals("fist_pinyin", jn.getString(JName.FIRSTNAME_PINYIN));
        assertEquals("middle_pinyin", jn.getString(JName.MIDDLENAME_PINYIN));
        assertEquals("last_pinyin", jn.getString(JName.LASTNAME_PINYIN));
        assertEquals("nick_name", jn.getString(JName.NICKNAME));
        assertEquals("postfix", jn.getString(JName.POSTFIX));
    }
    
    @Test
    public void parse_test() throws JSONException{
        JSONObject jn = new JSONObject("{ FN:三, LN:李, NN:小三 }");
        JName name = new JName();
        name.parse(jn);
        assertNull(name.getType());
        assertNull(name.getValue());
        assertEquals("三", name.get(JName.FIRSTNAME));
        assertNull(name.get(JName.MIDDLENAME));
        assertEquals("李", name.get(JName.LASTNAME));
        assertEquals("小三", name.get(JName.NICKNAME));
        assertNull(name.get(JName.FIRSTNAME_PINYIN));
        assertNull(name.get(JName.MIDDLENAME_PINYIN));
        assertNull(name.get(JName.LASTNAME_PINYIN));
        assertNull(name.get(JName.PREFIX));
        assertNull(name.get(JName.POSTFIX));
    }
}
