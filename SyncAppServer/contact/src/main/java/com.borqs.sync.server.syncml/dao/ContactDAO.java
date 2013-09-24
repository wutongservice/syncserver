package com.borqs.sync.server.syncml.dao;

import com.borqs.pim.jcontact.JEMail;
import com.borqs.pim.jcontact.JPhone;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.providers.ContactMerge;
import com.borqs.sync.server.common.providers.ContactProvider;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.util.Utility;
import com.borqs.sync.server.syncml.converter.JContactConverter;
import com.borqs.sync.server.syncml.converter.TypeMatcher;
import com.borqs.sync.server.syncml.util.ContactLogger;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 3/28/12
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContactDAO {

    private static final String SQL_GET_ALL_ITEM_KEYS =
            "select id from borqs_pim_contact "
                    + " WHERE userid = ? "
                    + " AND status <> 'D' ";

    private static final String SQL_GET_CHANGED_CONTACTS_BY_USER_AND_LAST_UPDATE =
            "select id,status from borqs_pim_contact where userid=? and " +
                    "last_update>? and last_update<? order by id";

    private static final String SQL_GET_CONTACTID_BY_BORQSID_AND_USERID = "select id" +
            " from borqs_pim_contact where borqsid=? and userid=? and status <> 'D'";

    private static final String SQL_GET_POTENTIAL_TWINS =
            new StringBuilder("SELECT c.id, i.type as item_type, i.value as item_value ")
                    .append("FROM borqs_pim_contact c LEFT OUTER JOIN borqs_pim_contact_item i ")
                    .append("ON (c.id = i.contact) WHERE (c.userid = ?) ").toString();

    private static final String SQL_UNNAMED_WHERE_CLAUSES = new StringBuilder()
            .append(" AND ( (c.first_name is null) OR (c.first_name = ?) )")
            .append(" AND ( (c.middle_name is null) OR (c.middle_name = ?) )")
            .append(" AND ( (c.last_name is null) OR (c.last_name = ?) )")
            .append(" AND ( (c.company is null) OR (c.company = ?) )")
            .append(" AND ( (c.display_name is null) OR (c.display_name = ?) ) ")
            .toString();

    private static final String SQL_GET_MAX_LAST_UPDATE_TIME = "select last_update from " +
            " borqs_pim_contact where userid=? and status <> 'D' order by last_update desc limit 1";

    private static final String SQL_GET_SYNC_SOURCE_VERSION = "select sync_source_version from borqs_user_sync_version where " +
            "username=? ";
    private static final String SQL_GET_SYNC_VERSION = "select sync_version from fnbl_principal where " +
            "username=? and device=? and id=? ";
    private static final String SQL_UPDATE_SYNC_VERSION = "update fnbl_principal set sync_version =? " +
            " WHERE username=? and device=? and id=? ";
    private static final String SQL_GET_DEVICE_VERSION_BY_USERNAME = "select device,sync_version from fnbl_principal where username=?";

    private static final String SQL_STATUS_NOT_D = " AND c.status != 'D' ";
    private static final String SQL_STATUS_NO_BORQSID = " AND c.borqsid is null ";
    private static final String SQL_ORDER_BY_ID = "ORDER BY id";

    private static final int CHANGED_ITEMS_TYPE_ADD = 0;
    private static final int CHANGED_ITEMS_TYPE_UPDATED = 1;
    private static final int CHANGED_ITEMS_TYPE_DELETED = 2;

    protected static final int SQL_FIRSTNAME_DIM = 64;
    protected static final int SQL_MIDDLENAME_DIM = 64;
    protected static final int SQL_LASTNAME_DIM = 64;
    protected static final int SQL_DISPLAYNAME_DIM = 128;
    protected static final int SQL_COMPANY_DIM = 255;
    protected static final int SQL_EMAIL_DIM = 255;

    private static final String UNSET_FIELD_PLACEHOLDER = "<N/A>";
    
    private Context mContext;
    private Logger mLogger;

    public ContactDAO(Context context){
        mContext = context;
        mLogger = ContactLogger.getLogger(context);
    }

    public List<Long> getAllItemKeys(String userId){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Long> keys = new ArrayList<Long>();

        try {
            conn = mContext.getSqlConnection();

            ps = conn.prepareStatement(SQL_GET_ALL_ITEM_KEYS);
            ps.setString(1,userId);

            rs = ps.executeQuery();

            while(rs.next()){
                keys.add(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, rs);
        }
        return keys;
    }

    public List<Long> getUpdatedItemsByLastUpdate(String userId, long since, long to) {
        mLogger.info("Seeking changed items "
                + "in time interval ]" + since + "; " + to + "[");

        return getChangedItemLastUpdate(userId,since,to, CHANGED_ITEMS_TYPE_UPDATED);
    }

    public List<Long> getNewItemsByLastUpdate(String userId, long since, long to) {
        mLogger.info("Seeking new items "
                + "in time interval ]" + since + "; " + to + "[");

        return getChangedItemLastUpdate(userId,since,to, CHANGED_ITEMS_TYPE_ADD);
    }

    public List<Long> getRemovedItemsByLastUpdate(String userId, long since, long to) {
        mLogger.info("Seeking removed items "
                + "in time interval ]" + since + "; " + to + "[");

        return getChangedItemLastUpdate(userId,since,to, CHANGED_ITEMS_TYPE_DELETED);
    }
    
    private List<Long> getChangedItemLastUpdate(String userId, long since, long to,int changeType){
        mLogger.info("Seeking changed items "
                + "in time interval ]" + since + "; " + to + "[");

        List<Long> changedItems = new ArrayList<Long>();

        Connection con = null;
        PreparedStatement ps = null;

        ResultSet rs = null;

        try {
            // Looks up the data source when the first connection is created
            con = mContext.getSqlConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_CHANGED_CONTACTS_BY_USER_AND_LAST_UPDATE);
            ps.setString(1, userId);
            ps.setLong(2, since);
            ps.setLong(3, to);

            rs = ps.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong(1);
                String status = rs.getString(2);
                char s = status.charAt(0);

                switch(changeType){
                    case CHANGED_ITEMS_TYPE_ADD:
                        if (s == 'N' || s == 'n') {
                            changedItems.add(id);
                        }
                        break;
                    case CHANGED_ITEMS_TYPE_UPDATED:
                        if (s == 'U' || s == 'u') {
                            changedItems.add(id);
                        }
                        break;
                    case CHANGED_ITEMS_TYPE_DELETED:
                        if (s == 'D' || s == 'd') {
                            changedItems.add(id);
                        }
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }
        return changedItems;
    }
    
    
    

    /**
     * Retrieves the UID list of the contacts considered to be "twins" of a
     * given contact.
     *
     * @param c the Contact object representing the contact whose twins
     *          need to be found.
     * @return a List of UIDs (as String objects) that may be empty but not null
     * @throws com.borqs.sync.server.common.exception.DAOException if an error occurs
     */
    public List<Long> getTwinItems(Contact c) {
        mLogger.info("Retrieving twin items for the given contact...");
        Map<Long, List<ContactItem>> unnamedContacts = new HashMap<Long, List<ContactItem>>();

        List<Long> twins = new ArrayList<Long>();
        Map<Long, List<ContactItem>> twinsFound =
                new HashMap<Long, List<ContactItem>>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if(!isTwinSearchAppliableOn(c)) {
            mLogger.info("Item with no email addresses, company name, first, "
                        + "last and display names: twin search skipped.");
            return twins;
        }
        //if the contact is borqs account,we should query the contact by borqsid and userid,the return the contactid
         if (!Utility.isEmpty(c.getBorqsId())) {
            String borqsId = c.getBorqsId();
            mLogger.info("the borqsid is :" + borqsId + ",we query the contact by borqsid and userid as twin contact id directly");
            try {
                long twinContactId = getContactIDByUserAndBorqsID(borqsId,c.getOwnerId());
                if(twinContactId > 0){
                    twins.add(twinContactId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return twins;
        }

        try {

            String firstName = c.getFirstName();
            String lastName = c.getLastName();
            String middleName = c.getMiddleName();

            String displayName = c.getDisplayName();
            String companyName = c.getCompany();


            firstName = StringUtils.left(firstName, SQL_FIRSTNAME_DIM);
            middleName = StringUtils.left(middleName, SQL_MIDDLENAME_DIM);
            lastName = StringUtils.left(lastName, SQL_LASTNAME_DIM);
            displayName = StringUtils.left(displayName, SQL_DISPLAYNAME_DIM);
            companyName = StringUtils.left(companyName, SQL_COMPANY_DIM);
            firstName   = normalizeField(firstName);
            middleName   = normalizeField(middleName);
            lastName    = normalizeField(lastName);
            displayName = normalizeField(displayName);
            companyName = normalizeField(companyName);

            StringBuilder query = new StringBuilder(SQL_GET_POTENTIAL_TWINS);
            List<String> params = new ArrayList<String>();

            // Looks up the data source when the first connection is created
            con = mContext.getSqlConnection();
            con.setReadOnly(true);

            //
            // If Funambol is not in the debug mode is not possible to print the
            // contact because it contains sensitive data.
            //
            StringBuilder sb = new StringBuilder(100);
            sb.append("Looking for items having: ")
                     .append("\n> first name   : '").append(toPrintableString(firstName)).append('\'')
                     .append("\n> middle name   : '").append(toPrintableString(middleName)).append('\'')
                     .append("\n> last  name   : '").append(toPrintableString(lastName)).append('\'')
                     .append("\n> display name : '").append(toPrintableString(displayName)).append('\'')
                     .append("\n> company name : '").append(toPrintableString(companyName)).append('\'');

            mLogger.info(sb.toString());

            boolean isUnnamedContact =
                    StringUtils.isEmpty(firstName)
                            && StringUtils.isEmpty(middleName)
                            && StringUtils.isEmpty(lastName)
                            && StringUtils.isEmpty(displayName)
                            && StringUtils.isEmpty(companyName);

            if (isUnnamedContact) {

//                if (unnamedContacts == null) {

                    query.append(SQL_UNNAMED_WHERE_CLAUSES);
                    query.append(SQL_STATUS_NOT_D);
                    query.append(SQL_STATUS_NO_BORQSID);
                    query.append(SQL_ORDER_BY_ID);

                    params.add(c.getOwnerId());
                    params.add(firstName);
                    params.add(middleName);
                    params.add(lastName);
                    params.add(companyName);
                    params.add(displayName);

                    ps = con.prepareStatement(query.toString());
                    mLogger.info("isUnnamedContact,the query sql is :" + query.toString());

                    int cont = 1;
                    for (String param : params) {
                        ps.setString(cont++, param);
                    }

                    rs = ps.executeQuery();

                    //slipts query result in a better organized data structure
                    //-contact id
                    //  -item type, item value
                    //  -item type, item value
                    //  -...
                    //-contact id
                    //  -...
                    unnamedContacts = getTwinsItemsFromRecordset(rs);

                    mLogger.info("Found '"+unnamedContacts.size()+
                                "' potential twin unnamed contacts with ids '"+
                                unnamedContacts.keySet().toString()+"'");
                    DBUtility.close(null, null, rs);
//                }

                // returns only the twin items

                twinsFound =
                        retrievePotentialTwinsComparingEmailsAndPhoneNumbers(c, unnamedContacts, isUnnamedContact);

            } else {

                StringBuilder nameSB = new StringBuilder();
                nameSB.append(lastName.trim()).append(middleName.trim()).append(firstName.trim());

                params.add(c.getOwnerId());

                query.append(" AND (");
                query.append(" lower(concat(trim(ifnull(c.last_name,'')),trim(ifnull(c.middle_name,'')),trim(ifnull(c.first_name,'')))) = ? ");
                params.add(nameSB.toString().toLowerCase());
//                if ("".equals(firstName)) {
//                    query.append(" (c.first_name is null) OR ");
//                }
//                query.append(" (lower(c.first_name) = ?) ");
//                params.add(firstName.toLowerCase());
//                query.append(" )");
//
//                query.append(" AND (");
//                if ("".equals(lastName)) {
//                    query.append(" (c.last_name is null) OR ");
//                }
//                query.append(" (lower(c.last_name) = ?) ");
//                params.add(lastName.toLowerCase());
                query.append(" )");
                //
                // Only if the first name and last name are empty,
                // the company is used in the research of twin items.
                //
                if ("".equals(firstName) && "".equals(lastName) && "".equals(middleName)) {

                    query.append(" AND (");

                    if ("".equals(companyName)) {
                        query.append(" (c.company is null) OR ");
                    }
                    query.append(" (lower(c.company) = ?) ");
                    params.add(companyName.toLowerCase());
                    query.append(" )");

                    //
                    // Only if the first name, last name and company are empty,
                    // the display name is used in the research of twin items.
                    //
                    if ("".equals(companyName)) {

                        query.append(" AND (");
                        if ("".equals(displayName)) {
                            query.append(" (c.display_name is null) OR ");
                        }
                        query.append(" (lower(c.display_name) = ?) ");
                        params.add(displayName.toLowerCase());
                        query.append(" ) ");
                    }
                }

                query.append(SQL_STATUS_NOT_D);
                query.append(SQL_STATUS_NO_BORQSID);
                query.append(SQL_ORDER_BY_ID);

                ps = con.prepareStatement(query.toString());
                mLogger.info("namedContact,the query sql is :" + query.toString());

                int cont = 1;
                for (String param : params) {
                    ps.setString(cont++, param);
                }

                rs = ps.executeQuery();

                //slipts query result in a better organized data structure
                //-contact id
                //  -item type, item value
                //  -item type, item value
                //  -...
                //-contact id
                //  -...
                Map<Long, List<ContactItem>> twinsInfo =
                        getTwinsItemsFromRecordset(rs);

                mLogger.info("Found '"+twinsInfo.size()+
                        "' potential twin contacts with ids '"+
                        twinsInfo.keySet().toString()+"'");
                DBUtility.close(null, null, rs);
                mLogger.info("exist name in contact,then execute retrievePotentialTwinsComparingEmailsAndPhoneNumbers");
                //returns only the twin items
                twinsFound=
                        retrievePotentialTwinsComparingEmailsAndPhoneNumbers(c, twinsInfo, isUnnamedContact);
            }

            for (Long twinId : twinsFound.keySet()) {
                mLogger.info("Found twin '" + twinId + "'");
                twins.add(twinId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }
        return twins;
    }

    /**
     * This method allows to understand if is possible to run the twin search
     * on the given contact.
     * Fields used in the twin search are:
     * -  firstName
     * -  lastName
     * -  displayName
     * -  companyName
     * -  at least one email address
     * -  at least one phone number
     *
     * @param contact the contact we want to check
     *
     * @return true if at least one field used for twin search contains
     * meaningful data, false otherwise
     */
    public boolean isTwinSearchAppliableOn(Contact contact) {

        if(contact==null) return false;

        boolean hasAtLeastOneValidEmail = hasAtLeastOneNonEmptyProperty(contact.getEmails());

        boolean hasAtLeastOneValidPhone = hasAtLeastOneNonEmptyProperty(contact.getTelephones());

        String firstName = contact.getFirstName();
        String lastName = contact.getLastName();
        String displayName = contact.getDisplayName();
        String companyName = contact.getCompany();

        firstName   = normalizeField(firstName);
        lastName    = normalizeField(lastName);
        displayName = normalizeField(displayName);
        companyName = normalizeField(companyName);

        mLogger.info("@@@@@@@@@ firstName: " + firstName);
        mLogger.info("@@@@@@@@@ lastName: " + lastName);
        mLogger.info("@@@@@@@@@ displayName: " + displayName);
        mLogger.info("@@@@@@@@@ companyName: " + companyName);
        mLogger.info("@@@@@@@@@ hasAtLeastOneValidEmail: " + hasAtLeastOneValidEmail);
        mLogger.info("@@@@@@@@@ hasAtLeastOneValidPhone: " + hasAtLeastOneValidPhone);
        return (firstName.length()   > 0 ||
                lastName.length()    > 0 ||
                companyName.length() > 0 ||
                displayName.length() > 0 ||
                hasAtLeastOneValidEmail  ||
                hasAtLeastOneValidPhone    );
    }

    /**
     * Verify that contact and twin have same phones in the same positions.
     *
     * @param contactPhones the list of contact's phones
     * @param twinPhones the map of twin's phones
     * @return true if contact and twin have same phones in the same positions
     */
    private boolean haveContactAndTwinSamePhonesInSamePosition(
            List<ContactItem> contactPhones,
            List<ContactItem> twinPhones) {

        TypeMatcher typeMatcher = JContactConverter.getTypeMatcher();
        if(contactPhones != null){
            for (ContactItem phone : contactPhones) {
                int type = phone.getType();
                String convertedContactType = typeMatcher.matchJContactType(type, JPhone.OTHER,TypeMatcher.TYPE_PHONE_MATCHER);
                mLogger.info("contact phone type:" + type);
                if (ContactItem.CONTACT_ITEM_TYPE_UNDEFINED == type) continue;
                String phoneContactValue = phone.getValue();
                //compare the twin
                boolean findTwin = false;
                int found = -1;
                for(int i = 0;i<twinPhones.size();i++){
                    ContactItem phonePro = twinPhones.get(i);
                    String phoneTwinValue = phonePro.getValue();
                    int phoneTwinType = phonePro.getType();
                    String convertedTwinType = typeMatcher.matchJContactType(phoneTwinType, JPhone.OTHER,TypeMatcher.TYPE_PHONE_MATCHER);
                    if(convertedTwinType.equalsIgnoreCase(convertedContactType) && phoneContactValue.trim().equalsIgnoreCase(phoneTwinValue.trim())){
                        findTwin = true;
                        found = i;
                        break;
                    }
                }
                if(!findTwin){
                    return false;
                }else{
                    twinPhones.remove(found);
                }
            }
            mLogger.info("haveContactAndTwinSamePhonesInSamePosition : true");
        }

        return true;
    }

    /**
     * Verify if the list of {@link ContactItem} contains at least a valid
     * property value.
     *
     * @param listsOfProperties the list of properties to check
     * @return true if the list contains at least a value
     */
    private boolean hasAtLeastOneNonEmptyProperty(List... listsOfProperties) {
        if (null == listsOfProperties) return false;

        for (List propertiesList : listsOfProperties) {
            if (null == propertiesList) continue;

            //analyze the single list of properties
            for(Object rawProperty : propertiesList) {
                if (rawProperty == null) continue;
                ContactItem prop = (ContactItem) rawProperty;
                String value = prop.getValue();
                //the property has a non empty value
                if (!StringUtils.isEmpty(normalizeField(value))) return true;
            }
        }

        return false;
    }

    private long getContactIDByUserAndBorqsID(String borqsId,String userId) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(SQL_GET_CONTACTID_BY_BORQSID_AND_USERID);
            ps.setString(1,borqsId);
            ps.setString(2,userId);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new Exception("Error getContactIdByUserAndBorqsID");
        } finally {
            DBUtility.close(conn,ps,rs);
        }
        return 0;
    }

    /**
     *
     *
     * @param fieldValue the value of the field to normalize
     *
     * @return the normalized field, the field itself if it's not null
     */
    private String normalizeField(String fieldValue) {
        if (fieldValue == null || ("null".equals(fieldValue))) {
            return "";
        }
        return fieldValue;
    }

    /**
     *
     * @param fieldValue the string we want to log
     *
     * @return a log printable representation of the given string
     */
    private String toPrintableString(String fieldValue) {
        return (fieldValue!=null && fieldValue.length()>0)?
                fieldValue:
                UNSET_FIELD_PLACEHOLDER;
    }

    /**
     * Read data from twin query and put it inside a Map.
     * First map has the contact id as Key and a map of contact items as their values.
     * Second map has the item type as Key and the item value as Valu
     * e.
     *
     * @param rs the ResultSet to check
     * @return the map of potential twins
     */
    private Map<Long, List<ContactItem>> getTwinsItemsFromRecordset(ResultSet rs)
            throws Exception {
        long oldTwinId = -1;
        long twinId;
        int itemType;
        String itemValue;
        List<ContactItem> twinValues = null;
        Map<Long, List<ContactItem>> values =
                new HashMap<Long, List<ContactItem>>();
        while(rs.next()) {
            twinId = rs.getLong(1); // The id is the first
            if (twinId != oldTwinId) {
                //new contact id
                oldTwinId = twinId;
                twinValues = new ArrayList<ContactItem>();
                //add new contact record
                values.put(twinId, twinValues);
            }
            //add new item type and value to the map of contact items
            itemType = rs.getByte(2);
            itemValue = rs.getString(3);
            ContactItem pro = new ContactItem();
            pro.setType(itemType);
            pro.setValue(itemValue);
            twinValues.add(pro);
        }
        return values;
    }

    /**
     * Analyzes raw list of twins for a contact and returns only the items that
     * satisfies the condition on emails and phone numbers.
     *
     * @param contact the contact to check twins
     * @param twinsInfo the information about potential twins to check
     * @param isUnnamedContact true id the contact is unnamed, false otherwise
     * @return a Map of twin items
     */
    public Map<Long, List<ContactItem>>
    retrievePotentialTwinsComparingEmailsAndPhoneNumbers(
            Contact contact,
            Map<Long, List<ContactItem>> twinsInfo,
            boolean isUnnamedContact) {

        Map<Long, List<ContactItem>> twinsFound =
                new HashMap<Long, List<ContactItem>>();

        if (twinsInfo.isEmpty()) return twinsFound;


        //get emails from contact
        List<ContactItem> contactEmails = getContactPropertiesRemovingNullOrEmptyValues(
                false,contact.getEmails());
        for (ContactItem index : contactEmails) {
            index.setValue(StringUtils.left(index.getValue(), SQL_EMAIL_DIM));
            mLogger.info("after removeNullOrEmptyValues,the contactEmail value is :" + index.getValue());
            mLogger.info("after removeNullOrEmptyValues,the contactEmail type is :" + index.getType());
        }

        //get phone numbers from contact
        mLogger.info("getContactPhoneRemovingNullOrEmptyValues");
        List<ContactItem> contactPhones  = getContactPropertiesRemovingNullOrEmptyValues(
                true,contact.getTelephones());
        for (ContactItem index : contactPhones) {
            mLogger.info("after removeNullOrEmptyValues,the contactPhone value is :" + index.getValue());
            mLogger.info("after removeNullOrEmptyValues,the contactPhone type is :" + index.getType());
        }

        for (Long twinId: twinsInfo.keySet()) {

            List<ContactItem> twinValues = twinsInfo.get(twinId);

            //get emails for the potential twin

            List<ContactItem> twinEmails = getPropertiesWithValidValues(
                    twinValues,
                    false,
                    ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS,
                    ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS,
                    ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS);
            //get phone numbers for the potential twin
            List<ContactItem> twinPhones = getPropertiesWithValidValues(
                    twinValues,
                    true,
                    ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER,//no
                    ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER,
                    ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER);

            boolean areTwins = false;

            //manages the conditions that make the potential twins true twins
            //(inclusion cases)
            if (!isUnnamedContact) {
                //case
                //- if both contact and twins haven't email addresses and phone
                //  numbers, they must be considered twins
                if (!areTwins) {
                    areTwins = contactEmails.isEmpty()
                            && contactPhones.isEmpty()
                            && twinEmails.isEmpty()
                            && twinPhones.isEmpty();
                }
                //case
                //- if the contact contains no fields other than name
                //  (first/last/display/company) and the twin contains name
                //  plus other fields (or viceversa), they must be considered
                // twins
                if (!areTwins) {
                    areTwins = contactOrTwinHasNoFieldsWhileTheOtherHas(
                            contactEmails, contactPhones, twinEmails, twinPhones);
                }
            }

            //case
            //- If they contain at least one identical email address in any
            //  of the address fields
            if (!areTwins) {
                mLogger.info("execute haveContactAndTwinEmailsInCommon");
                areTwins =
                        haveContactAndTwinEmailsInCommon(contactEmails, twinEmails);
                mLogger.info("areTwins:" + areTwins);
            }
            //case
            //- If they contain at least one identical phone number in any of
            //  the phone number fields
            if (!areTwins) {
                mLogger.info("execute haveContactAndTwinPhoneNumbersInCommon");
                areTwins =
                        haveContactAndTwinPhoneNumbersInCommon(contactPhones, twinPhones);
                mLogger.info("areTwins:" + areTwins);
            }

            //manages the conditions that make the true twins not twins anymore
            //(exclusion cases)

            //manage the case
            //- If the contacts don't have different phone numbers/email
            //  addresses/ in the same corresponding field (e.g. 2 different
            //  phone numbers in the same HOME phone # fields of the contacts)
            if (areTwins) {
                mLogger.info("execute haveContactAndTwinSameEmailsInSamePosition");
                areTwins =
                        haveContactAndTwinSameEmailsInSamePosition(contactEmails, twinEmails);
                mLogger.info("areTwins:" + areTwins);
            }
            if (areTwins) {
                mLogger.info("execute haveContactAndTwinSamePhonesInSamePosition");
                areTwins =
                        haveContactAndTwinSamePhonesInSamePosition(contactPhones, twinPhones);
                mLogger.info("areTwins:" + areTwins);
            }

            if (areTwins){
                twinsFound.put(twinId, twinValues);
            }
        }
        mLogger.info("twinsFound:" + twinsFound.keySet().toString());
        return twinsFound;
    }

    /**
     * Retrieves only the list of contact properties that have a value.
     * The properties with a null or empty value is discarded.
     *
     * @param contactPropertiesLists the list of contact properties to check
     * @param cleanPhoneNumberValue true if the values must be cleaned of spaces, parenthesis
     *                   plus signs etc (typical telephone numbers)
     * @return the list of contact's properties with a valid value.
     */
    private List getContactPropertiesRemovingNullOrEmptyValues(
            boolean cleanPhoneNumberValue,
            List<ContactItem> contactPropertiesLists) {
        mLogger.info("getContactPropertiesRemovingNullOrEmptyValues");
        List<ContactItem> result = new ArrayList();

        if(contactPropertiesLists == null || contactPropertiesLists.size() <= 0){
            return  result;
        }

        for (ContactItem contactItem : contactPropertiesLists) {
                mLogger.info("contactItem.getValue():" + contactItem.getValue());
                if (StringUtils.isNotEmpty(contactItem.getValue())) {
                    if (cleanPhoneNumberValue) {
                        cleanPhoneNumber(contactItem);
                    }
                    result.add(contactItem);
                }
            }
        return result;
    }

    /**
     * Cleans phone number from spaces, parenthesis, plus etc
     * @param value phone number to clean, cannot be null
     * @return the phone number without unused characters
     */
    private String cleanPhoneNumber(String value) {
        //http://www.vogella.de/articles/JavaRegularExpressions/article.html
        //\W -> all non digit chars (no letters and no numbers)
        value = value.replaceAll("[\\W]", "");
        value = value.replace("-", "").replace(" ", "");
        return value;
    }

    private void cleanPhoneNumber(ContactItem property) {
        String newValue = cleanPhoneNumber(property.getValue());
        property.setValue(newValue);
    }

    /**
     * Retrieves the map of property that have a not null and not empty value
     * for the given value types.
     *
     * @param values the list of property values to check
     * @param cleanPhoneNumberValue true if the values must be cleaned of spaces, parenthesis
     *                   plus signs etc (typical telephone numbers)
     * @param valueTypes the property types to consider
     * @return the list of property values with a valid value.
     */
    private List<ContactItem> getPropertiesWithValidValues(
            List<ContactItem> values,
            boolean cleanPhoneNumberValue,
            int... valueTypes) {
        //new list
        List<ContactItem> result = new ArrayList<ContactItem>();

        if(values == null || values.size() <=0 ){
            return result;
        }

        for (int type : valueTypes) {
            for(ContactItem pro:values){
                if(type == pro.getType()){
                    String value = pro.getValue();
                    if (StringUtils.isNotEmpty(value)) {
                        if (cleanPhoneNumberValue) {
                            //clean the value
                            value = cleanPhoneNumber(value);
                        }
                        ContactItem newPro = new ContactItem();
                        newPro.setType(type);
                        newPro.setValue(value);
                        result.add(newPro);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Verifies that if
     * - the contact contains no fields other than name, and the twin contains
     *   the same name plus emails and/or phone numbers, they can be considered
     *   twins
     * - the twin contains no fields other than name, and the contact contains
     *   the same name plus emails and/or phone numbers, they can be considered
     *   twins
     *
     * @param contactEmails the list of contact emails
     * @param contactPhones the list of contact phone numbers
     * @param twinEmails the map of the twin's emails
     * @param twinPhones the map of the twin's phone numbers
     * @return true if the conditions are satisfied
     */
    private boolean contactOrTwinHasNoFieldsWhileTheOtherHas(
            List<ContactItem> contactEmails,
            List<ContactItem> contactPhones,
            List<ContactItem> twinEmails,
            List<ContactItem> twinPhones) {

        boolean contactHasEmails = false;
        boolean contactHasPhones = false;
        boolean twinHasEmails = false;
        boolean twinHasPhones = false;

        if (twinEmails != null) twinHasEmails = !twinEmails.isEmpty();
        if (twinPhones != null) twinHasPhones = !twinPhones.isEmpty();

        if (contactEmails != null) contactHasEmails = !contactEmails.isEmpty();
        if (contactPhones != null) contactHasPhones = !contactPhones.isEmpty();

        return  ( (twinHasEmails || twinHasPhones) && !(contactHasEmails || contactHasPhones) )
                || ( (contactHasEmails || contactHasPhones) && !(twinHasEmails || twinHasPhones) );
    }

    /**
     * Checks if contact and potential twin have emails in common.
     *
     * @param contactEmails the list of contact's emails
     * @param twinEmails the map of twin's emails
     *
     * @return <b>true</b> if contact and twin have at least one email address
     *         in common, <b>false</b> otherwise
     */
    private boolean haveContactAndTwinEmailsInCommon(
            List<ContactItem> contactEmails,
            List<ContactItem> twinEmails) {

        //no emails to process
        if (contactEmails == null || contactEmails.isEmpty() || twinEmails == null ||twinEmails.isEmpty()) return false;

        //aggregate the two email lists
        List<String> emailsSummary = aggregateEmailAddressesRemovingDuplicates(
                contactEmails,
                twinEmails);

        // checks if contact and twin do not share emails or if they share, and
        // if the total number of emails are greater than 3 (prevent data loss)
        mLogger.info("emailsSummary.size():" + emailsSummary.size());
        mLogger.info("contactEmails.size():" + contactEmails.size());
        mLogger.info("twinEmails.size():" + twinEmails.size());
        if (emailsSummary.size() < (contactEmails.size() + twinEmails.size())) {
            mLogger.info("haveContactAndTwinEmailsInCommon:true");
            return true;
        } else {
            mLogger.info("haveContactAndTwinEmailsInCommon:false");
            return false;
        }
    }

    /**
     * Checks if contact and potential twin have phone numbers in common.
     *
     * @param contactPhones the list of contact's phone numbers
     * @param twinPhones the map of twin's phone numbers
     *
     * @return <b>true</b> if contact and twin have at least one phone numbers
     *         in common, <b>false</b> otherwise
     */
    private boolean haveContactAndTwinPhoneNumbersInCommon(
            List<ContactItem> contactPhones,
            List<ContactItem> twinPhones) {

        //no phone number to process
        if (contactPhones == null || contactPhones.isEmpty() || twinPhones == null || twinPhones.isEmpty()) return false;

        for (ContactItem phone:contactPhones) {
            String contactPhone = phone.getValue();
            
            for(ContactItem phonePro:twinPhones){
                if (phonePro.getValue().trim().equalsIgnoreCase(contactPhone.trim())) {
                    mLogger.info("haveContactAndTwinPhoneNumbersInCommon:true");
                    return true;
                }
            }
        }
        mLogger.info("haveContactAndTwinPhoneNumbersInCommon:false");
        return false;
    }
    
    /**
     * Creates a list that contains all the emails starting from contact and
     * twin lists, but without duplicate elements.
     * The comparison is case insensitive.
     *
     * @param contactEmails the list of contact's emails
     * @param twinEmails the map of twin's emails
     * @return a List of emails
     */
    private List<String> aggregateEmailAddressesRemovingDuplicates(
            List<ContactItem> contactEmails,
            List<ContactItem> twinEmails) {

        mLogger.info("aggregateEmailAddressesRemovingDuplicates");


        List<String> finalList = new ArrayList<String>();

        if(contactEmails != null){
            for (ContactItem email:contactEmails) {
                finalList.add(email.getValue());
                mLogger.info("finalList add an email:" + email.getValue());
            }
        }

        if(twinEmails != null){
            for(ContactItem emailPro:twinEmails){
                String twinEmail = emailPro.getValue();
                mLogger.info("twinEmail:" + twinEmail);
                boolean foundEmail = false;
                //find if new twin email is a duplicate of an already existing email
                for (ContactItem contactEmail:contactEmails) {
                    mLogger.info("contact email:" + contactEmail);
                    if (twinEmail.trim().equalsIgnoreCase(contactEmail.getValue().trim())) {
                        mLogger.info("twin email equal contact email,value is :" + twinEmail);
                        foundEmail = true;
                        break;
                    }
                }
                //if it isn't, then add it to the list
                mLogger.info("found not duplicate email?" + foundEmail);
                if (!foundEmail) finalList.add(twinEmail);

            }
        }


        return finalList;
    }

    /**
     * Verify that contact and twin have same emails in the same positions.
     *
     * @param contactEmails the list of contact's emails
     * @param twinEmails the map of twin's emails
     * @return true if contact and twin have same emails in the same positions
     */
    private boolean haveContactAndTwinSameEmailsInSamePosition(
            List<ContactItem> contactEmails,
            List<ContactItem> twinEmails) {

        TypeMatcher typeMatcher = JContactConverter.getTypeMatcher();
        if(contactEmails != null){
            for (ContactItem email : contactEmails) {
                int type =email.getType();
                String convertedType = typeMatcher.matchJContactType(type, JEMail.OTHER,TypeMatcher.TYPE_EMAIL_MATCHER);
                mLogger.info("contact email type:" + type);
                if (ContactItem.CONTACT_ITEM_TYPE_UNDEFINED == type) continue;
                //get emails
                String emailContactValue = email.getValue();
                //compare the twin
                boolean findTwin = false;
                int found = -1;
                if(twinEmails != null){
                    for(int i = 0;i<twinEmails.size();i++){
                        ContactItem emailPro = twinEmails.get(i);
                        String emailTwinValue = emailPro.getValue();
                        int emailProType = emailPro.getType();
                        String converteTwinEmailType = typeMatcher.matchJContactType(emailProType, JEMail.OTHER,TypeMatcher.TYPE_EMAIL_MATCHER);
                        if(convertedType.equalsIgnoreCase(converteTwinEmailType) && emailContactValue.trim().equalsIgnoreCase(emailTwinValue.trim())){
                            findTwin = true;
                            found = i;
                            break;
                        }
                    }
                }
                if(!findTwin){
                    return false;
                }else{
                    twinEmails.remove(found);
                }
            }
            mLogger.info("haveContactAndTwinSameEmailsInSamePosition : true");
        }


        return true;
    }

    public long getMaxLastupdate(long defaultSince,String userId){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = mContext.getSqlConnection();

            ps = conn.prepareStatement(SQL_GET_MAX_LAST_UPDATE_TIME);
            ps.setString(1,userId);

            rs = ps.executeQuery();

            if(rs.next()){
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(conn, ps, rs);
        }
        return defaultSince;
    }

    public void replaceSyncVersionWithSourceVersion(long principalId,String deviceId,String userId){
        mLogger.info("=============replaceSyncVersionWithSourceVersion");
        mLogger.info("=============principalId :" + principalId);
        mLogger.info("=============deviceId :" + deviceId);

        long syncSourceVersion = getSyncSourceVersion(userId);
        mLogger.info("===current syncSourceVersion: " + syncSourceVersion);
        long syncVersion = getSyncVersion(deviceId,principalId,userId);
        if(syncVersion != syncSourceVersion){
            syncVersion = syncSourceVersion;
            updateSyncVersion(deviceId, principalId, userId, syncVersion);
        }
    }

    public long getSyncSourceVersion(String userId){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_SYNC_SOURCE_VERSION);
            ps.setString(1, userId);

            rs = ps.executeQuery();
            if(rs.next()){
                long syncSourceVersion = rs.getLong(1);
                mLogger.info("====getSyncSourceVersion" + syncSourceVersion);
                return syncSourceVersion;
            }
        } catch (Exception e) {
            mLogger.info("========query device error :" + e.getMessage());
            e.printStackTrace();
        } finally{
            DBUtility.close(con,ps,rs);
        }
        return -1;
    }

    public long getSyncVersion(String deviceId,long principalId,String userId){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_SYNC_VERSION);
            ps.setString(1, userId);
            ps.setString(2, deviceId);
            ps.setLong(3, principalId);
            rs = ps.executeQuery();
            if(rs.next()){
                long syncVersion = rs.getLong(1);
                mLogger.info("====getSyncVersion" + syncVersion);
                return syncVersion;

            }
        } catch (Exception e) {
            mLogger.info("=========getSyncVersion error" + e.getMessage());
            e.printStackTrace();
        } finally{
            DBUtility.close(con,ps,rs);
        }
        return -1;
    }

    public void updateSyncVersion(String deviceId,long principalId,String userId,long syncVersion){
        mLogger.info("========updateSyncVersion===========");
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = mContext.getSqlConnection();
            ps = con.prepareStatement(SQL_UPDATE_SYNC_VERSION);
            ps.setLong(1,syncVersion);
            ps.setString(2, userId);
            ps.setString(3, deviceId);
            ps.setLong(4, principalId);
            mLogger.info("=========update sync_version by userid:" + userId + " ," +
                    "deviceId: " + deviceId + " ,principalId: " + principalId);
            int update = ps.executeUpdate();
            mLogger.info("=========update item:" + update);
        } catch (Exception e) {
            mLogger.info("==========update item error" + e.getMessage());
            e.printStackTrace();
        } finally{
            DBUtility.close(con,ps,null);
        }
    }

    public List<String> getSyncDevices(String userId){
        List<String> syncDevices = new ArrayList<String>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_DEVICE_VERSION_BY_USERNAME);
            ps.setString(1, userId);

            rs = ps.executeQuery();
            long syncSourceVersion = getSyncSourceVersion(userId);
            while(rs.next()){
                String deviceId = rs.getString(1);
                long syncVersion = rs.getLong(2);
                if(syncVersion == syncSourceVersion){
                    continue;
                }
                syncDevices.add(deviceId);
            }
        } catch (Exception e) {
            mLogger.info("========query device error :" + e.getMessage());
            e.printStackTrace();
        } finally{
            DBUtility.close(con,ps,rs);
        }
        mLogger.info("syncdevices: " + syncDevices.toString());
        return syncDevices;
    }


}
