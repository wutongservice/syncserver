/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.providers;

import com.borqs.sync.server.common.exception.DAOException;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.util.LogHelper;
import com.borqs.sync.server.common.util.Utility;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: b251
 * Date: 1/30/12
 * Time: 1:24 PM
 * Borqs project
 */
public class ContactProvider {
    protected static final String SQL_FIELD_ID = "id";
    protected static final String SQL_FIELD_USERID = "userid";
    protected static final String SQL_FIELD_BORQSID = "borqsid";
    protected static final String SQL_FIELD_ITEM_LAST_UPDATE = "last_update";
    protected static final String SQL_FIELD_STATUS = "status";
    protected static final String SQL_FIELD_PHOTO_TYPE = "photo_type";
    protected static final String SQL_FIELD_IMPORTANCE = "importance";
    protected static final String SQL_FIELD_SENSITIVITY = "sensitivity";
    protected static final String SQL_FIELD_SUBJECT = "subject";
    protected static final String SQL_FIELD_FOLDER = "folder";
    protected static final String SQL_FIELD_ANNIVERSARY = "anniversary";
    protected static final String SQL_FIELD_FIRST_NAME = "first_name";
    protected static final String SQL_FIELD_MIDDLE_NAME = "middle_name";
    protected static final String SQL_FIELD_LAST_NAME = "last_name";
    protected static final String SQL_FIELD_BFIRST_NAME = "bfirst_name";
    protected static final String SQL_FIELD_BMIDDLE_NAME = "bmiddle_name";
    protected static final String SQL_FIELD_BLAST_NAME = "blast_name";
    protected static final String SQL_FIELD_DISPLAY_NAME = "display_name";
    protected static final String SQL_FIELD_BIRTHDAY = "birthday";
    protected static final String SQL_FIELD_BODY = "body";
    protected static final String SQL_FIELD_CATEGORIES = "categories";
    protected static final String SQL_FIELD_CHILDREN = "children";
    protected static final String SQL_FIELD_HOBBIES = "hobbies";
    protected static final String SQL_FIELD_GENDER = "gender";
    protected static final String SQL_FIELD_INITIALS = "initials";
    protected static final String SQL_FIELD_LANGUAGES = "languages";
    protected static final String SQL_FIELD_NICKNAME = "nickname";
    protected static final String SQL_FIELD_SPOUSE = "spouse";
    protected static final String SQL_FIELD_SUFFIX = "suffix";
    protected static final String SQL_FIELD_TITLE = "title";
    protected static final String SQL_FIELD_ASSISTANT = "assistant";
    protected static final String SQL_FIELD_COMPANY = "company";
    protected static final String SQL_FIELD_COMPANIES = "companies";
    protected static final String SQL_FIELD_DEPARTMENT = "department";
    protected static final String SQL_FIELD_JOB_TITLE = "job_title";
    protected static final String SQL_FIELD_MANAGER = "manager";
    protected static final String SQL_FIELD_MILEAGE = "mileage";
    protected static final String SQL_FIELD_OFFICE_LOCATION = "office_location";
    protected static final String SQL_FIELD_PROFESSION = "profession";
    protected static final String SQL_FIELD_TYPE = "type";
    protected static final String SQL_FIELD_VALUE = "value";
    protected static final String SQL_FIELD_ISPRIVATE = "private";
    protected static final String SQL_FIELD_LAST_UPDATE = "last_update";
    protected static final String SQL_FIELD_STREET = "street";
    protected static final String SQL_FIELD_CITY = "city";
    protected static final String SQL_FIELD_STATE = "state";
    protected static final String SQL_FIELD_POSTAL_CODE = "postal_code";
    protected static final String SQL_FIELD_COUNTRY = "country";
    protected static final String SQL_FIELD_PO_BOX = "po_box";
    protected static final String SQL_FIELD_EXTENDED_ADDRESS =
            "extended_address";
    protected static final String SQL_FIELD_ADDRESS_ISPRIVATE = "private";
    protected static final String SQL_FIELD_ADDRESS_LAST_UPDATE = "last_update";

    protected static final int SQL_ANNIVERSARY_DIM = 255;
    protected static final int SQL_ASSISTANT_DIM = 255;
    protected static final int SQL_BIRTHDAY_DIM = 255;
    protected static final int SQL_CATEGORIES_DIM = 255;
    protected static final int SQL_CHILDREN_DIM = 255;
    protected static final int SQL_CITY_DIM = 255;
    protected static final int SQL_COMPANY_DIM = 255;
    protected static final int SQL_COMPANIES_DIM = 255;
    protected static final int SQL_COUNTRY_DIM = 255;
    protected static final int SQL_DEPARTMENT_DIM = 255;
    protected static final int SQL_DISPLAYNAME_DIM = 255;
    protected static final int SQL_EMAIL_DIM = 255;
    protected static final int SQL_FIRSTNAME_DIM = 255;
    protected static final int SQL_FOLDER_DIM = 255;
    protected static final int SQL_GENDER_DIM = 1;
    protected static final int SQL_HOBBIES_DIM = 255;
    protected static final int SQL_INITIALS_DIM = 255;
    protected static final int SQL_LABEL_DIM = 255; // @todo Enough?
    protected static final int SQL_LANGUAGES_DIM = 255;
    protected static final int SQL_LASTNAME_DIM = 255;
    protected static final int SQL_MANAGER_DIM = 255;
    protected static final int SQL_MIDDLENAME_DIM = 255;
    protected static final int SQL_MILEAGE_DIM = 255;
    protected static final int SQL_NICKNAME_DIM = 255;
    protected static final int SQL_BODY_DIM = 4096;
    protected static final int SQL_OFFICELOCATION_DIM = 255;
    protected static final int SQL_PHONE_DIM = 255;
    protected static final int SQL_POSTALCODE_DIM = 255;
    protected static final int SQL_POSTALOFFICEADDRESS_DIM = 255;
    protected static final int SQL_EXTENDEDADDRESS_DIM = 255;
    protected static final int SQL_PROFESSION_DIM = 255;
    protected static final int SQL_TITLE_DIM = 255;
    protected static final int SQL_SPOUSE_DIM = 255;
    protected static final int SQL_STATE_DIM = 255;
    protected static final int SQL_STREET_DIM = 255;
    protected static final int SQL_SUBJECT_DIM = 255;
    protected static final int SQL_SUFFIX_DIM = 255;
    protected static final int SQL_JOBTITLE_DIM = 255;
    protected static final int SQL_WEBPAGE_DIM = 255;
    protected static final int SQL_BFIRSTNAME_DIM = 255;
    protected static final int SQL_BMIDDLENAME_DIM = 255;
    protected static final int SQL_BLASTNAME_DIM = 255;

    private static final String SQL_INSERT_INTO_BORQS_PIM_CONTACT =
            "INSERT INTO borqs_pim_contact "
                    + "(userid, borqsid, last_update, status, photo_type, importance, sensitivity, "
                    + "subject, folder, anniversary, first_name, middle_name, "
                    + "last_name,bfirst_name,bmiddle_name,blast_name, display_name, birthday, body, "
                    + "categories, children, "
                    + "hobbies, initials, languages, nickname, spouse, suffix, title, "
                    + "assistant, company, department, job_title, manager, mileage, "
                    + "office_location, profession, companies, gender ) "
                    + "VALUES "
                    + "(?, ?, ?, ?, ?, ?, ?, ? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?) ";

    private static final String SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM =
            "INSERT INTO borqs_pim_contact_item "
                    + "(contact, type, value,private,last_update) "
                    + "VALUES (?, ?, ?,?,?) ";

    private static final String SQL_INSERT_INTO_BORQS_PIM_CONTACT_PHOTO =
            "INSERT INTO borqs_pim_contact_photo (contact, type, photo, url) VALUES (?,?,?,?)";

    private static final String SQL_GET_CONTACT_ID_BY_ID_AND_USER_ID =
            "select id from borqs_pim_contact where id=? and userid=?";

    private static final String SQL_GET_CONTACT_ID_BY_USER_ID_AND_BORQS_ID =
            "select id from borqs_pim_contact where userid=? and borqsid=? and status in ('N','U')";

    private static final String SQL_GET_CONTACT_ID_BY_USER_ID_AND_ID =
            "select * from borqs_pim_contact where id=?";
    private static final String SQL_GET_CONTACT_ID_BY_USER_ID=
            "select * from borqs_pim_contact where userid=?";

    private static final String SQL_GET_BORQS_PIM_CONTACT_ITEM_BY_ID =
            "SELECT type, value,private,last_update FROM borqs_pim_contact_item WHERE contact = ? "
                    + "ORDER BY type";

    private static final String SQL_GET_BORQS_PIM_ADDRESS_BY_ID =
            "SELECT type, street, city, state, postal_code, country, po_box, " +
                    "extended_address,private,last_update FROM borqs_pim_address WHERE contact = ? ";

    private static final String SQL_SELECT_FROM_BORQS_PIM_CONTACT_PHOTO =
            "SELECT p.contact, p.type, p.photo, p.url FROM borqs_pim_contact c, " +
                    "borqs_pim_contact_photo p where c.id = ?  and c.userid = ? and " +
                    "c.status != 'D' and c.id = p.contact";

    private static final String SQL_UPDATE_CONTACT_TO_DELETE =
            "UPDATE borqs_pim_contact SET status = 'D', last_update = ? "
                    + "WHERE id = ? AND userid = ? ";

    private static final String SQL_UPDATE_CONTACT_TO_DELETE_BY_ID =
            "UPDATE borqs_pim_contact SET status = 'D', last_update = ? "
                    + "WHERE id = ?";
    private static final String SQL_UPDATE_CONTACT_TO_BATCH_DELETE_BY_ID = 
    		"UPDATE borqs_pim_contact SET status = 'D', last_update = ? WHERE ";

    private static final String SQL_UPDATE_ALL_CONTACT_TO_DELETE_BY_USER_ID =
            "UPDATE borqs_pim_contact SET status = 'D', last_update = ? "
                    + "WHERE userid = ? and status != 'D' ";

    private static final String SQL_EQUALS_QUESTIONMARK = " = ?";
    private static final String SQL_EQUALS_QUESTIONMARK_COMMA = " = ?, ";

    private static final String SQL_QUERY_IS_BORQS_NAME_SAME_WITH_FIRST_NAME_BY_ID =
            "select id from borqs_pim_contact where id=? and first_name in " +
                    "(select value from borqs_pim_contact_item where contact=? and type=?)";

