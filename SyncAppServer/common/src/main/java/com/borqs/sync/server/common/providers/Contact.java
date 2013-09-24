package com.borqs.sync.server.common.providers;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    public static final String FIELD_ID = "id";
    public static final String FIELD_USERID = "userid";
    public static final String FIELD_BORQSID = "borqsid";
    public static final String FIELD_LAST_UPDATE = "last_update";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_PHOTO_TYPE = "photo_type";
    public static final String FIELD_IMPORTANCE = "importance";
    public static final String FIELD_SENSITIVITY = "sensitivity";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_FOLDER = "folder";
    public static final String FIELD_ANNIVERSARY = "anniversary";
    public static final String FIELD_FIRST_NAME = "first_name";
    public static final String FIELD_MIDDLE_NAME = "middle_name";
    public static final String FIELD_LAST_NAME = "last_name";
    public static final String FIELD_DISPLAY_NAME = "display_name";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_BODY = "body";
    public static final String FIELD_CATEGORIES = "categories";
    public static final String FIELD_CHILDREN = "children";
    public static final String FIELD_HOBBIES = "hobbies";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_INITIALS = "initials";
    public static final String FIELD_LANGUAGES = "languages";
    public static final String FIELD_NICKNAME = "nickname";
    public static final String FIELD_SPOUSE = "spouse";
    public static final String FIELD_SUFFIX = "suffix";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_ASSISTANT = "assistant";
    public static final String FIELD_COMPANY = "company";
    public static final String FIELD_COMPANIES = "companies";
    public static final String FIELD_DEPARTMENT = "department";
    public static final String FIELD_JOB_TITLE = "job_title";
    public static final String FIELD_MANAGER = "manager";
    public static final String FIELD_MILEAGE = "mileage";
    public static final String FIELD_OFFICE_LOCATION = "office_location";
    public static final String FIELD_PROFESSION = "profession";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_STREET = "street";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_POSTAL_CODE = "postal_code";
    public static final String FIELD_COUNTRY = "country";
    public static final String FIELD_PO_BOX = "po_box";
    public static final String FIELD_EXTENDED_ADDRESS =
            "extended_address";
	
	// Details
	private long 		mId;
	private String mOwnerId;
	private String mBorqsId;
	private long 		mLastUpdate;
	private String 	mStatus;
	private short 		mPhotoType;
	private short 		mImportance;
	private short 		mSensitivity;
	private String		mSubject;
	private String 	mFolder;
	private String 	mAnniversary;
	private String 	mFirstName;
	private String 	mMiddleName;
	private String 	mLastName;

	private String 	mDisplayName;
	private String 	mBirthday;
	private String 	mBody;
	private String 	mCategories;
	private String 	mChildren;
	private String 	mHobbies;
	private String 	mInitials;
	private String 	mLanguages;
	private String 	mNickName;
	private String 	mSpouse;
	private String 	mSuffix;
	private String 	mTitle;
	private String 	mAssistant;
	private String 	mCompany;
	private String 	mDepartment;
	private String 	mJobTitle;
	private String 	mManager;
	private String 	mMileage;
	private String 	mOfficeLocation;
	private String 	mProfession;
	private String 	mCompanies;
	private String 	mGender;
	private String  mBorqsName;
    private String mBFirstName;
    private String mBMiddleName;
    private String mBLastName;

    private boolean mIsPrivate;

    private List<ContactItem> mXTags = new ArrayList<ContactItem>();

    
    //LOGIN 
    private List<String> mLoginPhones = new ArrayList<String>();
    private List<String> mLoginEmails = new ArrayList<String>();
	
	@SuppressWarnings("rawtypes")
	private List[] items = new List[5];
//	private Address[] addresses = new Address[3];
	private Photo photo;
	
	
