/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

import com.borqs.sync.server.common.json.JsonReader;
import com.borqs.sync.server.common.providers.ContactGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Date: 3/26/12
 * Time: 1:40 PM
 * Borqs project
 *
 {
 "circle_id" : 5,
 "circle_name" : "Address Book",
 "member_count" : 87,
 "updated_time" : 1329270947231,
 "members" : "10000,10001,10004,10005,10006,10008,10009,10012,10014,10015,10016,10018,10020,10025,10027,10033,10036,10040,10041,10042,10043,10046,10051,10055,10056,10058,10125,10178,10212,10222,10255,10259,10288,10317,10320,10328,10356,10357,10358,10362,10363,10364,10368,10384,10392,10405,10408,10420,10425,10430,10439,10454,10498,10502,10518,10523,10524,10720,11361,12196,12210,12450,12468,12481,12581,12651,13026,13404,13811,14468,14835,14851,14853,14890,14893,14939,14940,14971,14991,14993,14996,15000,15096,15116,15123,15141,17038"
 }
 */
public final class Circle {
    private static final String FIELD_ID = "circle_id";
    private static final String FIELD_NAME = "circle_name";
    private static final String MEMBER_COUNT = "member_count";
    private static final String LAST_UPDATE = "updated_time";
    private static final String MEMBERS = "members";
    private static final String USER_ID = "user_id";

    //known id
    private static final int CIRCLE_BLOCKED_ID = 4;
    private static final int CIRCLE_ADDRESS_BOOK_ID = 5;
    private static final int CIRCLE_DEFAULT_ID = 6;
    private static final int CIRCLE_FAMILY_ID = 9;
    private static final int CIRCLE_CLOSED_FRIENDS_ID = 10;
    private static final int CIRCLE_ACQUAINTANCE_ID = 11;

    // id<100, means the circle is pre-defined
    private static final int MAX_PRELOADED_CIRCLE_ID = 100;

    private int mId;
    private String mName;
    private long mLastUpdate;
    private int mMemberCount;
    private List<String> mMembers;
    
    public int getId(){
        return mId;
    }
    
    public String getName(){
        if(isPreloadedCircle(getId())){
            return toShortName(getId());
        }
        return mName;
    }

    public long getLastUpdate(){
        return mLastUpdate;
    }
    
    public int getMemberCount(){
        return mMemberCount;
    }

    public List<String> getMemebers(){
        return mMembers==null? Collections.EMPTY_LIST: mMembers;
    }

    static Circle createFrom(JsonReader input) throws IOException {
        Circle c = new Circle();
        c.parse(input);
        return c;
    }


    public ContactGroup convert(){
        return new ContactGroup(getName());
    }

    private Circle(){}

    // {"circle_id" : 5,
    // "circle_name" : "Address Book",
    // "member_count" : 70,
    // "updated_time" : 1323416043215,
    // "members" :[{}]
    //}
    private void parse(JsonReader reader) throws IOException {
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(FIELD_ID.equals(name)){
                mId = reader.nextInt();
            } else if(FIELD_NAME.equals(name)){
                mName = reader.nextString();
            } else if(MEMBER_COUNT.equals(name)){
                mMemberCount = reader.nextInt();
            } else if(LAST_UPDATE.equals(name)){
                mLastUpdate = reader.nextLong();
            } else if(MEMBERS.equals(name)){
                mMembers = parseMembers(reader);
            } else{
                reader.skipValue();
            }
        }
        reader.endObject();
    }
    
    private List<String> parseMembers(JsonReader reader) throws IOException {
        ArrayList<String> buddyList = new ArrayList<String>();
        String str_members = reader.nextString();

        if(str_members!=null && str_members.trim().length()>0){
            String[] members = str_members.split(",");
            for(String m : members){
                buddyList.add(m);
            }
        }

        return buddyList;
    }

    public boolean isVisibleToUser(){
        if(mId == CIRCLE_BLOCKED_ID || mId == CIRCLE_ADDRESS_BOOK_ID){
            return false;
        }

        return true;
    }
    
    private boolean isPreloadedCircle(int circleId){
        return circleId<MAX_PRELOADED_CIRCLE_ID;
    }

    public static String toShortName(int id) {
        switch (id){
            case CIRCLE_ACQUAINTANCE_ID:
                return "熟人";
            case CIRCLE_ADDRESS_BOOK_ID:
                return "名片交换";
            case CIRCLE_BLOCKED_ID:
                return "黑名单";
            case CIRCLE_CLOSED_FRIENDS_ID:
                return "朋友";
            case CIRCLE_DEFAULT_ID:
                return "关注对象";
            case CIRCLE_FAMILY_ID:
                return "家庭";
            default:
                return "未分组联系人";
        }
    }
}