    private static final String SQL_UPDATE_BORQS_PIM_CONTACT_BEGIN =
            "UPDATE borqs_pim_contact SET ";

    private static final String SQL_UPDATE_BORQS_PIM_CONTACT_END =
            " WHERE id =?";

    private static final String SQL_FILTER_BY_CONTACT_TYPE =
            "WHERE contact = ? AND type = ? ";

    private static final String SQL_QUERY_IF_IN_CONTACT_ITEM =
            "SELECT contact FROM borqs_pim_contact_item "
                    + SQL_FILTER_BY_CONTACT_TYPE;

    private static final String SQL_UPDATE_BORQS_PIM_CONTACT_ITEM =
            "UPDATE borqs_pim_contact_item SET value = ?,private=?,last_update=? "
                    + SQL_FILTER_BY_CONTACT_TYPE;

    private static final String SQL_DELETE_BORQS_PIM_CONTACT_ITEM =
            "DELETE FROM borqs_pim_contact_item "
                    + SQL_FILTER_BY_CONTACT_TYPE;
    private static final String SQL_SELECT_FROM_BORQS_PIM_ADDRESS =
            "SELECT contact FROM borqs_pim_address "
                    + SQL_FILTER_BY_CONTACT_TYPE;
    private static final String SQL_INSERT_INTO_BORQS_PIM_ADDRESS =
            "INSERT INTO borqs_pim_address "
                    + "(contact, type, street, city, state, postal_code, country, "
                    + "po_box, extended_address,private,last_update) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?) ";

    private static final String SQL_UPDATE_BORQS_PIM_ADDRESS_BEGIN =
            "UPDATE borqs_pim_address SET ";
    private static final String SQL_UPDATE_BORQS_PIM_ADDRESS_END =
            " " + SQL_FILTER_BY_CONTACT_TYPE;

    private static final String SQL_DELETE_BORQS_PIM_CONTACT_PHOTO =
            "DELETE FROM borqs_pim_contact_photo WHERE contact = ?";

    private static final String SQL_UPDATE_BORQS_PIM_CONTACT_PHOTO =
            "UPDATE borqs_pim_contact_photo SET type = ?, url = ?, photo = ? where contact = ?";

    private static final String SQL_UPDATE_CONTACT_LAST_UPDATE =
            "UPDATE borqs_pim_contact SET "+ SQL_FIELD_LAST_UPDATE +" = ? WHERE id = ?";

    private static final String SQL_DELETE_PHONE_ITEMS =
            "delete from borqs_pim_contact_item where contact=? and type in (" +
                    ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER + "," +
                    ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER + ")";

    private static final String SQL_DELETE_EMAIL_ITEMS =
            "delete from borqs_pim_contact_item where contact=? and type in (" +
                    ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS + "," +
                    ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS + "," +
                    ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS + "," +
                    ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER + ")";
    
    private static final String SQL_DELETE_WEB_ITEMS =
            "delete from borqs_pim_contact_item where contact=? and type in (" +
                    ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE + "," +
                    ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE + "," +
                    ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE + ")";

    private static final String SQL_DELETE_ADDRESS =
            "delete from borqs_pim_address where contact=? and type in (" +
                    Address.ADDRESS_TYPE_WORK + "," +
                    Address.ADDRESS_TYPE_HOME + "," +
                    Address.ADDRESS_TYPE_OTHER + ")";
    
    private static final String SQL_DELETE_ALL_SYNC_UPLOAD_ITEMS = "delete from borqs_pim_contact_item where contact=? and "
            + " type != " + ContactItem.TYPE_X_TAG_GROUP
            + " and type != " + ContactItem.TYPE_X_TAG_BORQS_NAME;
    
    private static final String SQL_DELETE_GROUP_ITEMS = "delete from borqs_pim_contact_item where" +
            " contact=? and type=" + ContactItem.TYPE_X_TAG_GROUP;
    
    private static final String SQL_UPDATE_CONTACT_AS_PRIVATE = "update borqs_pim_contact set status='U' ,borqsid=?,last_update=? where id=?";
    private static final String SQL_UPDATE_CONTACT_ITEM_AS_PRIVATE = "update borqs_pim_contact_item set private=true ,last_update=? where contact=? ";
    private static final String SQL_REMOVE_CONTACT_GROUP = "delete from borqs_pim_contact_item where contact=? and type in (?,?) " ;

    private Context mContext;
    private Logger mLogger;

    public ContactProvider(Context context){
        mContext = context;
    }
    
    public void useLogger(Logger logger){
        mLogger = logger;
    }

    /**
     * delete a item from SyncML database
     *
     * @param contactId - item to be deleted
     * @return
     */
    public boolean deleteItem(long contactId) {
        return deleteItemWithTimestamp(contactId, System.currentTimeMillis());
    }

