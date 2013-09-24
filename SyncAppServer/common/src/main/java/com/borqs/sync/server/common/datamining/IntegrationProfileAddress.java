package com.borqs.sync.server.common.datamining;

import com.borqs.sync.server.common.util.Utility;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/30/12
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationProfileAddress {

    private String mCity;
    private String mStreet;
    private String mState;
    private String mPostcode;
    private String mPostOfficeAddress;
    private String mExtendedAddress;
    private String mCountry;
    private int mCount;
    private boolean mIsPrivate;
    private long mLastUpdate;
    
    public boolean isPrivate(){
        return mIsPrivate;
    }
    
    public void setPrivate(boolean isPrivate){
        mIsPrivate = isPrivate;
    }

    public void setCount(int count){
        mCount = count;
    }
    
    public int getCount(){
        return mCount;
    }

    public void setCity(String city){
        mCity = city;
    }
    
    public String getCity(){
        return mCity;
    }

    public void setStreet(String street){
        mStreet = street;
    }

    public String getStreet(){
        return mStreet;
    }

    public void setState(String state){
        mState = state;
    }

    public String getState(){
        return mState;
    }

    public void setPostcode(String postcode){
        mPostcode = postcode;
    }

    public String getPostcode(){
        return mPostcode;
    }

    public void setPostOfficeAddress(String postOfficeAddress){
        mPostOfficeAddress = postOfficeAddress;
    }
    
    public String getPostOfficeAddress(){
        return mPostOfficeAddress;
    }
    
    public void setExtendedAddress(String extendedAddress){
        mExtendedAddress = extendedAddress;
    }
    
    public String getExtendedAddress(){
        return mExtendedAddress;
    }

    public void setCountry(String country) {
        mCountry = country;
    }
    
    public String getCountry(){
        return mCountry;
    }

    /**
     *
     * @return the address string value
     */
    public String toValue(){
        StringBuilder valueBuilder = new StringBuilder();
        valueBuilder.append(Utility.isEmpty(mCity)?"":mCity)
                .append(Utility.isEmpty(mCountry)?"":mCountry)
                .append(Utility.isEmpty(mExtendedAddress)?"":mExtendedAddress)
                .append(Utility.isEmpty(mPostcode)?"":mPostcode)
                .append(Utility.isEmpty(mPostOfficeAddress)?"":mPostOfficeAddress)
                .append(Utility.isEmpty(mState)?"":mState)
                .append(Utility.isEmpty(mStreet)?"":mStreet);
        return valueBuilder.toString();
    }

    public void setLastUpdate(long lastUpdate) {
        mLastUpdate = lastUpdate;
    }

    public long getLastUpdate(){
        return mLastUpdate;
    }
}
