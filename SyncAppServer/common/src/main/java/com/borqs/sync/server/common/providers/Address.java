package com.borqs.sync.server.common.providers;

import com.borqs.sync.server.common.util.Utility;

public class Address {
	private long mContact;
	private int mType;
	private String mStreet;
	private String mCity;
	private String mState;
	private String mPostalCode;
	private String mCountry;
	private String mPostOfficeAddress;
	private String mExtendedAddress;
    private boolean mIsPrivate;
    private long mLastUpdate;

    public static final int ADDRESS_TYPE_UNKNOW  = 1;
	public static final int ADDRESS_TYPE_HOME  = 1;
    public static final int ADDRESS_TYPE_WORK  = 2;
    public static final int ADDRESS_TYPE_OTHER = 3;
	
	public Address(long contact, int type, String street, String city,
			String state, String postalCode, String country,
			String postOfficeAddress, String extendedAddress) {
		mContact = contact;
		mType = type;
		mStreet = street;
		mCity = city;
		mState = state;
		mPostalCode = postalCode;
		mCountry = country;
		mPostOfficeAddress = postOfficeAddress;
		mExtendedAddress = extendedAddress;
	}

    private Address(){}
	
	public long getContact() {
		return mContact;
	}
	public void setContact(long contact) {
		mContact = contact;
	}
	public int getType() {
		return mType;
	}
	public void setType(int type) {
        mType = type;
	}
	public String getStreet() {
		return mStreet;
	}
	public void setStreet(String street) {
        mStreet = street;
	}
	public String getCity() {
		return mCity;
	}
	public void setCity(String city) {
		mCity = city;
	}
	public String getState() {
		return mState;
	}
	public void setState(String state) {
        mState = state;
	}
	public String getPostalCode() {
		return mPostalCode;
	}
	public void setPostalCode(String postalCode) {
        mPostalCode = postalCode;
	}
	public String getCountry() {
		return mCountry;
	}
	public void setCountry(String country) {
        mCountry = country;
	}
	public String getPostOfficeAddress() {
		return mPostOfficeAddress;
	}
	public void setPostOfficeAddress(String postOfficeAddress) {
        mPostOfficeAddress = postOfficeAddress;
	}
	public String getExtendedAddress() {
		return mExtendedAddress;
	}
	public void setExtendedAddress(String extendedAddress) {
		mExtendedAddress = extendedAddress;
	}

    public void setPrivate(boolean isPrivate){
        mIsPrivate = isPrivate;
    }

    public boolean  isPrivate(){
        return mIsPrivate;
    }

    public void setLastUpdate(long lastUpdate){
        mLastUpdate = lastUpdate;
    }

    public long getLastUpdate(){
        return mLastUpdate;
    }

    public static class BUILDER{
        Address mAddress = new Address();
        public Address build(){
            return mAddress;
        }
        public BUILDER setType(int type){
            mAddress.mType = type;
            return this;
        }
        public BUILDER setCountry(String country){
            mAddress.mCountry = country;
            return this;
        }
        public BUILDER setState(String state){
            mAddress.mState = state;
            return this;
        }
        public BUILDER setCity(String city){
            mAddress.mCity = city;
            return this;
        }
        public BUILDER setStreet(String street){
            mAddress.mStreet = street;
            return this;
        }
        public BUILDER setPostalCode(String code){
            mAddress.mPostalCode = code;
            return this;
        }
        public BUILDER setPostOfficeAddress(String po){
            mAddress.mPostOfficeAddress = po;
            return this;
        }
        public BUILDER setExtends(String extend){
            mAddress.mExtendedAddress = extend;
            return this;
        }
        public BUILDER setPrivate(boolean isPrivate){
            mAddress.mIsPrivate = isPrivate;
            return this;
        }
        public BUILDER setLastUpdate(long lastUpdate){
            mAddress.mLastUpdate = lastUpdate;
            return this;
        }

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
                .append(Utility.isEmpty(mPostalCode)?"":mPostalCode)
                .append(Utility.isEmpty(mPostOfficeAddress)?"":mPostOfficeAddress)
                .append(Utility.isEmpty(mState)?"":mState)
                .append(Utility.isEmpty(mStreet)?"":mStreet);
        return valueBuilder.toString();
    }
}
