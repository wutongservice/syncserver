/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.datasync.engine;

/**
 * Date: 1/9/12
 * Time: 2:18 PM
 * Borqs project
 */
public final class TimeRange {
    private long mBegin;
    private long mEnd;
    public TimeRange(long begin, long end) {
        mBegin = begin;
        mEnd = end;
    }

    public long begin(){
        return mBegin;
    }

    public long end(){
        return mEnd;
    }

    public boolean isCover(long point){
        return mBegin<point;
    }
}
