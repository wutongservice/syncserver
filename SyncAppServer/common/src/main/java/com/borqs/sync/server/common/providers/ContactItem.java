package com.borqs.sync.server.common.providers;

public class ContactItem {
    public static final String TABLE_NAME = "borqs_pim_contact_item";
    
    //column define
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_PRIVATE = "private";
    public static final String COLUMN_LAST_UPDATE = "last_update";

    //type define
    //phone
	public static final int    CONTACT_ITEM_TYPE_UNDEFINED = -1;
    public static final int    CONTACT_ITEM_TYPE_ASSISTANT_NUMBER  = 13;
    public static final int    CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER  = 11;
    public static final int    CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER  = 10;
    public static final int    CONTACT_ITEM_TYPE_CALLBACK_NUMBER  = 15;
    public static final int    CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER  = 20;
    public static final int    CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER  = 12;
    public static final int    CONTACT_ITEM_TYPE_HOME_FAX_NUMBER  = 2;
    public static final int    CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER  = 3;
    public static final int    CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER  = 30;
    public static final int    CONTACT_ITEM_TYPE_PAGER_NUMBER  = 14;
    public static final int    CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER  = 21;
    public static final int    CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER  = 1;
    public static final int    CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER  = 22;
    public static final int    CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER  = 31;
    public static final int    CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER  = 29;
    public static final int    CONTACT_ITEM_TYPE_TELEX_NUMBER  = 27;
    public static final int    CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER  = 28;
    //email
    public static final int    CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS  = 4;
    public static final int    CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS  = 16;
    public static final int    CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS  = 23;
    //webpage
    public static final int    CONTACT_ITEM_TYPE_HOME_WEB_PAGE  = 6;
    public static final int    CONTACT_ITEM_TYPE_WEB_PAGE  = 5;
    public static final int    CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE  = 7;
    //other
    public static final int    CONTACT_ITEM_TYPE_INSTANT_MESSENGER  = 8;
    public static final int    CONTACT_ITEM_TYPE_BUSINESS_LABEL  = 17;
    public static final int    CONTACT_ITEM_TYPE_HOME_LABEL  = 18;
    public static final int    CONTACT_ITEM_TYPE_OTHER_LABEL  = 19;



    public static final int    TYPE_X_TAG_ACCOUNT_TYPE = 101;
    public static final int    TYPE_X_TAG_GROUP = 102;
    public static final int    TYPE_X_TAG_BORQS_NAME = 103;
    public static final int    TYPE_X_TAG_PHONETIC_FIRST_NAME = 104;
    public static final int    TYPE_X_TAG_PHONETIC_MIDDLE_NAME = 105;
    public static final int    TYPE_X_TAG_PHONETIC_LAST_NAME = 106;
    public static final int    TYPE_X_TAG_PREFIX_NAME = 107;
    public static final int    TYPE_X_TAG_STARRED = 108;
    public static final int    TYPE_X_TAG_BLOCK = 109;
    public static final int    TYPE_X_TAG_RINGTONG = 110;

    public static final int    TYPE_X_TAG_IM_MSN = 111;
    public static final int    TYPE_X_TAG_IM_GTALK = 112;
    public static final int    TYPE_X_TAG_IM_SKYPE = 113;
    public static final int    TYPE_X_TAG_IM_AIM = 114;
    public static final int    TYPE_X_TAG_IM_YAHOO = 115;
    public static final int    TYPE_X_TAG_IM_ICQ = 116;
    public static final int    TYPE_X_TAG_IM_JABBER = 117;
    public static final int    TYPE_X_TAG_IM_NETMEETING = 118;
    public static final int    TYPE_X_TAG_IM_WIN_LIVE = 119;

    public static final String VALUe_ACCOUNT_TYPE_BORQS = "com.borqs";

    private long mContact;
    private String mValue;
    private int mType;
    private boolean mIsPrivate;
    private long mLastUpdate;


    public ContactItem(String value, int type,boolean isPrivate,long lastUpdate) {
		mValue = value;
		mType = type;
        mIsPrivate = isPrivate;
        mLastUpdate = lastUpdate;
	}

    public ContactItem() {
    }
	
	public long getContact() {
		return mContact;
	}

	public void setContact(long contact) {
        mContact = contact;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(String value) {
        mValue = value;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
        mType = type;
	}
    
    public boolean isPrivate(){
        return mIsPrivate;
    }

    public void setPrivate(boolean isPrivate){
        mIsPrivate = isPrivate;
    }

    public void setLastUpdate(long lastUpdate){
        mLastUpdate = lastUpdate;
    }

    public long getLastUpdate(){
        return mLastUpdate;
    }
}