//	// Addresses
//	public static final
// int CONTACT_HOME_ADDRESS_INDEX = 0;
//	public static final int CONTACT_OTHER_ADDRESS_INDEX = 1;
//	public static final int CONTACT_WORK_ADDRESS_INDEX = 2;
//
	// Contact Items
	public static final int CONTACT_ITEM_EMAILS_INDEX = 0;
	public static final int CONTACT_ITEM_TELEPHONES_INDEX = 1;
	public static final int CONTACT_ITEM_WEBPAGES_INDEX = 2;
    public static final int CONTACT_ITEM_IM_INDEX = 3;
    public static final int CONTACT_ITEM_ADDRESS_INDEX = 4;
	
	// Photo Types
	public static final short CONTACT_PHOTO_TYPE_EMPTY = 0;
	public static final short CONTACT_PHOTO_TYPE_IMAGE = 1;
	public static final short CONTACT_PHOTO_TYPE_URL = 2;
    
	public Contact() {
		mId = -1;
	}
	
	// 
	public long getId() {
		return mId;
	}

	public void setId(long id) {
        mId = id;
	}

	public String getOwnerId() {
		return mOwnerId;
	}

	public void setOwnerId(String owner_borqs_id) {
        mOwnerId = owner_borqs_id;
	}

	public String getBorqsId() {
		return mBorqsId;
	}

	public void setBorqsId(String borqsId) {
        mBorqsId = borqsId;
	}

	public long getLastUpdate() {
		return mLastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
        mLastUpdate = lastUpdate;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
        mStatus = status;
	}

	public short getPhotoType() {
		return mPhotoType;
	}

	public void setPhotoType(short photoType) {
        mPhotoType = photoType;
	}

	public short getImportance() {
		return mImportance;
	}

	public void setImportance(short importance) {
        mImportance = importance;
	}

	public short getSensitivity() {
		return mSensitivity;
	}

	public void setSensitivity(short sensitivity) {
        mSensitivity = sensitivity;
	}

	public String getSubject() {
		return mSubject;
	}

	public void setSubject(String subject) {
        mSubject = subject;
	}

	public String getFolder() {
		return mFolder;
	}

	public void setFolder(String folder) {
        mFolder = folder;
	}

	public String getAnniversary() {
		return mAnniversary;
	}

	public void setAnniversary(String anniversary) {
        mAnniversary = anniversary;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String firstName) {
        mFirstName = firstName;
	}

	public String getMiddleName() {
		return mMiddleName;
	}

	public void setMiddleName(String middleName) {
        mMiddleName = middleName;
	}

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String lastName) {
        mLastName = lastName;
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public void setDisplayName(String displayName) {
        mDisplayName = displayName;
	}

	public String getBirthday() {
		return mBirthday;
	}

	public void setBirthday(String birthday) {
        mBirthday = birthday;
	}

	public String getBody() {
		return mBody;
	}

	public void setBody(String body) {
        mBody = body;
	}

	public String getCategories() {
		return mCategories;
	}

	public void setCategories(String categories) {
        mCategories = categories;
	}

	public String getChildren() {
		return mChildren;
	}

	public void setChildren(String children) {
        mChildren = children;
	}

	public String getHobbies() {
		return mHobbies;
	}

	public void setHobbies(String hobbies) {
        mHobbies = hobbies;
	}

	public String getInitials() {
		return mInitials;
	}

	public void setInitials(String initials) {
        mInitials = initials;
	}

	public String getLanguages() {
		return mLanguages;
	}

	public void setLanguages(String languages) {
        mLanguages = languages;
	}

	public String getNickName() {
		return mNickName;
	}

	public void setNickName(String nickName) {
        mNickName = nickName;
	}

	public String getSpouse() {
		return mSpouse;
	}

	public void setSpouse(String spouse) {
        mSpouse = spouse;
	}

	public String getSuffix() {
		return mSuffix;
	}

	public void setSuffix(String suffix) {
        mSuffix = suffix;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
        mTitle = title;
	}

	public String getAssistant() {
		return mAssistant;
	}

	public void setAssistant(String assistant) {
        mAssistant = assistant;
	}

	public String getCompany() {
		return mCompany;
	}

	public void setCompany(String company) {
        mCompany = company;
	}

	public String getDepartment() {
		return mDepartment;
	}

	public void setDepartment(String department) {
        mDepartment = department;
	}

	public String getJobTitle() {
		return mJobTitle;
	}

	public void setJobTitle(String jobTitle) {
        mJobTitle = jobTitle;
	}

	public String getManager() {
		return mManager;
	}

	public void setManager(String manager) {
        mManager = manager;
	}

	public String getMileage() {
		return mMileage;
	}

	public void setMileage(String mileage) {
        mMileage = mileage;
	}

	public String getOfficeLocation() {
		return mOfficeLocation;
	}

	public void setOfficeLocation(String officeLocation) {
        mOfficeLocation = officeLocation;
	}

	public String getProfession() {
		return mProfession;
	}

	public void setProfession(String profession) {
        mProfession = profession;
	}

	public String getCompanies() {
		return mCompanies;
	}

	public void setCompanies(String companies) {
        mCompanies = companies;
	}

	public String getGender() {
		return mGender;
	}

	public void setGender(String gender) {
        mGender = gender;
	}
    
	/**
	 * Emails
	 */
	@SuppressWarnings("unchecked")
	public void setEmails(List<ContactItem> emails) {
		if(emails == null) {
			return;
		}
		
		items[CONTACT_ITEM_EMAILS_INDEX] = emails;
		
		for(ContactItem email : (List<ContactItem>)items[CONTACT_ITEM_EMAILS_INDEX]) {
			email.setContact(getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ContactItem> getEmails() {
		return items[CONTACT_ITEM_EMAILS_INDEX];
	}
	
	@SuppressWarnings("unchecked")
	public void addEmail(ContactItem email) {
		if(email == null) {
			return;
		}
		
		if(items[CONTACT_ITEM_EMAILS_INDEX] == null) {
			items[CONTACT_ITEM_EMAILS_INDEX] = new ArrayList<ContactItem>();
		}
		
		email.setContact(getId());
		items[CONTACT_ITEM_EMAILS_INDEX].add(email);
	}

    /**
     * IMs
     */
    public List<ContactItem> getIms(){
        return items[CONTACT_ITEM_IM_INDEX];
    }
    
    public void addIm(ContactItem im){
        if(im==null){
            return;
        }
        if(items[CONTACT_ITEM_IM_INDEX] == null){
            items[CONTACT_ITEM_IM_INDEX] = new ArrayList<ContactItem>(); 
        }
        im.setContact(getId());
        items[CONTACT_ITEM_IM_INDEX].add(im);
    }

    public void setIms(List<ContactItem> ims) {
        if(ims == null) {
            return;
        }

        items[CONTACT_ITEM_IM_INDEX] = ims;

        for(ContactItem im : (List<ContactItem>)items[CONTACT_ITEM_IM_INDEX]) {
            im.setContact(getId());
        }
    }
	
	/**
	 * Telephones
	 */
	@SuppressWarnings("unchecked")
	public void setTelephones(List<ContactItem> phones) {
		if(phones == null) {
			return;
		}
		
		items[CONTACT_ITEM_TELEPHONES_INDEX] = phones;
		
		for(ContactItem phone : (List<ContactItem>)items[CONTACT_ITEM_TELEPHONES_INDEX]) {
			phone.setContact(getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ContactItem> getTelephones() {
		return items[CONTACT_ITEM_TELEPHONES_INDEX];
	}
	
	@SuppressWarnings("unchecked")
	public void addTelephone(ContactItem phone) {
		if(phone == null) {
			return;
		}
		
		if(items[CONTACT_ITEM_TELEPHONES_INDEX] == null) {
			items[CONTACT_ITEM_TELEPHONES_INDEX] = new ArrayList<ContactItem>();
		}
		
		phone.setContact(getId());
		items[CONTACT_ITEM_TELEPHONES_INDEX].add(phone);
	}
	
	/**
	 * Webpages
	 */
	@SuppressWarnings("unchecked")
	public void setWebpages(List<ContactItem> pages) {
		if(pages == null) {
			return;
		}
		
		items[CONTACT_ITEM_WEBPAGES_INDEX] = pages;
		
		for(ContactItem page : (List<ContactItem>)items[CONTACT_ITEM_WEBPAGES_INDEX]) {
			page.setContact(getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ContactItem> getWebpages() {
		return items[CONTACT_ITEM_WEBPAGES_INDEX];
	}
	
	@SuppressWarnings("unchecked")
	public void addWebpage(ContactItem page) {
		if(page == null) {
			return;
		}
		
		if(items[CONTACT_ITEM_WEBPAGES_INDEX] == null) {
			items[CONTACT_ITEM_WEBPAGES_INDEX] = new ArrayList<ContactItem>();
		}
		
		page.setContact(getId());
		items[CONTACT_ITEM_WEBPAGES_INDEX].add(page);
	}

    public void setAddress(List<Address> addresses) {
        if(addresses == null) {
            return;
        }

        items[CONTACT_ITEM_ADDRESS_INDEX] = addresses;

        for(Address address : (List<Address>)items[CONTACT_ITEM_ADDRESS_INDEX]) {
            address.setContact(getId());
        }
    }
    
    public List<Address> getAddress(){
        return items[CONTACT_ITEM_ADDRESS_INDEX];
    }
	
//	/**
//	 * Addresses
//	 */
//	public void setHomeAddress(Address home) {
//		addresses[CONTACT_HOME_ADDRESS_INDEX] = home;
//		addresses[CONTACT_HOME_ADDRESS_INDEX].setContact(getId());
//		addresses[CONTACT_HOME_ADDRESS_INDEX].setType(Address.ADDRESS_TYPE_HOME);
//	}
//
//	public Address getHomeAddress() {
//		return addresses[CONTACT_HOME_ADDRESS_INDEX];
//	}
//
//	public void setOtherAddress(Address other) {
//		addresses[CONTACT_OTHER_ADDRESS_INDEX] = other;
//		addresses[CONTACT_OTHER_ADDRESS_INDEX].setContact(getId());
//		addresses[CONTACT_OTHER_ADDRESS_INDEX].setType(Address.ADDRESS_TYPE_OTHER);
//	}
//
//	public Address getOtherAddress() {
//		return addresses[CONTACT_OTHER_ADDRESS_INDEX];
//	}
//
//	public void setWorkAddress(Address work) {
//		addresses[CONTACT_WORK_ADDRESS_INDEX] = work;
//		addresses[CONTACT_WORK_ADDRESS_INDEX].setContact(getId());
//		addresses[CONTACT_WORK_ADDRESS_INDEX].setType(Address.ADDRESS_TYPE_WORK);
//	}
//
//	public Address getWorkAddress() {
//		return addresses[CONTACT_WORK_ADDRESS_INDEX];
//	}
	
	
	/**
	 * Photo
	 */
	public Photo getPhoto() {
		return photo;
	}
	public void setPhoto(Photo photo) {
        if(photo == null){
            return;
        }
		this.photo = photo;
		this.photo.contact = getId();
		
		if(photo.image != null) {
			setPhotoType(CONTACT_PHOTO_TYPE_IMAGE);
		} else if(photo.url != null) {
			setPhotoType(CONTACT_PHOTO_TYPE_URL);
		} else {
			setPhotoType(CONTACT_PHOTO_TYPE_EMPTY);
		}
	}
	
	public String getBorqsName() {
		return mBorqsName;
	}
	
	public void setBorqsName(String borqsName) {
        mBorqsName = borqsName;
	}
    
    public void addLoginEmail(String loginEmail){
        mLoginEmails.add(loginEmail);
    }

    public List<String> getLoginEmails(){
        return mLoginEmails;
    }
    
    public List<String> getLoginPhones(){
        return mLoginPhones;
    }

    public void addLoginPhone(String loginPhone){
        mLoginPhones.add(loginPhone);
    }

    public void addXTag(ContactItem item){
        mXTags.add(item);
    }
    
    public List<ContactItem> getXTags(){
        return mXTags;
    }

    public void setXTags(List<ContactItem> xtags) {
        if(xtags == null) {
            return;
        }

        mXTags = xtags;
    }

//    public void setPrivate(boolean isPrivate){
//        mIsPrivate = isPrivate;
//    }
//
//    public boolean isPrivate(){
//        return mIsPrivate;
//    }

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
}
