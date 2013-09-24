package com.borqs.sync.server.common.datamining;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/30/12
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationProfileName {

    private String mFirstName;
    private String mMiddleName;
    private String mLastName;
    private String mBFirstName;
    private String mBMiddleName;
    private String mBLastName;
    private int mCount;

    public void setCount(int count){
        mCount = count;
    }

    public int getCount(){
        return mCount;
    }
    
    public void setBFirstName(String bFirstName){
        mBFirstName = bFirstName;
    }
    
    public String getBFirstName(){
        return mBFirstName;
    }
    
    public void setBMiddleName(String bMiddleName){
        mBMiddleName = bMiddleName;
    }
    
    public String getBMiddleName(){
        return mBMiddleName;
    }
    
    public void setBLastName(String bLastName){
        mBLastName = bLastName;
    }
    
    public String getBLastName(){
        return mBLastName;
    }

    public void setFirstName(String firstName){
        mFirstName = firstName;
    }

    public void setMiddleName(String middleName){
        mMiddleName = middleName;
    }

    public void setLastName(String lastName){
        mLastName = lastName;
    }
    
    public String getFirstName(){
        return mFirstName;
    }
    
    public String getMiddleName(){
        return mMiddleName;
    }
    
    public String getLastName(){
        return mLastName;
    }
}
