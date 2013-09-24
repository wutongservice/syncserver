/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.pim;


import com.borqs.json.JSONArray;
import com.borqs.json.JSONException;
import com.borqs.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONUtility_Test {

    @Test
    public void encodeBytes_test(){
        byte[] test = "这是一个中文字符".getBytes();
        String enStr = JSONUtility.encodeBytes(test);
        byte[] result = JSONUtility.decodeBytes(enStr);
                
        org.junit.Assert.assertArrayEquals(test, result);       
    }

    @Test
    public void has_test() throws JSONException{
        JSONArray array = new JSONArray("[v1, {a:b}, v3]");
        assertTrue(JSONUtility.has(array, "v1"));
        assertTrue(JSONUtility.has(array, new JSONObject("{a:b}")));
        assertTrue(JSONUtility.has(array, "v3"));
        assertFalse(JSONUtility.has(array, "v4"));
    }
    
    @Test
    public void putArray_test() throws JSONException{
        JSONArray array = JSONUtility.putArray("value1", "value2", new JSONObject("{a:b}"));
        assertEquals(array.length(), 3);
    }
    
    @Test
    public void getAttribute_test() throws JSONException{
        //case 1
        assertNull(JSONUtility.getAttribute("", "1111"));
        
        //case 2
        JSONObject o = new JSONObject("{key1:a, key2:b}");
        assertEquals("a", JSONUtility.getAttribute(o, "key1"));
        assertEquals("b", JSONUtility.getAttribute(o, "key2"));
        assertNull(JSONUtility.getAttribute(o, "key3"));
    }
}
