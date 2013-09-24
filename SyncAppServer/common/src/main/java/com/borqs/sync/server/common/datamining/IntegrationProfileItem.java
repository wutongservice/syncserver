package com.borqs.sync.server.common.datamining;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 6/13/12
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationProfileItem {

    private long mContact;
    private String mValue;
    private int mType;
    private boolean mIsPrivate;
    private long mLastUpdate;
    private int mCount;

    public void setCount(int count){
        mCount = count;
    }

    public int getCount(){
        return mCount;
    }

    public void setContactId(long contactId){
        mContact = contactId;
    }
    
    public long getContactId(){
        return mContact;
    }
    
    public void setValue(String value){
        mValue = value;
    }
    
    public String getValue(){
        return mValue;
    }
    
    public void setType(int type){
        mType = type;
    }
    
    public int getType(){
        return mType;
    }

    public void setIsPrivate(boolean isPrivate){
        mIsPrivate = isPrivate;
    }

    public boolean isPrivate(){
        return mIsPrivate;
    }

    public void setLastUpdate(long lastUpdate){
        mLastUpdate = lastUpdate;
    }

    public long getLastUpdate(){
        return mLastUpdate;
    }
}
