/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.account;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-2-26
 * Time: 下午12:50
 */
public final class ProfileField {
    //Account field
    public static final String PROFILE_PRIVACY = "profile_privacy";
    public static final String USER_ID = "user_id";
    public static final String PENDING_REQUEST = "pedding_requests";




    //time stamp for last update
    public static final String LAST_UPDATE_BASIC = "basic_updated_time";
    public static final String LAST_UPDATE_PROFILE = "profile_updated_time";
    public static final String LAST_UPDATE_CONTACT_INFO = "contact_info_updated_time";
    public static final String LAST_UPDATE_ADDRESS = "address_updated_time";

    
    //composted field name
    public static final String CONTACT_INFO = "contact_info";
    public static final String ADDRESS = "address";
    public static final String PHOTO = "image_url";

    //base info
    public static final String LOGIN_PHONE1 = "login_phone1";
    public static final String LOGIN_PHONE2 = "login_phone2";
    public static final String LOGIN_PHONE3 = "login_phone3";
    public static final String LOGIN_EMAIL1 = "login_email1";
    public static final String LOGIN_EMAIL2 = "login_email2";
    public static final String LOGIN_EMAIL3 = "login_email3";
    public static final String WEBPAGE = "domain_name";
    public static final String PROFESSION = "profession";
    public static final String OFFICE_LOCATION = "office_address";
    public static final String MIDDLENAME = "middle_name";
    public static final String LASTNAME = "last_name";
    public static final String LANGUAGES = "languages";
    public static final String JOB_TITLE = "job_title";
    public static final String HOBBIES = "interests";
    public static final String GENDER = "gender";
    public static final String FIRSTNAME = "first_name";
    public static final String DISPLAYNAME = "display_name";
    public static final String DEPARTMENT = "department";
    public static final String COMPANY = "company";
    public static final String BODY = "about_me";
    public static final String BIRTHDAY = "birthday";

    //address info
    public static final String ADDRESS_TYPE = "type";
    public static final String ADDRESS_COUNTRY = "country";
    public static final String ADDRESS_STATE = "state";
    public static final String ADDRESS_CITY = "city";
    public static final String ADDRESS_STREET = "street";
    public static final String ADDRESS_CODE = "postal_code";
    public static final String ADDRESS_PO_BOX = "po_box";
    public static final String ADDRESS_EXTENDED = "extended_address";

    //Contact info
    public static final String CONTACT_EMAIL_ADDRESS = "email_address";
    public static final String CONTACT_EMAIL_ADDRESS_2 = "email_2_address";
    public static final String CONTACT_EMAIL_ADDRESS_3 = "email_3_address";

    public static final String CONTACT_WORK_TEL = "business_telephone_number";
    public static final String CONTACT_WORK_TEL_2 = "business_2_telephone_number";
    public static final String CONTACT_WORK_TEL_3 = "business_3_telephone_number";

    public static final String CONTACT_MOBILE_TEL = "mobile_telephone_number";
    public static final String CONTACT_MOBILE_TEL_2 = "mobile_2_telephone_number";
    public static final String CONTACT_MOBILE_TEL_3 = "mobile_3_telephone_number";

    public static final String CONTACT_HOME_TEL = "home_telephone_number";
    public static final String CONTACT_HOME_TEL_2 = "home_2_telephone_number";
    public static final String CONTACT_HOME_TEL_3 = "home_3_telephone_number";

    public static final String CONTACT_WORK_FAX = "business_fax_number";
    public static final String CONTACT_HOME_FAX = "home_fax_number";
    public static final String CONTACT_OTHER_FAX = "other_fax_number";

    public static final String CONTACT_TELEX_NUMBER = "telex_number";
    public static final String CONTACT_RADIO_TEL = "radio_telephone_number";
    public static final String CONTACT_PAGER = "pager_number";
    public static final String CONTACT_OTHER_TEL = "other_telephone_number";
    public static final String CONTACT_COMPANY_MAIN_TEL = "company_main_telephone_number";
    public static final String CONTACT_ASSISTANT_TEL = "assistant_number";
    public static final String CONTACT_CALLBACK_TEL = "callback_number";
    public static final String CONTACT_CAR_TEL = "car_telephone_number";
    public static final String CONTACT_PRIMARY_TEL = "primary_telephone_number";


    public static final String CONTACT_IM_QQ = "im_qq";

    public static final String CONTACT_WEB_PAGE = "web_page";
    public static final String CONTACT_HOME_WEB_PAGE = "home_web_page";
    public static final String CONTACT_WORK_WEB_PAGE = "business_web_page";

    //type define
    public static final String TYPE_ACCOUNT_ADDRESS_HOME = "home";
    public static final String TYPE_ACCOUNT_ADDRESS_WORK = "work";
}
