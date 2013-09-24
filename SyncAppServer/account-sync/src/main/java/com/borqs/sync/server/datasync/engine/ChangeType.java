package com.borqs.sync.server.datasync.engine;

/**
 * User: b251
 * Date: 1/12/12
 * Time: 3:14 PM
 * Borqs project
 */
public enum ChangeType {
    ADD(0),
    UPDATE(1),
    DELETION(2);

    private int mType;
    private ChangeType(int type){
       mType = type;
    }
}
