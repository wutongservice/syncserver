/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

import com.borqs.sync.server.common.json.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-2-26
 * Time: 上午11:59
 */
public final class ProfileRecordList extends LinkedList<ProfileRecord>{
    public static final ProfileRecordList EMPTY = new ProfileRecordList();

    public static ProfileRecordList parseProfileList(Reader reader) throws Exception {
        return parseProfileList(reader, null);
    }
    public static ProfileRecordList parseProfileList(Reader reader, Logger logger) throws Exception {
        ProfileRecordList result = new ProfileRecordList();
        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(true);
        jsonReader.beginArray();
        while(jsonReader.hasNext()){
            ProfileRecord record = ProfileRecord.createFrom(jsonReader);
            if(logger != null){
                logger.log(Level.INFO, "ProfileRecordList: add <" +record.asContact().getDisplayName() +">");
            }
            result.add(record);
        }
        jsonReader.endArray();
        return result;
    }

    private ProfileRecordList(){}
}
