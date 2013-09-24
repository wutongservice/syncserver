package com.borqs.sync.server.datasync.engine;

/**
 * User: b251
 * Date: 1/9/12
 * Time: 3:07 PM
 * Borqs project
 */
public enum SyncDataType {
    GROUP(0),
    CONTACTS(1);

    private int mType;
    private SyncDataType(int type){
       mType = type;
    }
}
