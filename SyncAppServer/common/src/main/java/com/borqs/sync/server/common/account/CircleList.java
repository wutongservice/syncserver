/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

import com.borqs.sync.server.common.json.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

/**
 * Date: 3/26/12
 * Time: 2:50 PM
 * Borqs project
 */
public class CircleList extends LinkedList<Circle> {
    public static final CircleList EMPTY = new CircleList();

    public static CircleList createFrom(Reader reader) throws IOException {
        return createFrom(new JsonReader(reader));
    }
    
    public static CircleList createFrom(JsonReader reader) throws IOException {
        CircleList cl = new CircleList();
        reader.setLenient(true);
        reader.beginArray();
        while(reader.hasNext()){
            Circle c = Circle.createFrom(reader);
            if(c != null){
                cl.add(c);
            }
        }
        reader.endArray();

        return cl;
    }

    private CircleList(){}
}