    public boolean deleteItemWithTimestamp(long contactId, long timestamp){
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(SQL_UPDATE_CONTACT_TO_DELETE_BY_ID);

            if(timestamp <= 0){
                timestamp = System.currentTimeMillis();
            }

            ps.setLong(1, timestamp);
            ps.setLong(2, contactId);
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, null);
        }

        return false;
    }
    
    /**
     * batch delete a item from SyncML database
     * @param contactId
     * @param timestamp
     * @return
     */
    public boolean batchDeleteItemWithTimestamp(String contactId, long timestamp){
    	if(null == contactId || "".equals(contactId)) return true;
    	
        Connection conn = null;
        PreparedStatement ps = null;

        try {
        	String[] ids = contactId.split(",");
        	StringBuffer sb = new StringBuffer();
        	for(String id : ids) {
        		if(sb.length()>0) {
        			sb.append(" or ");
        		}
        		sb.append("id=").append(id);
        	}
        	
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(SQL_UPDATE_CONTACT_TO_BATCH_DELETE_BY_ID+sb.toString());

            if(timestamp <= 0){
                timestamp = System.currentTimeMillis();
            }

            ps.setLong(1, timestamp);
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, null);
        }

        return false;
    }
    
    public long findContact(String userId, String borqsId){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            conn = mContext.getSqlConnection();

            // Find the record's id
            ps = conn.prepareStatement(SQL_GET_CONTACT_ID_BY_USER_ID_AND_BORQS_ID);
            ps.setString(1, userId);
            ps.setString(2, borqsId);
            rs = ps.executeQuery();

            if (rs.first()) {
                return rs.getLong("id");
            }
        } catch (Exception e){
            LogHelper.logW(mLogger, "Exception: " + e);
        } finally{
            DBUtility.close(conn, ps, rs);
        }

        return -1;
    }

    /**
     * updatea a item to SyncML database
     *
     * @param contactId - item to be modified
     * @param c - new data
     * @return
     */
    public boolean updateItem(long contactId, Contact c) {
        if (c == null) {
            return false;
        }
        long original_last_update_time = c.getLastUpdate();
        if(original_last_update_time <= 0){
            original_last_update_time = System.currentTimeMillis();
        }
        c.setLastUpdate(original_last_update_time);

        String userId = c.getOwnerId();
        String borqsId = c.getBorqsId();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = mContext.getSqlConnection();
            conn.setAutoCommit(false);

            String mileage = c.getMileage();
            String subject = c.getSubject();
            String folder = c.getFolder();
            String anniversary = c.getAnniversary();
            String firstName = c.getFirstName();
            String middleName = c.getMiddleName();
            String lastName = c.getLastName();
            String displayName = c.getDisplayName();
            String birthday = c.getBirthday();
            String body = c.getBody();
            String categories = c.getCategories();
            String gender = c.getGender();
            String hobbies = c.getHobbies();
            String initials = c.getInitials();
            String languages = c.getLanguages();
            String nickName = c.getNickName();
            String spouse = c.getSpouse();
            String suffix = c.getSuffix();
            String assistant = c.getAssistant();
            String officeLocation = c.getOfficeLocation();
            String company = c.getCompany();
            String companies = c.getCompanies();
            String department = c.getDepartment();
            String jobTitle = c.getJobTitle();
            String manager = c.getManager();
            String profession = c.getProfession();
            String children = c.getChildren();
            String title = c.getTitle();
            String borqsName = c.getBorqsName();
            String bFirstName = c.getBFirstName();
            String bMiddleName = c.getBMiddleName();
            String bLastName = c.getBLastName();

            List<ContactItem> emails = c.getEmails();
            List<ContactItem> phones = c.getTelephones();
            List<ContactItem> webpages = c.getWebpages();
            List<Address> addresses = c.getAddress();

//            Address[] addresses = new Address[3];
//            addresses[0] = c.getHomeAddress();
//            addresses[1] = c.getOtherAddress();
//            addresses[2] = c.getWorkAddress();

            StringBuilder sqlUpdateContact = new StringBuilder();
            sqlUpdateContact.append(SQL_UPDATE_BORQS_PIM_CONTACT_BEGIN);
            sqlUpdateContact.append(SQL_FIELD_LAST_UPDATE);
            sqlUpdateContact.append(SQL_EQUALS_QUESTIONMARK_COMMA);

            // Photo strategy
            Photo photo = c.getPhoto();
            boolean photoToRemove    = false;
            boolean photoToSet       = false;
            boolean photoNothingToDo = false;
            short photoType = 0;

            if(photo == null) {
                photoNothingToDo = true;
                photoToRemove = true;
            } else {
                if (photo.getImage() != null) {
                    photoType  = Photo.PHOTO_IMAGE;
                    photoToSet = true;
                } else if (photo.getUrl() != null) {
                    photoType  = Photo.PHOTO_URL;
                    photoToSet = true;
                } else {
                    photoToRemove = true;
                    photoType  = Photo.EMPTY_PHOTO;
                }

                sqlUpdateContact.append(SQL_FIELD_PHOTO_TYPE)
                        .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            sqlUpdateContact.append(SQL_FIELD_IMPORTANCE
                    + SQL_EQUALS_QUESTIONMARK_COMMA);

            sqlUpdateContact.append(SQL_FIELD_SENSITIVITY
                    + SQL_EQUALS_QUESTIONMARK_COMMA);

            if (!Utility.isEmpty(borqsId)) {
                sqlUpdateContact.append(SQL_FIELD_BORQSID
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
//            if (subject != null) {
                sqlUpdateContact.append(SQL_FIELD_SUBJECT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (folder != null) {
                sqlUpdateContact.append(SQL_FIELD_FOLDER
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (anniversary != null) {
                sqlUpdateContact.append(SQL_FIELD_ANNIVERSARY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (firstName != null) {
                sqlUpdateContact.append(SQL_FIELD_FIRST_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (middleName != null) {
                sqlUpdateContact.append(SQL_FIELD_MIDDLE_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (lastName != null) {
                sqlUpdateContact.append(SQL_FIELD_LAST_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
            //            if (firstName != null) {
            //if the contact is private ,we don't handle bname and display name
            //bname and display name a one way sync from server to client
                sqlUpdateContact.append(SQL_FIELD_BFIRST_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
                sqlUpdateContact.append(SQL_FIELD_BMIDDLE_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
                sqlUpdateContact.append(SQL_FIELD_BLAST_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
                sqlUpdateContact.append(SQL_FIELD_DISPLAY_NAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            if (birthday != null) {
                sqlUpdateContact.append(SQL_FIELD_BIRTHDAY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (body != null) {
                sqlUpdateContact.append(SQL_FIELD_BODY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (categories != null) {
                sqlUpdateContact.append(SQL_FIELD_CATEGORIES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (children != null) {
                sqlUpdateContact.append(SQL_FIELD_CHILDREN
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (hobbies != null) {
                sqlUpdateContact.append(SQL_FIELD_HOBBIES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (initials != null) {
                sqlUpdateContact.append(SQL_FIELD_INITIALS
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (languages != null) {
                sqlUpdateContact.append(SQL_FIELD_LANGUAGES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (nickName != null) {
                sqlUpdateContact.append(SQL_FIELD_NICKNAME
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (spouse != null) {
                sqlUpdateContact.append(SQL_FIELD_SPOUSE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (suffix != null) {
                sqlUpdateContact.append(SQL_FIELD_SUFFIX
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (title != null) {
                sqlUpdateContact.append(SQL_FIELD_TITLE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (assistant != null) {
                sqlUpdateContact.append(SQL_FIELD_ASSISTANT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (company != null) {
                sqlUpdateContact.append(SQL_FIELD_COMPANY
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (department != null) {
                sqlUpdateContact.append(SQL_FIELD_DEPARTMENT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (jobTitle != null) {
                sqlUpdateContact.append(SQL_FIELD_JOB_TITLE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (manager != null) {
                sqlUpdateContact.append(SQL_FIELD_MANAGER
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (mileage != null) {
                sqlUpdateContact.append(SQL_FIELD_MILEAGE
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (officeLocation != null) {
                sqlUpdateContact.append(SQL_FIELD_OFFICE_LOCATION
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (profession != null) {
                sqlUpdateContact.append(SQL_FIELD_PROFESSION
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (companies != null) {
                sqlUpdateContact.append(SQL_FIELD_COMPANIES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }
//            if (gender != null) {
                sqlUpdateContact.append(SQL_FIELD_GENDER
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
//            }

            sqlUpdateContact.append(SQL_FIELD_STATUS);
            sqlUpdateContact.append(SQL_EQUALS_QUESTIONMARK);
            sqlUpdateContact.append(SQL_UPDATE_BORQS_PIM_CONTACT_END);

            ps = conn.prepareStatement(sqlUpdateContact.toString());

            int k = 1;

            // Last update, use original last_update time
//			ps.setLong(k++, System.currentTimeMillis());
            ps.setLong(k++, original_last_update_time);

            // Photo type
            if(!photoNothingToDo) {
                ps.setShort(k++, photoType);
            }

            ps.setShort(k++, c.getImportance());
            ps.setShort(k++, c.getSensitivity());

            if (!Utility.isEmpty(borqsId)) {
                ps.setString(k++, borqsId);
            }

            if (subject != null) {
                if (subject.length() > SQL_SUBJECT_DIM) {
                    subject = subject.substring(0, SQL_SUBJECT_DIM);
                }
                ps.setString(k++, subject);
            }else{
                ps.setString(k++, new String());
            }

            if (folder != null) {
                if (folder.length() > SQL_FOLDER_DIM) {
                    folder = folder.substring(0, SQL_FOLDER_DIM);
                }
                ps.setString(k++, folder);
            }else{
                ps.setString(k++, new String());
            }

            if (anniversary != null) {
                if (anniversary.length() > SQL_ANNIVERSARY_DIM) {
                    anniversary = anniversary.substring(0, SQL_ANNIVERSARY_DIM);
                }
                ps.setString(k++, anniversary);
            }else{
                ps.setString(k++, new String());
            }

            if (firstName != null) {
                if (firstName.length() > SQL_FIRSTNAME_DIM) {
                    firstName = firstName.substring(0, SQL_FIRSTNAME_DIM);
                }
                ps.setString(k++, firstName);
            }else{
                ps.setString(k++, new String());
            }

            if (middleName != null) {
                if (middleName.length() > SQL_MIDDLENAME_DIM) {
                    middleName = middleName.substring(0, SQL_MIDDLENAME_DIM);
                }
                ps.setString(k++, middleName);
            }else{
                ps.setString(k++, new String());
            }

            if (lastName != null) {
                if (lastName.length() > SQL_LASTNAME_DIM) {
                    lastName = lastName.substring(0, SQL_LASTNAME_DIM);
                }
                ps.setString(k++, lastName);
            }else{
                ps.setString(k++, new String());
            }

            if (bFirstName != null) {
                if (bFirstName.length() > SQL_BFIRSTNAME_DIM) {
                    bFirstName = bFirstName.substring(0, SQL_BFIRSTNAME_DIM);
                }
                ps.setString(k++, bFirstName);
            }else{
                ps.setString(k++, new String());
            }

            if (bMiddleName != null) {
                if (bMiddleName.length() > SQL_BMIDDLENAME_DIM) {
                    bMiddleName = bMiddleName.substring(0, SQL_BMIDDLENAME_DIM);
                }
                ps.setString(k++, bMiddleName);
            }else{
                ps.setString(k++, new String());
            }

            if (bLastName != null) {
                if (bLastName.length() > SQL_BLASTNAME_DIM) {
                    bLastName = bLastName.substring(0, SQL_BLASTNAME_DIM);
                }
                ps.setString(k++, bLastName);
            }else{
                ps.setString(k++, new String());
            }

            if (displayName != null) {
                if (displayName.length() > SQL_DISPLAYNAME_DIM) {
                    displayName = displayName.substring(0, SQL_DISPLAYNAME_DIM);
                }
                ps.setString(k++, displayName);
            } else{
                ps.setString(k++, new String());
            }

            if (birthday != null) {
                if (birthday.length() > SQL_BIRTHDAY_DIM) {
                    birthday = birthday.substring(0, SQL_BIRTHDAY_DIM);
                }
                ps.setString(k++, birthday);
            }else{
                ps.setString(k++, new String());
            }

            if (body != null) {
                if (body.length() > SQL_BODY_DIM) {
                    body = body.substring(0, SQL_BODY_DIM);
                }
                ps.setString(k++, body);
            }else{
                ps.setString(k++, new String());
            }

            if (categories != null) {
                if (categories.length() > SQL_CATEGORIES_DIM) {
                    categories = categories.substring(0, SQL_CATEGORIES_DIM);
                }
                ps.setString(k++, categories);
            }else{
                ps.setString(k++, new String());
            }

            if (children != null) {
                if (children.length() > SQL_CHILDREN_DIM) {
                    children = children.substring(0, SQL_CHILDREN_DIM);
                }
                ps.setString(k++, children);
            }else{
                ps.setString(k++, new String());
            }

            if (hobbies != null) {
                if (hobbies.length() > SQL_HOBBIES_DIM) {
                    hobbies = hobbies.substring(0, SQL_HOBBIES_DIM);
                }
                ps.setString(k++, hobbies);
            }else{
                ps.setString(k++, new String());
            }

            if (initials != null) {
                if (initials.length() > SQL_INITIALS_DIM) {
                    initials = initials.substring(0, SQL_INITIALS_DIM);
                }
                ps.setString(k++, initials);
            }else{
                ps.setString(k++, new String());
            }

            if (languages != null) {
                if (languages.length() > SQL_LANGUAGES_DIM) {
                    languages = initials.substring(0, SQL_LANGUAGES_DIM);
                }
                ps.setString(k++, languages);
            }else{
                ps.setString(k++, new String());
            }

            if (nickName != null) {
                if (nickName.length() > SQL_NICKNAME_DIM) {
                    nickName = nickName.substring(0, SQL_NICKNAME_DIM);
                }
                ps.setString(k++, nickName);
            }else{
                ps.setString(k++, new String());
            }

            if (spouse != null) {
                if (spouse.length() > SQL_SPOUSE_DIM) {
                    spouse = spouse.substring(0, SQL_SPOUSE_DIM);
                }
                ps.setString(k++, spouse);
            }else{
                ps.setString(k++, new String());
            }

            if (suffix != null) {
                if (suffix.length() > SQL_SUFFIX_DIM) {
                    suffix = suffix.substring(0, SQL_SUFFIX_DIM);
                }
                ps.setString(k++, suffix);
            } else{
                ps.setString(k++, new String());
            }

            if (title != null) {
                if (title.length() > SQL_TITLE_DIM) {
                    title = title.substring(0, SQL_TITLE_DIM);
                }
                ps.setString(k++, title);
            }else{
                ps.setString(k++, new String());
            }

            if (assistant != null) {
                if (assistant.length() > SQL_ASSISTANT_DIM) {
                    assistant = assistant.substring(0, SQL_ASSISTANT_DIM);
                }
                ps.setString(k++, assistant);
            }else{
                ps.setString(k++, new String());
            }

            if (company != null) {
                if (company.length() > SQL_COMPANY_DIM) {
                    company = company.substring(0, SQL_COMPANY_DIM);
                }
                ps.setString(k++, company);
            }else{
                ps.setString(k++, new String());
            }

            if (department != null) {
                if (department.length() > SQL_DEPARTMENT_DIM) {
                    department = department.substring(0, SQL_DEPARTMENT_DIM);
                }
                ps.setString(k++, department);
            }else{
                ps.setString(k++, new String());
            }

            if (jobTitle != null) {
                if (jobTitle.length() > SQL_JOBTITLE_DIM) {
                    jobTitle = jobTitle.substring(0, SQL_JOBTITLE_DIM);
                }
                ps.setString(k++, jobTitle);
            }else{
                ps.setString(k++, new String());
            }

            if (manager != null) {
                if (manager.length() > SQL_MANAGER_DIM) {
                    manager = manager.substring(0, SQL_MANAGER_DIM);
                }
                ps.setString(k++, manager);
            }else{
                ps.setString(k++, new String());
            }

            if (mileage != null) {
                if (mileage.length() > SQL_MILEAGE_DIM) {
                    mileage = mileage.substring(0, SQL_MILEAGE_DIM);
                }
                ps.setString(k++, mileage);
            }else{
                ps.setString(k++, new String());
            }

            if (officeLocation != null) {
                if (officeLocation.length() > SQL_OFFICELOCATION_DIM) {
                    officeLocation = officeLocation.substring(0,
                            SQL_OFFICELOCATION_DIM);
                }
                ps.setString(k++, officeLocation);
            }else{
                ps.setString(k++, new String());
            }

            if (profession != null) {
                if (profession.length() > SQL_PROFESSION_DIM) {
                    profession = profession.substring(0, SQL_PROFESSION_DIM);
                }
                ps.setString(k++, profession);
            }else{
                ps.setString(k++, new String());
            }

            if (companies != null) {
                if (companies.length() > SQL_COMPANIES_DIM) {
                    companies = companies.substring(0, SQL_COMPANIES_DIM);
                }
                ps.setString(k++, companies);
            } else{
                ps.setString(k++, new String());
            }

            if (gender != null) {
                if (gender.length() > SQL_GENDER_DIM) {
                    gender = gender.substring(0, SQL_GENDER_DIM);
                }
                ps.setString(k++, gender);
            }else{
                ps.setString(k++, new String());
            }

            ps.setString(k++, String.valueOf('U'));

            ps.setLong(k++, contactId);

            ps.executeUpdate();
            DBUtility.close(null, ps, null);


            //delete all except BorqsName,Group and then insert others
            ps = conn.prepareStatement(SQL_DELETE_ALL_SYNC_UPLOAD_ITEMS);
            ps.setLong(1, contactId);
            ps.executeUpdate();
            DBUtility.close(null, ps, null);


            // borqs name
            LogHelper.logD(mLogger, ">>>>>>>>>>>>>>(S) Begin to update borqs name <<<<<<<<<<<<<");

            if (!Utility.isEmpty(borqsName)) {

                LogHelper.logD(mLogger, ">>>>>>>>>>>>>>(S) Borqs Name: " + borqsName + " <<<<<<<<<<<<<");

                ps = conn.prepareStatement(SQL_QUERY_IF_IN_CONTACT_ITEM);
                ps.setLong(1, contactId);
                ps.setInt(2, ContactItem.TYPE_X_TAG_BORQS_NAME);
                rs = ps.executeQuery();

                boolean findBorqsName = rs.first();
                DBUtility.close(null, ps, rs);

                if (!findBorqsName) {
                    LogHelper.logD(mLogger, ">>>>>>>>>>>>>>(S) No item for borqs name. Add one <<<<<<<<<<<<<");

                    ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                    ps.setLong(1, contactId);
                    ps.setInt(2, ContactItem.TYPE_X_TAG_BORQS_NAME);
                    ps.setString(3, borqsName);
                    ps.setBoolean(4,false);//borqsname can not be update by client
                    ps.setLong(5,c.getLastUpdate());

                    ps.executeUpdate();
                    DBUtility.close(null, ps, null);
                } else {
                    LogHelper.logD(mLogger, ">>>>>>>>>>>>>>(S) Update existed borqs name to new <<<<<<<<<<<<<");

                    ps = conn.prepareStatement(SQL_UPDATE_BORQS_PIM_CONTACT_ITEM);
                    ps.setString(1, borqsName);
                    ps.setBoolean(2,false);//borqsname can not be update by client
                    ps.setLong(3,c.getLastUpdate());
                    ps.setLong(4, contactId);
                    ps.setInt(5, ContactItem.TYPE_X_TAG_BORQS_NAME);
                    ps.executeUpdate();
                    DBUtility.close(null, ps, null);
                }
            }

            int type;
            PreparedStatement ps1;

            //emails
//            ps1 = conn.prepareStatement(SQL_DELETE_EMAIL_ITEMS);
//            ps1.setLong(1, contactId);
//            ps1.executeUpdate();
//            DBUtility.close(null, ps1, null);

            if(emails !=null && emails.size() > 0) {
                for(ContactItem email : emails) {
                    type = email.getType();
                    if(type != ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS
                            && type != ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS
                            && type != ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS
                            && type != ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER) {
                        continue;
                    }

                    String emailValue = email.getValue();
                    emailValue = StringUtils.left(emailValue, SQL_EMAIL_DIM);

                    if (!Utility.isEmpty(emailValue)) {
                        ps1 = conn.prepareStatement(
                                SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);

                        ps1.setLong(1, contactId);
                        ps1.setInt(2, type);
                        ps1.setString(3, emailValue);
                        ps1.setBoolean(4,email.isPrivate());
                        ps1.setLong(5,email.getLastUpdate());

                        ps1.executeUpdate();
                        DBUtility.close(null, ps1, null);
                    }
                }
            }

            // Phone
            // Delete current phones
//            ps1 = conn.prepareStatement(SQL_DELETE_PHONE_ITEMS);
//            ps1.setLong(1, contactId);
//            ps1.executeUpdate();
//            DBUtility.close(null, ps1, null);

            if(phones !=null && phones.size() > 0) {
                for(ContactItem phone : phones) {
                    type = phone.getType();
                    if(type != ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER
                            && type != ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER) {
                        continue;
                    }

                    String phoneValue = phone.getValue();
                    phoneValue = StringUtils.left(phoneValue, SQL_PHONE_DIM);

                    if (!Utility.isEmpty(phoneValue)) {
                        ps1 = conn.prepareStatement(
                                SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);

                        ps1.setLong(1, contactId);
                        ps1.setInt(2, type);
                        ps1.setString(3, phoneValue);
                        ps1.setBoolean(4,phone.isPrivate());
                        ps1.setLong(5,phone.getLastUpdate());

                        ps1.executeUpdate();
                        DBUtility.close(null, ps1, null);
                    }
                }
            }

            if(webpages !=null && webpages.size() > 0) {
                for(ContactItem webpage : webpages) {
                    type = webpage.getType();

                    if(type != ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE
                            && type != ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE
                            && type != ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE) {
                        continue;
                    }


                    String webpageValue = webpage.getValue();
                    webpageValue = StringUtils.left(webpageValue, SQL_WEBPAGE_DIM);

                    if (!Utility.isEmpty(webpageValue)) {
                        ps1 = conn.prepareStatement(
                                SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                        ps1.setLong(1, contactId);
                        ps1.setInt(2, type);
                        ps1.setString(3, webpageValue);
                        ps1.setBoolean(4,webpage.isPrivate());
                        ps1.setLong(5,webpage.getLastUpdate());

                        ps1.executeUpdate();
                        DBUtility.close(null, ps1, null);
                    }
                }
            }

            //delete address
            ps1 = conn.prepareStatement(SQL_DELETE_ADDRESS);
            ps1.setLong(1, contactId);
            ps1.executeUpdate();
            DBUtility.close(null, ps1, null);

            // Addresses
            if(addresses !=null && addresses.size() > 0) {
                for(Address a : addresses) {
                    if(a == null) {
                        continue;
                    }
                    String street = replaceNewLine(StringUtils.left(a.getStreet(), SQL_STREET_DIM));
                    String city = StringUtils.left(a.getCity(), SQL_CITY_DIM);
                    String state = StringUtils.left(a.getState(), SQL_STATE_DIM);
                    String postalCode = StringUtils.left(a.getPostalCode(), SQL_POSTALCODE_DIM);
                    String country = StringUtils.left(a.getCountry(), SQL_COUNTRY_DIM);
                    String postOfficeAddress = StringUtils.left(a.getPostOfficeAddress(),
                            SQL_POSTALOFFICEADDRESS_DIM);
                    String extendedAddress = StringUtils.left(a.getExtendedAddress(),
                            SQL_EXTENDEDADDRESS_DIM);

                    String[] addressFields = {street, city,
                            postalCode, country, state,
                            postOfficeAddress, extendedAddress};

                    if(!hasOnlyEmptyOrNullContent(addressFields)){
                        ps1 = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_ADDRESS);
                        ps1.setLong(1, contactId);
                        ps1.setInt(2, a.getType());
                        ps1.setString(3, street);
                        ps1.setString(4, city);
                        ps1.setString(5, state);
                        ps1.setString(6, postalCode);
                        ps1.setString(7, country);
                        ps1.setString(8, postOfficeAddress);
                        ps1.setString(9, extendedAddress);
                        ps1.setBoolean(10, a.isPrivate());
                        ps1.setLong(11, a.getLastUpdate());

                        ps1.executeUpdate();
                        DBUtility.close(null, ps1, null);
                    }
                }
            }

            List<ContactItem> ims = c.getIms();
            if(ims != null ){
                for(ContactItem item:ims){
                    if(item != null){
                        if(!Utility.isEmpty(item.getValue())){
                            ps1 = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                            ps1.setLong(1, contactId);
                            ps1.setInt(2, item.getType());
                            ps1.setString(3, item.getValue());
                            ps1.setBoolean(4,item.isPrivate());
                            ps1.setLong(5,item.getLastUpdate());

                        ps1.executeUpdate();
                        DBUtility.close(null, ps1, null);
                    }
                }
              }
            }

            //xtags
            List<ContactItem> xtags = c.getXTags();

            if(xtags != null){
                for(ContactItem item :xtags){
                    if(item != null){
                        if(!Utility.isEmpty(item.getValue()) || item.getType() !=ContactItem.TYPE_X_TAG_BORQS_NAME ){
                            ps1 = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                            ps1.setLong(1, contactId);
                            ps1.setInt(2, item.getType());
                            ps1.setString(3, item.getValue());
                            ps1.setBoolean(4,isForeverPublic(item)?false:item.isPrivate());
                            ps1.setLong(5,item.getLastUpdate());

                        ps1.executeUpdate();
                        DBUtility.close(null, ps1, null);
                    }
                }
              }
            }

            // Photo
            if(photoToSet) {
                setPhoto(conn, contactId, c.getOwnerId(), photo);
            } else if(photoToRemove) {
                deletePhoto(conn, contactId, c.getOwnerId());
            }

            conn.commit();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                mLogger.info("update contact error!!!!!rollback the update " + e.getMessage());
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        } finally{
            DBUtility.close(conn, null, null);
        }

        return false;  //TODO
    }

    /**
     * query the all sync item in SyncML database, reflect them by the handler
     *
     * @param userId    - owner id
     * @param handler   - handler to handle the row cusor
     * @return
     */
    public boolean querySocialContacts(String userId, CursorResultHandler handler) {
        Connection conn = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("select id,userid,borqsid,last_update,status from borqs_pim_contact where userid=? and status in ('N','U') and borqsid is not null");
            ps.setString(1, userId);
            rs = ps.executeQuery();
            handler.onResult(rs);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, rs);
        }

        return false;
    }

    /**
     * insert the new data item into SyncML database
     *
     * @param c - data content
     * @return the source id for the new item
     */
    public long insertItem(Contact c) {
        long original_last_update_time = c.getLastUpdate();

        if(original_last_update_time <= 0){
            original_last_update_time = System.currentTimeMillis();
        }
        c.setLastUpdate(original_last_update_time);

        if (c == null) {
            return -1;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        long id = 0;
        int type = 0;

        short importance = c.getImportance();
        short sensitivity = c.getSensitivity();
        String mileage = c.getMileage();
        String subject = c.getSubject();
        String folder = c.getFolder();
        String anniversary = c.getAnniversary();
        String firstName = c.getFirstName();
        String middleName = c.getMiddleName();
        String lastName = c.getLastName();
        String displayName = c.getDisplayName();
        String birthday = c.getBirthday();
        String body = c.getBody();
        String categories = c.getCategories();
        String gender = c.getGender();
        String hobbies = c.getHobbies();
        String initials = c.getInitials();
        String languages = c.getLanguages();
        String nickName = c.getNickName();
        String spouse = c.getSpouse();
        String suffix = c.getSuffix();
        String assistant = c.getAssistant();
        String officeLocation = c.getOfficeLocation();
        String company = c.getCompany();
        String companies = c.getCompanies();
        String department = c.getDepartment();
        String jobTitle = c.getJobTitle();
        String manager = c.getManager();
        String profession = c.getProfession();
        String children = c.getChildren();
        String title = c.getTitle();
        String borqsName = c.getBorqsName();
        String bFirstName = c.getBFirstName();
        String bMiddleName = c.getBMiddleName();
        String bLastName = c.getBLastName();

        List<ContactItem> emails = c.getEmails();
        List<ContactItem> phones = c.getTelephones();
        List<ContactItem> webpages = c.getWebpages();
        List<Address> addresses = c.getAddress();

//        Address[] addresses = new Address[3];
//        addresses[0] = c.getHomeAddress();
//        addresses[1] = c.getOtherAddress();
//        addresses[2] = c.getWorkAddress();

        try {
            conn = mContext.getSqlConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT, Statement.RETURN_GENERATED_KEYS);
            //ps.setLong(1, id);

            int k=1;

            ps.setString(k++, c.getOwnerId());
            ps.setString(k++, c.getBorqsId());
            ps.setLong(k++, original_last_update_time);
            ps.setString(k++, "N");

            // Photo status
            boolean hasPhoto = false;
            Photo photo = c.getPhoto();
            if(photo == null) {
                ps.setNull(k++, Types.SMALLINT);
            } else {
                ps.setShort(k++, c.getPhotoType());
                if(c.getPhotoType() != Contact.CONTACT_PHOTO_TYPE_EMPTY) {
                    hasPhoto = true;
                }
            }

            ps.setShort(k++, importance);
            ps.setShort(k++, sensitivity);

            ps.setString(k++, StringUtils.left(subject, SQL_SUBJECT_DIM));
            ps.setString(k++, StringUtils.left(folder, SQL_FOLDER_DIM));

            ps.setString(k++, StringUtils.left(anniversary, SQL_ANNIVERSARY_DIM));
            ps.setString(k++, StringUtils.left(firstName, SQL_FIRSTNAME_DIM));
            ps.setString(k++, StringUtils.left(middleName, SQL_MIDDLENAME_DIM));
            ps.setString(k++, StringUtils.left(lastName, SQL_LASTNAME_DIM));
            ps.setString(k++, StringUtils.left(bFirstName, SQL_BFIRSTNAME_DIM));
            ps.setString(k++, StringUtils.left(bMiddleName, SQL_BMIDDLENAME_DIM));
            ps.setString(k++, StringUtils.left(bLastName, SQL_BLASTNAME_DIM));
            ps.setString(k++, StringUtils.left(displayName, SQL_DISPLAYNAME_DIM));
            ps.setString(k++, StringUtils.left(birthday, SQL_BIRTHDAY_DIM));
            ps.setString(k++, StringUtils.left(body, SQL_BODY_DIM));
            ps.setString(k++, StringUtils.left(categories, SQL_CATEGORIES_DIM));
            ps.setString(k++, StringUtils.left(children, SQL_CHILDREN_DIM));
            ps.setString(k++, StringUtils.left(hobbies, SQL_HOBBIES_DIM));
            ps.setString(k++, StringUtils.left(initials, SQL_INITIALS_DIM));
            ps.setString(k++, StringUtils.left(languages, SQL_LANGUAGES_DIM));
            ps.setString(k++, StringUtils.left(nickName, SQL_NICKNAME_DIM));
            ps.setString(k++, StringUtils.left(spouse, SQL_SPOUSE_DIM));
            ps.setString(k++, StringUtils.left(suffix, SQL_SUFFIX_DIM));
            ps.setString(k++, StringUtils.left(title, SQL_TITLE_DIM));

            ps.setString(k++, StringUtils.left(assistant, SQL_ASSISTANT_DIM));
            ps.setString(k++, StringUtils.left(company, SQL_COMPANY_DIM));
            ps.setString(k++, StringUtils.left(department, SQL_DEPARTMENT_DIM));
            ps.setString(k++, StringUtils.left(jobTitle, SQL_JOBTITLE_DIM));
            ps.setString(k++, StringUtils.left(manager, SQL_MANAGER_DIM));

            if (mileage != null && mileage.length() > SQL_MILEAGE_DIM) {
                mileage = mileage.substring(0, SQL_MILEAGE_DIM);
            }
            ps.setString(k++, StringUtils.left(mileage, SQL_MILEAGE_DIM));
            ps.setString(k++, StringUtils.left(officeLocation, SQL_OFFICELOCATION_DIM));
            ps.setString(k++, StringUtils.left(profession, SQL_PROFESSION_DIM));
            ps.setString(k++, StringUtils.left(companies, SQL_COMPANIES_DIM));
            ps.setString(k++, StringUtils.left(gender, SQL_GENDER_DIM));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) {
                id = rs.getLong(1);
                c.setId(id);
            }

            DBUtility.close(null, ps, rs);

            //
            // emails
            //
            if (emails != null && !emails.isEmpty()) {
                ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);

                for(ContactItem email : emails) {
                    type = email.getType();
                    // Unknown property: saves nothing
                    if (ContactItem.CONTACT_ITEM_TYPE_UNDEFINED == type) continue;

                    String emailValue = email.getValue();

                    if (emailValue != null && emailValue.length() != 0) {
                        if (emailValue.length() > SQL_EMAIL_DIM) {
                            emailValue = emailValue.substring(0, SQL_EMAIL_DIM);
                        }
                        ps.setLong(1, id);
                        ps.setInt(2, type);
                        ps.setString(3, emailValue);
                        ps.setBoolean(4,email.isPrivate());
                        ps.setLong(5,email.getLastUpdate()<=0?c.getLastUpdate():email.getLastUpdate());

                        ps.executeUpdate();
                    }
                }

                DBUtility.close(null, ps, null);
            }

            // borqs name
            LogHelper.logD(mLogger, ">>>>>>>>>>>>>>(S) Begin to insert borqs name <<<<<<<<<<<<<");

            if (!Utility.isEmpty(borqsName)) {


                LogHelper.logD(mLogger, ">>>>>>>>>>>>>>(S)add one borqsname <<<<<<<<<<<<<");

                ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                ps.setLong(1, id);
                ps.setInt(2, ContactItem.TYPE_X_TAG_BORQS_NAME);
                ps.setString(3, borqsName);
                ps.setBoolean(4,false);//borqsname can not be update by client
                ps.setLong(5,c.getLastUpdate());

                ps.executeUpdate();
                DBUtility.close(null, ps, null);

            }

            //
            // phones
            //
            if (phones != null && !phones.isEmpty()) {
                ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);

                for(ContactItem phone : phones) {
                    type = phone.getType();
                    // Unknown property: saves nothing
                    if (ContactItem.CONTACT_ITEM_TYPE_UNDEFINED == type) continue;

                    String phoneValue = phone.getValue();

                    if (!Utility.isEmpty(phoneValue)) {
                        if (phoneValue.length() > SQL_PHONE_DIM) {
                            phoneValue = phoneValue.substring(0, SQL_PHONE_DIM);
                        }
                        ps.setLong(1, id);
                        ps.setInt(2, type);
                        ps.setString(3, phoneValue);
                        ps.setBoolean(4,phone.isPrivate());
                        ps.setLong(5,phone.getLastUpdate()<=0?c.getLastUpdate():phone.getLastUpdate());

                        ps.executeUpdate();

                    }
                }

                DBUtility.close(null, ps, null);
            }

            //
            // webPages
            //
            if (webpages != null && !webpages.isEmpty()) {

                ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                for(ContactItem webPage : webpages) {
                    type = webPage.getType();
                    if(type != ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE
                            && type != ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE
                            && type != ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE) {
                        continue;
                    }

                    String webPageValue = webPage.getValue();
                    if (!Utility.isEmpty(webPageValue)) {
                        if (webPageValue.length() > SQL_WEBPAGE_DIM) {
                            webPageValue = webPageValue.substring(0,
                                    SQL_WEBPAGE_DIM);
                        }

                        ps.setLong(1, id);
                        ps.setInt(2, type);
                        ps.setString(3, webPageValue);
                        ps.setBoolean(4,webPage.isPrivate());
                        ps.setLong(5,webPage.getLastUpdate()<=0?c.getLastUpdate():webPage.getLastUpdate());

                        ps.executeUpdate();
                    }

                }

                DBUtility.close(null, ps, null);
            }

            // Addresses
            if(addresses != null && addresses.size() > 0){
                for(Address a : addresses) {
                    if(a != null) {
                        String street = a.getStreet();
                        String city = a.getCity();
                        String postalCode = a.getPostalCode();
                        String state = a.getState();
                        String country = a.getCountry();
                        String postalOfficeAddress =
                                a.getPostOfficeAddress();
                        String extendedAddress = a.getExtendedAddress();

                        String[] addressFields = {street, city,
                                postalCode, country, state,
                                postalOfficeAddress, extendedAddress};

                        if(!hasOnlyEmptyOrNullContent(addressFields)){
                            ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_ADDRESS);

                            ps.setLong(1, id);
                            ps.setInt(2, a.getType());
                            ps.setString(3, replaceNewLine(StringUtils.left(street, SQL_STREET_DIM)));
                            ps.setString(4, StringUtils.left(city, SQL_CITY_DIM));
                            ps.setString(5, StringUtils.left(state, SQL_STATE_DIM));
                            ps.setString(6, StringUtils.left(postalCode, SQL_POSTALCODE_DIM));
                            ps.setString(7, StringUtils.left(country, SQL_COUNTRY_DIM));
                            ps.setString(8, StringUtils.left(postalOfficeAddress, SQL_POSTALOFFICEADDRESS_DIM));
                            ps.setString(9, StringUtils.left(extendedAddress, SQL_EXTENDEDADDRESS_DIM));
                            ps.setBoolean(10, a.isPrivate());
                            ps.setLong(11, a.getLastUpdate());

                            ps.executeUpdate();
                            DBUtility.close(null, ps, null);
                        }
                    }
                }
            }

            
            //im
            List<ContactItem> ims = c.getIms();
            if(ims != null){
                for(ContactItem item :ims){
                    if(item != null){
                        if(!Utility.isEmpty(item.getValue())){
                            ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                            ps.setLong(1, id);
                            ps.setInt(2, item.getType());
                            ps.setString(3, item.getValue());
                            ps.setBoolean(4,item.isPrivate());
                            ps.setLong(5,item.getLastUpdate()<=0?c.getLastUpdate():item.getLastUpdate());

                        ps.executeUpdate();
                        DBUtility.close(null, ps, null);
                    }
                }
                }
            }

            //xtags
            List<ContactItem> xtags = c.getXTags();
            if(xtags != null){
                for(ContactItem item :xtags){
                    if(item != null){
                        if(!Utility.isEmpty(item.getValue()) || item.getType() != ContactItem.TYPE_X_TAG_BORQS_NAME){
                            ps = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_ITEM);
                            ps.setLong(1, id);
                            ps.setInt(2, item.getType());
                            ps.setString(3, item.getValue());
                            ps.setBoolean(4,isForeverPublic(item)?false:item.isPrivate());
                            ps.setLong(5,item.getLastUpdate()<=0?c.getLastUpdate():item.getLastUpdate());

                        ps.executeUpdate();
                        DBUtility.close(null, ps, null);
                    }
                }
            }
            }

            // Add photo
            if(hasPhoto) {
                insertExternalPhoto(conn, id, c.getOwnerId(), photo);
            }

            conn.commit();

            return id;
        } catch(Exception e) {
            e.printStackTrace();
            try {
                mLogger.info("insert contact error!!!!!!rollback the insert." + e.getMessage());
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally{
            DBUtility.close(conn, null, null);
        }

        return 0;  //TODO
    }

    /**
     * list our the contacts under the userId
     * @param userId
     * @return contact ids in this userid
     */
    public List<Long> listContactIds(String userId, boolean includeDeleted){
        ArrayList<Long> list = new ArrayList<Long>();
        Connection conn = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "select id from borqs_pim_contact where userid=?";
            if(!includeDeleted){
                sql += " AND status!='D'";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            while(rs.next()){
                list.add(rs.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, rs);
        }

        return list;
    }
    
    /**
     * count the contacts that belong to userId
     * @param userId
     * @param includeDeleted
     * @return count value with long type
     */
    public long countByUser(String userId, boolean includeDeleted){
        Connection conn = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        long count = -1;
        try {
            String sql = "select count(*) c from borqs_pim_contact where userid=?";
            if(!includeDeleted){
                sql += " AND status!='D'";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            while(rs.next()){
            	count = rs.getLong("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, rs);
        }

        return count;
    }

    /**
     * Checks whether an array of String objects has some non-white content.
     *
     * @param strings could also be null
     * @return false only if the array contains at least a non-null string
     *               having some content different from white spaces
     */
    private static boolean hasOnlyEmptyOrNullContent(String[] strings) {

        if (strings == null) {
            return true;
        }

        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null && strings[i].length() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether an array of String objects has some non-null content.
     *
     * @param strings could also be null
     * @return false only if the array contains at least a string not being null
     */
    private static boolean hasOnlyNullContent(String[] strings) {

        if (strings == null) {
            return true;
        }

        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) {
                return false;
            }
        }
        return true;
    }

    private static String replaceNewLine(String string) {

        if (string != null) {
            char[] nl = { 10 };
            String newLine = new String(nl);
            string = string.replaceAll(newLine, " ");
        }
        return string;
    }

    private void insertExternalPhoto(Connection conn, long contactId,
                                     String userId, Photo photo) throws DAOException, SQLException {
        if (!verifyPermission(conn, contactId, userId)) {
            throw new DAOException("Contact '" + contactId +
                    " is not a contact of the user '" +
                    userId + "'");
        }

        PreparedStatement stmt = null;

        if (photo == null) {
            return ;
        }
        byte[] image = photo.getImage();

        try {
            stmt = conn.prepareStatement(SQL_INSERT_INTO_BORQS_PIM_CONTACT_PHOTO);

            stmt.setLong(1, contactId);

            if (photo.getType() == null) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, photo.getType());
            }

            if (image == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBinaryStream(3, new ByteArrayInputStream(image), image.length);
            }

            if (photo.getUrl() == null) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, photo.getUrl());
            }

            stmt.execute();

        } finally {
            DBUtility.close(null, stmt, null);
        }
    }

    private boolean verifyPermission(Connection conn, long contactId, String userId) throws DAOException {
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        boolean contactFound = false;
        try {

            stmt = conn.prepareStatement(SQL_GET_CONTACT_ID_BY_ID_AND_USER_ID);
            stmt.setLong(1, contactId);
            stmt.setString(2, userId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                contactFound = true;
            }

        } catch (Exception e) {
            throw new DAOException("Error checking contact '" + contactId + "'", e);
        } finally {
            DBUtility.close(null, stmt, rs);
        }
        return contactFound;
    }

    private void setPhoto(Connection conn, long contactId, String userId, Photo photo) throws DAOException, SQLException {
        if (!verifyPermission(conn, contactId, userId)) {
            throw new DAOException("Contact '" + contactId +
                    " is not a contact of the user '" +
                    userId + "'");
        }

        PreparedStatement stmt = null;

        byte[] image = null;
        String type  = null;
        String url   = null;

        if (photo != null) {
            image = photo.getImage();
            type  = photo.getType();
            url   = photo.getUrl();
        }

        int numUpdatedRows  = 0;

        try {

            stmt = conn.prepareStatement(SQL_UPDATE_BORQS_PIM_CONTACT_PHOTO);

            if (type == null) {
                stmt.setNull(1, Types.VARCHAR);
            } else {
                stmt.setString(1, type);
            }

            if (url == null) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, url);
            }

            if (image == null) {
                stmt.setNull(3, Types.BINARY);
            } else {
                stmt.setBinaryStream(3, new ByteArrayInputStream(image), image.length);
            }

            stmt.setLong(4, contactId);

            numUpdatedRows = stmt.executeUpdate();

        } finally {
            DBUtility.close(null, stmt, null);
        }

        if(numUpdatedRows != 1) {
            insertExternalPhoto(conn, contactId, userId, photo);
        }
    }

    private boolean deletePhoto(Connection conn, long contactId, String userId) throws DAOException {
        if (!verifyPermission(conn, contactId, userId)) {
            throw new DAOException("Contact '" + contactId +
                    " is not a contact of the user '" +
                    userId + "'");
        }

        PreparedStatement stmt = null;

        int numDeletedRows  = 0;
        try {
            stmt = conn.prepareStatement(SQL_DELETE_BORQS_PIM_CONTACT_PHOTO);

            stmt.setLong(1, contactId);

            numDeletedRows = stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DAOException("Error deleting photo with id: '" + contactId + "'", ex);
        } finally {
            DBUtility.close(null, stmt, null);
        }

        return (numDeletedRows == 1);
    }

    public boolean deleteAllItems(String userId){
        return deleteAllItemsWithTimestamp(userId,System.currentTimeMillis());
    }

    public boolean deleteAllItemsWithTimestamp(String userId,long since){
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = mContext.getSqlConnection();

            ps = conn.prepareStatement(SQL_UPDATE_ALL_CONTACT_TO_DELETE_BY_USER_ID);
            if(since <= 0){
               since = System.currentTimeMillis();
            }
            ps.setLong(1, since);
            ps.setString(2, userId);

            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, null);
        }

        return false;
    }
    

    public void updateLastUpdate(long raw_contact_id, long timestamp) {
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(SQL_UPDATE_CONTACT_LAST_UPDATE);
            ps.setLong(1, timestamp);
            ps.setLong(2, raw_contact_id);
            ps.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logW(mLogger, "update last update error!!!!!: " + e.getStackTrace());
        } finally{
            DBUtility.close(conn, ps, null);
        }
    }

    /**
     *
     * @param contactId
     * @return
     */
    public Contact getItem(String contactId) {

        LogHelper.logD(mLogger,"Retrieving contact : " + contactId);

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Contact c = new Contact();

        Long id = Long.parseLong(contactId);
        c.setId(id);

        try {
            // Looks up the data source when the first connection is created
            con = mContext.getSqlConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_CONTACT_ID_BY_USER_ID_AND_ID);
            ps.setLong(1, id);

            rs = ps.executeQuery();

            c = createContact(id, rs, c);

            DBUtility.close(null, ps, rs);

            ps = con.prepareStatement(SQL_GET_BORQS_PIM_CONTACT_ITEM_BY_ID);
            ps.setLong(1, id);

            rs = ps.executeQuery();

            try {
                addPIMContactItems(id, c, rs);
            } catch (SQLException sqle) {
                throw new SQLException("Error while adding extra PIM contact "
                        + "information. " + sqle.getMessage(),
                        sqle.getSQLState());
            }

            DBUtility.close(null, ps, rs);

            ps = con.prepareStatement(SQL_GET_BORQS_PIM_ADDRESS_BY_ID);
            ps.setLong(1, id);

            rs = ps.executeQuery();

            try {
                addPIMAddresses(id, c, rs);
            } catch (SQLException sqle) {
                throw new SQLException("Error while adding PIM address "
                        + "information. " + sqle,
                        sqle.getSQLState());
            }
            DBUtility.close(null, ps, rs);

            if (Photo.PHOTO_IMAGE.equals(c.getPhotoType()) ||
                    Photo.PHOTO_URL.equals(c.getPhotoType())) {
                Photo photo = getPhoto(con, id, c.getOwnerId());
                c.setPhoto(photo);
            } else if (Photo.EMPTY_PHOTO.equals(c.getPhotoType())) {
                c.setPhoto(new Photo(id, String.valueOf(Photo.EMPTY_PHOTO), null, null));
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }

        return c;
    }
    
    private void fillPIMItems(Connection con, Contact c, long id) {
    	try {
			PreparedStatement ps = con.prepareStatement(SQL_GET_BORQS_PIM_CONTACT_ITEM_BY_ID);
			ps.setLong(1, id);
			
			ResultSet rs = ps.executeQuery();
			try {
			    addPIMContactItems(id, c, rs);
			} catch (SQLException sqle) {
			    throw new SQLException("Error while adding extra PIM contact "
			            + "information. " + sqle.getMessage(),
			            sqle.getSQLState());
			}

			DBUtility.close(null, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
			LogHelper.logW(mLogger, "failed to get PIM contact's items!");
		}
    }
    
    private void fillPIMAddresses(Connection con, Contact c, long id) {
    	try {
			PreparedStatement ps = con.prepareStatement(SQL_GET_BORQS_PIM_ADDRESS_BY_ID);
			ps.setLong(1, id);

			ResultSet rs = ps.executeQuery();
			try {
			    addPIMAddresses(id, c, rs);
			} catch (SQLException sqle) {
			    throw new SQLException("Error while adding PIM address "
			            + "information. " + sqle,
			            sqle.getSQLState());
			}
			DBUtility.close(null, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
			LogHelper.logW(mLogger, "failed to get PIM contact's addresses!");
		}
    }
    
    public List<Contact> getItems(String userId, int offset, int count) {

        LogHelper.logD(mLogger,"Retrieving contact : " + userId);
        long total = this.countByUser(userId, false);
        
        List<Contact> contacts = new ArrayList<Contact>();
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Looks up the data source when the first connection is created
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            StringBuffer sb = new StringBuffer();
            sb.append(SQL_GET_CONTACT_ID_BY_USER_ID);
            if(offset >= 0 && count >= 0) {
            	sb.append(" limit ").append(offset).append(",").append(count);
            } else {
            	if(offset >= 0) {
            		sb.append(" limit ").append(offset).append(",").append(total-offset);
            	} else if(count >= 0) {
            		sb.append(" limit ").append(0).append(",").append(count);
            	} else {
            		sb.append(" limit ").append(0).append(",").append(total);
            	}
            }
            LogHelper.logInfo(mLogger, "limit condition is "+sb.toString());
            
            ps = con.prepareStatement(sb.toString());
            ps.setString(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Contact c = createContact(rs);
                contacts.add(c);
                
                // fill items of the contact
                fillPIMItems(con, c, c.getId());
                
                // fill addresses of the contact
                fillPIMAddresses(con, c, c.getId());
                
                if (Photo.PHOTO_IMAGE.equals(c.getPhotoType()) ||
                		Photo.PHOTO_URL.equals(c.getPhotoType())) {
                	Photo photo = getPhoto(con, c.getId(), c.getOwnerId());
                	c.setPhoto(photo);
                } else if (Photo.EMPTY_PHOTO.equals(c.getPhotoType())) {
                	c.setPhoto(new Photo(c.getId(), String.valueOf(Photo.EMPTY_PHOTO), null, null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }

        return contacts;
    }

    /**
     * Returns the photo with the given id using the given connection.
     * <p>Note that the connection is not closed at the end of the method
     *
     * @param con the connection to use
     * @param id  the if
     * @return the photo, or null if not found
     * @throws DAOException if an error occurs
     */
    public Photo getPhoto(Connection con, long id, String userId) throws DAOException {

        Photo photo = null;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            stmt = con.prepareStatement(SQL_SELECT_FROM_BORQS_PIM_CONTACT_PHOTO);
            stmt.setLong(1, id);
            stmt.setString(2, userId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                photo = new Photo(id, rs.getString(2), rs.getBytes(3), rs.getString(4));
            }

        } catch (Exception e) {
            throw new DAOException("Error retrieving photo with id: " + id, e);
        } finally {
            DBUtility.close(null, stmt, rs);
        }
        return photo;
    }

    /**
     * Creates a ContactWrapper object from a ResultSet. Only the basic data are
     * set.
     *
     * @param id the UID of the wrapper object to be returned
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the borqs_pim_contact table, with the cursor before its first row
     * @return a newly created ContactWrapper initialized with the fields in the
     *         result set
     * @throws java.sql.SQLException
     */
    private Contact createContact(long id, ResultSet rs, Contact c)
            throws SQLException {

        if (rs.next()) {
            c.setOwnerId(rs.getString(SQL_FIELD_USERID));
            c.setLastUpdate(rs.getLong(SQL_FIELD_LAST_UPDATE));
            c.setStatus(rs.getString(SQL_FIELD_STATUS));
            c.setPhotoType(rs.getShort(SQL_FIELD_PHOTO_TYPE));
            c.setImportance(rs.getShort(SQL_FIELD_IMPORTANCE));
            c.setSensitivity(rs.getShort(SQL_FIELD_SENSITIVITY));
            c.setSubject(rs.getString(SQL_FIELD_SUBJECT));
            c.setFolder(rs.getString(SQL_FIELD_FOLDER));
            c.setAnniversary(rs.getString(SQL_FIELD_ANNIVERSARY));
            c.setFirstName(rs.getString(SQL_FIELD_FIRST_NAME));
            c.setMiddleName(rs.getString(SQL_FIELD_MIDDLE_NAME));
            c.setLastName(rs.getString(SQL_FIELD_LAST_NAME));
            c.setBFirstName(rs.getString(SQL_FIELD_BFIRST_NAME));
            c.setBMiddleName(rs.getString(SQL_FIELD_BMIDDLE_NAME));
            c.setBLastName(rs.getString(SQL_FIELD_BLAST_NAME));
            c.setDisplayName(rs.getString(SQL_FIELD_DISPLAY_NAME));
            c.setBirthday(rs.getString(SQL_FIELD_BIRTHDAY));
            c.setBody(rs.getString(SQL_FIELD_BODY));
            c.setCategories(rs.getString(SQL_FIELD_CATEGORIES));
            c.setChildren(rs.getString(SQL_FIELD_CHILDREN));
            c.setHobbies(rs.getString(SQL_FIELD_HOBBIES));
            c.setGender(rs.getString(SQL_FIELD_GENDER));
            c.setInitials(rs.getString(SQL_FIELD_INITIALS));
            c.setLanguages(rs.getString(SQL_FIELD_LANGUAGES));
            c.setNickName(rs.getString(SQL_FIELD_NICKNAME));
            c.setSpouse(rs.getString(SQL_FIELD_SPOUSE));
            c.setSuffix(rs.getString(SQL_FIELD_SUFFIX));
            c.setTitle(rs.getString(SQL_FIELD_TITLE));
            c.setAssistant(rs.getString(SQL_FIELD_ASSISTANT));
            c.setCompany(rs.getString(SQL_FIELD_COMPANY));
            c.setCompanies(rs.getString(SQL_FIELD_COMPANIES));
            c.setDepartment(rs.getString(SQL_FIELD_DEPARTMENT));
            c.setJobTitle(rs.getString(SQL_FIELD_JOB_TITLE));
            c.setManager(rs.getString(SQL_FIELD_MANAGER));
            c.setMileage(rs.getString(SQL_FIELD_MILEAGE));
            c.setOfficeLocation(rs.getString(SQL_FIELD_OFFICE_LOCATION));
            c.setProfession(rs.getString(SQL_FIELD_PROFESSION));
            c.setBorqsId(rs.getString(SQL_FIELD_BORQSID));
        }
        return c;
    }
    
    /**
     * create contact object by result set
     * @param rs
     * @return
     * @throws SQLException
     */
    private Contact createContact(ResultSet rs) throws SQLException {
        Contact c = new Contact();
        c.setId(rs.getLong("id"));

        c.setOwnerId(rs.getString(SQL_FIELD_USERID));
        c.setLastUpdate(rs.getLong(SQL_FIELD_LAST_UPDATE));
        c.setStatus(rs.getString(SQL_FIELD_STATUS));
        c.setPhotoType(rs.getShort(SQL_FIELD_PHOTO_TYPE));
        c.setImportance(rs.getShort(SQL_FIELD_IMPORTANCE));
        c.setSensitivity(rs.getShort(SQL_FIELD_SENSITIVITY));
        c.setSubject(rs.getString(SQL_FIELD_SUBJECT));
        c.setFolder(rs.getString(SQL_FIELD_FOLDER));
        c.setAnniversary(rs.getString(SQL_FIELD_ANNIVERSARY));
        c.setFirstName(rs.getString(SQL_FIELD_FIRST_NAME));
        c.setMiddleName(rs.getString(SQL_FIELD_MIDDLE_NAME));
        c.setLastName(rs.getString(SQL_FIELD_LAST_NAME));
        c.setBFirstName(rs.getString(SQL_FIELD_BFIRST_NAME));
        c.setBMiddleName(rs.getString(SQL_FIELD_BMIDDLE_NAME));
        c.setBLastName(rs.getString(SQL_FIELD_BLAST_NAME));
        c.setDisplayName(rs.getString(SQL_FIELD_DISPLAY_NAME));
        c.setBirthday(rs.getString(SQL_FIELD_BIRTHDAY));
        c.setBody(rs.getString(SQL_FIELD_BODY));
        c.setCategories(rs.getString(SQL_FIELD_CATEGORIES));
        c.setChildren(rs.getString(SQL_FIELD_CHILDREN));
        c.setHobbies(rs.getString(SQL_FIELD_HOBBIES));
        c.setGender(rs.getString(SQL_FIELD_GENDER));
        c.setInitials(rs.getString(SQL_FIELD_INITIALS));
        c.setLanguages(rs.getString(SQL_FIELD_LANGUAGES));
        c.setNickName(rs.getString(SQL_FIELD_NICKNAME));
        c.setSpouse(rs.getString(SQL_FIELD_SPOUSE));
        c.setSuffix(rs.getString(SQL_FIELD_SUFFIX));
        c.setTitle(rs.getString(SQL_FIELD_TITLE));
        c.setAssistant(rs.getString(SQL_FIELD_ASSISTANT));
        c.setCompany(rs.getString(SQL_FIELD_COMPANY));
        c.setCompanies(rs.getString(SQL_FIELD_COMPANIES));
        c.setDepartment(rs.getString(SQL_FIELD_DEPARTMENT));
        c.setJobTitle(rs.getString(SQL_FIELD_JOB_TITLE));
        c.setManager(rs.getString(SQL_FIELD_MANAGER));
        c.setMileage(rs.getString(SQL_FIELD_MILEAGE));
        c.setOfficeLocation(rs.getString(SQL_FIELD_OFFICE_LOCATION));
        c.setProfession(rs.getString(SQL_FIELD_PROFESSION));
        c.setBorqsId(rs.getString(SQL_FIELD_BORQSID));
        
        return c;
    }
    
    /**
     * Attaches extra information to a contact on the basis of a ResultSet.
     *
     * @param id the contactrid
     * @param c  the contact of the items
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the borqs_pim_contact_item table, with the cursor before its
     *           first row
     * @return the ContactWrapper object with extra information attached
     * @throws java.sql.SQLException
     */
    private Contact addPIMContactItems(long id, Contact c, ResultSet rs) throws SQLException {

        int type = ContactItem.CONTACT_ITEM_TYPE_UNDEFINED;
        String value = null;
        boolean isPrivate;
        long last_update = 0;
        int columnCount = 0;
        String column = null;

        while (rs.next()) {
            type = rs.getInt(SQL_FIELD_TYPE);
            value = rs.getString(SQL_FIELD_VALUE);
            isPrivate = rs.getBoolean(SQL_FIELD_ISPRIVATE);
            last_update = rs.getLong(SQL_FIELD_ITEM_LAST_UPDATE);
            LogHelper.logD(mLogger,"get contact item,value: " + value + ",type: " +
                    type + ",private: " + isPrivate + ",last_update: " + last_update);

            switch (type) {
                case ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER:
                case ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER:
                    c.addTelephone(new ContactItem(value, type,isPrivate,last_update));
                    break;
                case ContactItem.CONTACT_ITEM_TYPE_BUSINESS_LABEL:
                    break;
                case ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE:
                case ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE:
                case ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE:
                    c.addWebpage(new ContactItem(value, type,isPrivate,last_update));
                    break;
                case ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS:
                case ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS:
                case ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS:
                    c.addEmail(new ContactItem(value, type,isPrivate,last_update));
                    break;
                case ContactItem.CONTACT_ITEM_TYPE_HOME_LABEL:
                    break;
                case ContactItem.TYPE_X_TAG_IM_AIM:
                case ContactItem.TYPE_X_TAG_IM_GTALK:
                case ContactItem.TYPE_X_TAG_IM_ICQ:
                case ContactItem.TYPE_X_TAG_IM_JABBER:
                case ContactItem.TYPE_X_TAG_IM_MSN:
                case ContactItem.TYPE_X_TAG_IM_NETMEETING:
                case ContactItem.TYPE_X_TAG_IM_SKYPE:
                case ContactItem.TYPE_X_TAG_IM_YAHOO:
                case ContactItem.TYPE_X_TAG_IM_WIN_LIVE:
                case ContactItem.CONTACT_ITEM_TYPE_INSTANT_MESSENGER:
                    c.addIm(new ContactItem(value, type,isPrivate,last_update));
                    break;
                case ContactItem.TYPE_X_TAG_BORQS_NAME:
                    c.setBorqsName(value);
                    break;
                case ContactItem.TYPE_X_TAG_PHONETIC_FIRST_NAME:
                case ContactItem.TYPE_X_TAG_PHONETIC_LAST_NAME:
                case ContactItem.TYPE_X_TAG_PHONETIC_MIDDLE_NAME:
                case ContactItem.TYPE_X_TAG_PREFIX_NAME:
                case ContactItem.TYPE_X_TAG_ACCOUNT_TYPE:
                case ContactItem.TYPE_X_TAG_BLOCK:
                case ContactItem.TYPE_X_TAG_GROUP:
                case ContactItem.TYPE_X_TAG_RINGTONG:
                case ContactItem.TYPE_X_TAG_STARRED:
                    c.addXTag(new ContactItem(value, type,isPrivate,last_update));
                    break;
                case ContactItem.CONTACT_ITEM_TYPE_OTHER_LABEL:
                    break;

            }
        }
        return c;
    }

    /**
     * Attaches the address(es) to a contact on the basis of a ResultSet.
     *
     * @param id contact id
     * @param c  the contact (as a) still lacking address
     *           information
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the borqs_pim_address table, with the cursor before its first
     *           row
     * @return the ContactWrapper object with address information attached
     * @throws java.sql.SQLException
     */
    private static Contact addPIMAddresses(long id, Contact c,
                                           ResultSet rs) throws SQLException {
        List<Address> addresses = new ArrayList<Address>();

        while (rs.next()) {
            Address address = new Address.BUILDER().build();
            address.setCity(rs.getString(SQL_FIELD_CITY));
            address.setPostalCode(rs.getString(SQL_FIELD_POSTAL_CODE));
            address.setState(rs.getString(SQL_FIELD_STATE));
            address.setStreet(rs.getString(SQL_FIELD_STREET));
            address.setContact(id);
            address.setCountry(rs.getString(SQL_FIELD_COUNTRY));
            address.setExtendedAddress(rs.getString(SQL_FIELD_EXTENDED_ADDRESS));
            address.setPostOfficeAddress(rs.getString(SQL_FIELD_PO_BOX));
            address.setType(rs.getInt(SQL_FIELD_TYPE));
            address.setPrivate(rs.getBoolean(SQL_FIELD_ADDRESS_ISPRIVATE));
            address.setLastUpdate(rs.getLong(SQL_FIELD_ADDRESS_LAST_UPDATE));
            addresses.add(address);
        }
        c.setAddress(addresses);
        return c;
    }

    /**
     * update the contact and contact's item as private
     * @param contactId
     * @param timestamp
     * @return
     */
    public boolean updateItemAsPrivate(long contactId,long timestamp){
        //remove the group and borqsname
        Connection conn = mContext.getSqlConnection();
        boolean updated = false;
        try {
            conn.setAutoCommit(false);

            deleteBorqsPlus(conn,contactId);
            updated = updateContactAsPrivate(conn,contactId,timestamp) && updateContactItemAsPrivate(conn,contactId,timestamp);

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                mLogger.info("updateItemAsPrivate(delete friend) error!!!!!rollback the delete," + e.getMessage());
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally{
            DBUtility.close(conn,null,null);
        }
        return updated;
    }

    private void deleteBorqsPlus(Connection con,long contactId) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(SQL_REMOVE_CONTACT_GROUP);
            stmt.setLong(1, contactId); //contact_id
            stmt.setLong(2, ContactItem.TYPE_X_TAG_GROUP); //group
            stmt.setLong(3, ContactItem.TYPE_X_TAG_BORQS_NAME); //borqsname
            stmt.executeUpdate();
        }finally {
            DBUtility.close(null, stmt, null);
        }
    }

    private boolean updateContactAsPrivate(Connection con,long contactId, long timestamp) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(SQL_UPDATE_CONTACT_AS_PRIVATE);
            stmt.setNull(1,Types.VARCHAR);//borqsId
            stmt.setLong(2, timestamp); //last_update
            stmt.setLong(3, contactId); //contact_id
            return stmt.executeUpdate() > 0;
        } finally {
            DBUtility.close(null, stmt, null);
        }
    }

    private boolean updateContactItemAsPrivate(Connection con,long contactId, long timestamp) throws SQLException {
        PreparedStatement stmt = null;

        List<Integer> publicTypes = getPublicType();
        StringBuilder sb = new StringBuilder();
        if (!publicTypes.isEmpty()) {
            sb.append(" and type not in (");
            for (int i = 0; i < publicTypes.size(); i++) {
                if (i == publicTypes.size() - 1) {
                    sb.append(publicTypes.get(i));
                } else {
                    sb.append(publicTypes.get(i)).append(",");
                }
            }
            sb.append(")");
        }
        try {
            stmt = con.prepareStatement(SQL_UPDATE_CONTACT_ITEM_AS_PRIVATE + sb.toString());

            stmt.setLong(1, timestamp); //last_update
            stmt.setLong(2, contactId); //contact_id
            return stmt.executeUpdate() > 0;
        } finally {
            DBUtility.close(null, stmt, null);
        }
    }

    /**
     * only return a contact object with name
     * @param contactId
     * @return null if the contact not exist
     */
    public Contact getNameContact(long contactId){
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = mContext.getSqlConnection();
            stmt = con.prepareStatement("select first_name,middle_name,last_name,bfirst_name" +
                    ",bmiddle_name,blast_name from borqs_pim_contact where id=?");
            stmt.setLong(1, contactId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                Contact contact = new Contact();
                contact.setFirstName(rs.getString(1));
                contact.setMiddleName(rs.getString(2));
                contact.setLastName(rs.getString(3));
                contact.setBFirstName(rs.getString(4));
                contact.setBMiddleName(rs.getString(5));
                contact.setBLastName(rs.getString(6));
                return contact;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(con, stmt, rs);
        }
        return null;
    }

    public boolean isBorqsFriend(String contactId){
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = mContext.getSqlConnection();
            stmt = con.prepareStatement("select borqsid from borqs_pim_contact where id=?");
            stmt.setLong(1, Long.parseLong(contactId));

            rs = stmt.executeQuery();
            if (rs.next()) {
                return !Utility.isEmpty(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(con, stmt, rs);
        }
        return false;
    }
    
    private List<Integer> getPublicType(){
        List<Integer> publicTypes = new ArrayList<Integer>();
        publicTypes.add(ContactItem.TYPE_X_TAG_ACCOUNT_TYPE);
        publicTypes.add(ContactItem.TYPE_X_TAG_BORQS_NAME);
        publicTypes.add(ContactItem.TYPE_X_TAG_GROUP);
        publicTypes.add(ContactItem.TYPE_X_TAG_BLOCK);
        publicTypes.add(ContactItem.TYPE_X_TAG_STARRED);
        return publicTypes;
    }

    private boolean isForeverPublic(ContactItem item) {
       List<Integer> publicTypes = getPublicType();
       return publicTypes.contains(item.getType());
    }
}