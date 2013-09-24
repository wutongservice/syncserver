/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.account;

public class MessageStruct {

    private String mCmd;
    private String mPassword;
    private String mGuid;
    
    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String guid) {
        mGuid = guid;
    }

    public String getCmd() {
        return mCmd;
    }

    public void setCmd(String cmd) {
        mCmd = cmd;
    }

    public String getPassword(){
        return mPassword;
    }
    
    public void setPassword(String password){
        mPassword = password;
    }

}
