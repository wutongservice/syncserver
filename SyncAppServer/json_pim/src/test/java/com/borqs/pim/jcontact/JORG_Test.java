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

public class JORG_Test {
    @Test
    public void new_test() throws JSONException{
        TypedEntity org = new JORG(JORG.WORK, "Borqs", "Engineer");
        assertEquals(JORG.WORK, org.getType());
        assertEquals("Borqs", JORG.company(org.getValue()));
        assertEquals("Engineer", JORG.title(org.getValue()));
    }
    
    @Test
    public void parase_test() throws JSONException{
        JSONObject jorg = new JSONObject("{OTHER:{COMPANY:Borqs,TITLE:engineer}}");
        JORG org = new JORG();
        org.parse(jorg);
        
        TypedEntity enity = (TypedEntity)org;
        assertEquals(JORG.OTHER, org.getType());
        assertEquals("Borqs", JORG.company(org.getValue()));
        assertEquals("engineer", JORG.title(org.getValue()));
    }
}
