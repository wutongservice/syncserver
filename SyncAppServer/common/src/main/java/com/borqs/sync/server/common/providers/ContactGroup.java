/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.providers;

/**
 * Date: 3/27/12
 * Time: 11:23 AM
 * Borqs project
 */
public final class ContactGroup {
    private String mName;
    
    public ContactGroup(String name){
        mName = name;        
    }   
    
    public String name(){
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactGroup that = (ContactGroup) o;

        if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mName != null ? mName.hashCode() : 0;
    }
}
