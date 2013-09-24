package com.borqs.sync.server.common.providers;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.util.LogHelper;
import com.borqs.sync.server.common.util.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: b211
 * Date: 12-5-9
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class ContactMerge {
    
    private static final int TYPE_EMAIL = 1;
    private static final int TYPE_PHONE = 2;
    private static final int TYPE_OTHER = -1;

    private static final String SQL_STATUS_NOT_D = " AND status != 'D' ";
    private static final String SQL_STATUS_NO_BORQSID = " AND borqsid is null ";
    private static final String SQL_ORDER_BY_ID = " ORDER BY id";

    private static final String SQL_GET_POTENTIAL_TWINS =
            new StringBuilder("SELECT c.id, i.type as item_type, i.value as item_value ")
                    .append("FROM borqs_pim_contact c LEFT OUTER JOIN borqs_pim_contact_item i ")
                    .append("ON (c.id = i.contact) WHERE c.userid = ? AND ").toString();

    private Context mContext;
    private ContactProvider mContactProvider;
    private Logger mLogger;

    public ContactMerge(Context context){
        mContext = context;
        mContactProvider = new ContactProvider(mContext);
        mContactProvider.useLogger(mLogger);
    }
    
    public void setLogger(Logger logger){
        mLogger = logger;
    }

    /**
     * fill the item with last_update and private
     * @param c
     * @param timestamp
     * @param isPrivate
     */
    public void fillItemTimeAndPrivate(Contact c, long timestamp,boolean isPrivate){
        fillItemTimeAndPrivate(c.getTelephones(),timestamp,isPrivate);
        fillItemTimeAndPrivate(c.getEmails(),timestamp,isPrivate);
        fillItemTimeAndPrivate(c.getWebpages(),timestamp,isPrivate);
        fillItemTimeAndPrivate(c.getIms(),timestamp,isPrivate);
        fillItemTimeAndPrivate(c.getXTags(),timestamp,isPrivate);

        List<Address> addresses = c.getAddress();
        if(addresses != null){
            for(Address address:addresses){
                address.setLastUpdate(timestamp);
                address.setPrivate(isPrivate);
            }
        }
    }

    private void fillItemTimeAndPrivate(List<ContactItem> items, long timestamp, boolean isPrivate) {
        if (items != null) {
            for (ContactItem item : items) {
                item.setLastUpdate(timestamp);
                item.setPrivate(isPrivate);
            }
        }
    }

    /**
     *merge changed contact into target contact
     * @param changedContact the source(changed) contact
     * @param targetContact the contact you want to be merged
     * @return the merged contact
     */
    public Contact mergeFromPhone(Contact changedContact,Contact targetContact,long timestamp){
        fillItemTimeAndPrivate(changedContact,timestamp,true);
        targetContact = mergeCommon(changedContact,targetContact,true);
        targetContact = mergeFromPhoneName(changedContact,targetContact);
        targetContact = mergeEmail(changedContact,targetContact,true);
        targetContact = mergeIM(changedContact,targetContact,true);
        targetContact = mergePhone(changedContact,targetContact,true);
        targetContact = mergeWeb(changedContact,targetContact,true);
        targetContact = mergeXTag(changedContact,targetContact,true);
        targetContact = mergeFromPhoneAddress(changedContact, targetContact, true);
        return targetContact;
    }
    
    public Contact mergeFromAccount(Contact changedContact,Contact targetContact,long timestamp){
        fillItemTimeAndPrivate(changedContact, timestamp,false);
        targetContact = mergeCommon(changedContact,targetContact,false);
        targetContact = mergeFromAccountName(changedContact,targetContact);
        targetContact = mergeEmail(changedContact,targetContact,false);
        targetContact = mergeIM(changedContact,targetContact,false);
        targetContact = mergePhone(changedContact,targetContact,false);
        targetContact = mergeWeb(changedContact,targetContact,false);
        targetContact = mergeXTag(changedContact,targetContact,false);
        targetContact = mergeFromAccountAddress(changedContact,targetContact,false);
        return targetContact;
    }
    
    private Contact mergeFromPhoneAddress(Contact changedContact,Contact targetContact,boolean fromPhone){
        List<Address> changedContactAddresses = changedContact.getAddress();
        List<Address> targetContactAddresses = targetContact.getAddress();

        Map<String,Address> changedAddressMap = new HashMap<String, Address>();

        if(changedContactAddresses != null){
            for(Address changedAddress:changedContactAddresses){
                changedAddressMap.put(changedAddress.toValue(),changedAddress);
            }
        }

        if(targetContactAddresses != null){
            for(Address targetAddress:targetContactAddresses){
                if(!targetAddress.isPrivate()){
                    changedAddressMap.put(targetAddress.toValue(),targetAddress);
                }
            }
        }
        List<Address> newItems = new ArrayList<Address>();
        Collection<Address> itemCollections = changedAddressMap.values();
        for(Address item:itemCollections){
            newItems.add(item);
        }

        if(targetContact.getAddress() != null){
            targetContact.getAddress().clear();
        }
        targetContact.setAddress(newItems);
        return targetContact;
    }

    private Contact mergeFromAccountAddress(Contact changedContact,Contact targetContact,boolean fromPhone){
        List<Address> changedContactAddresses = changedContact.getAddress();
        List<Address> targetContactAddresses = targetContact.getAddress();

        Map<String,Address> changedAddressMap = new HashMap<String, Address>();

        if(changedContactAddresses != null){
            for(Address changedAddress:changedContactAddresses){
                changedAddressMap.put(changedAddress.toValue(),changedAddress);
            }
        }

        if(targetContactAddresses != null){
            for(Address targetAddress:targetContactAddresses){
                if(targetAddress.isPrivate() && !changedAddressMap.containsKey(targetAddress.toValue())){
                    changedAddressMap.put(targetAddress.toValue(),targetAddress);
                }
            }
        }
        List<Address> newItems = new ArrayList<Address>();
        Collection<Address> itemCollections = changedAddressMap.values();
        for(Address item:itemCollections){
            newItems.add(item);
        }

        if(targetContact.getAddress() != null){
            targetContact.getAddress().clear();
        }
        targetContact.setAddress(newItems);
        return targetContact;
    }
    
    private Contact mergeXTag(Contact changedContact,Contact targetContact,boolean fromPhone){
        //TODO should handle xtag when parse ProfileRecord(maybe)
        if(fromPhone){
            targetContact.setXTags(changedContact.getXTags());
        }else{
            //group sync is a seperated operation
            List<ContactItem> xtags = targetContact.getXTags();
            List<ContactItem> newXTags = new ArrayList<ContactItem>();
            for(ContactItem item:xtags){
                if(item.getType() != ContactItem.TYPE_X_TAG_GROUP){
                    newXTags.add(item);
                }
            }
            if(targetContact.getXTags() != null){
                targetContact.getXTags().clear();
            }
            targetContact.setXTags(newXTags);
        }
        return targetContact;
    }

    private Contact mergeCommon(Contact changedContact, Contact targetContact, boolean fromPhone) {
        targetContact.setBody(changedContact.getBody());
        targetContact.setBirthday(changedContact.getBirthday());
        targetContact.setCompanies(changedContact.getCompanies());
        targetContact.setCompany(changedContact.getCompany());
        targetContact.setDepartment(changedContact.getDepartment());
        targetContact.setJobTitle(changedContact.getJobTitle());
        if (!fromPhone) {
            //do not support sync between phone and syncserver
            targetContact.setGender(changedContact.getGender());
            targetContact.setBorqsId(changedContact.getBorqsId());
            targetContact.setBorqsName(changedContact.getBorqsName());
        }
        targetContact.setNickName(changedContact.getNickName());
        targetContact.setPhoto(changedContact.getPhoto());
        targetContact.setLastUpdate(changedContact.getLastUpdate());
        targetContact.setPhotoType(changedContact.getPhotoType());
        return targetContact;
    }

    private Contact mergeFromAccountName(Contact changedContact,Contact targetContact){
        String firstName = Utility.isEmpty(targetContact.getFirstName())?"":targetContact.getFirstName();
        String middleName = Utility.isEmpty(targetContact.getMiddleName())?"":targetContact.getMiddleName();
        String lastName = Utility.isEmpty(targetContact.getLastName())?"":targetContact.getLastName();
        String bFirstName = Utility.isEmpty(targetContact.getBFirstName())?"":targetContact.getBFirstName();
        String bMiddleName = Utility.isEmpty(targetContact.getBMiddleName())?"":targetContact.getBMiddleName();
        String bLastName = Utility.isEmpty(targetContact.getBLastName())?"":targetContact.getBLastName();
        String name = firstName.trim() + middleName.trim() + lastName.trim();
        String bName = bFirstName.trim() + bMiddleName.trim() + bLastName.trim();
        if(Utility.isEmpty(name) || name.equals(bName)){
            targetContact.setFirstName(changedContact.getBFirstName());
            targetContact.setMiddleName(changedContact.getBMiddleName());
            targetContact.setLastName(changedContact.getBLastName());
        }

        targetContact.setBFirstName(changedContact.getBFirstName());
        targetContact.setBMiddleName(changedContact.getBMiddleName());
        targetContact.setBLastName(changedContact.getBLastName());
        targetContact.setBorqsName(changedContact.getBorqsName());
        targetContact.setDisplayName(changedContact.getDisplayName());
        return targetContact;
    }

    private Contact mergeFromPhoneName(Contact changedContact,Contact targetContact){
        targetContact.setFirstName(changedContact.getFirstName());
        targetContact.setMiddleName(changedContact.getMiddleName());
        targetContact.setLastName(changedContact.getLastName());
        return targetContact;
    }

    private Contact mergePhone(Contact changedContact,Contact targetContact,boolean fromPhone){
        List<ContactItem> changedContactTelephones = changedContact.getTelephones();
        List<ContactItem> targetContactTelephones = targetContact.getTelephones();
        List<ContactItem> newPhones;
        if(fromPhone){
            newPhones = getFromPhoneMergedItems(changedContactTelephones,targetContactTelephones);
        }else{
            newPhones = getFromAccountMergedItems(changedContactTelephones,targetContactTelephones,TYPE_PHONE);
        }
        if(targetContact.getTelephones() != null){
            targetContact.getTelephones().clear();
        }
        targetContact.setTelephones(newPhones);
        return targetContact;
    }

    private Contact mergeEmail(Contact changedContact,Contact targetContact,boolean fromPhone){
        List<ContactItem> changedEmails = changedContact.getEmails();
        List<ContactItem> targetEmails = targetContact.getEmails();
        List<ContactItem> newEmails ;
        if(fromPhone){
            newEmails = getFromPhoneMergedItems(changedEmails,targetEmails);
        }else{
            newEmails = getFromAccountMergedItems(changedEmails,targetEmails,TYPE_EMAIL);
        }
        if(targetContact.getEmails() != null){
            targetContact.getEmails().clear();
        }
        targetContact.setEmails(newEmails);
        return targetContact;
    }

    private Contact mergeIM(Contact changedContact,Contact targetContact,boolean fromPhone){
        List<ContactItem> changedContactIms = changedContact.getIms();
        List<ContactItem> targetContactIms = targetContact.getIms();
        List<ContactItem> newIms;
        if(fromPhone){
            newIms =  getFromPhoneMergedItems(changedContactIms,targetContactIms);
        }else{
            newIms = getFromAccountMergedItems(changedContactIms,targetContactIms,TYPE_OTHER);
        }
        if(targetContact.getIms() != null){
            targetContact.getIms().clear();
        }
        targetContact.setIms(newIms);
        return targetContact;
    }

    private Contact mergeWeb(Contact changedContact,Contact targetContact,boolean fromPhone){
        List<ContactItem> changedContactWebpages = changedContact.getWebpages();
        List<ContactItem> targetContactWebpages = targetContact.getWebpages();
        List<ContactItem> newWebs ;
        if(fromPhone){
            newWebs =  getFromPhoneMergedItems(changedContactWebpages,targetContactWebpages);
        }else{
            newWebs = getFromAccountMergedItems(changedContactWebpages,targetContactWebpages,TYPE_OTHER);
        }

        if(targetContact.getWebpages() != null){
            targetContact.getWebpages().clear();
        }
        targetContact.setWebpages(newWebs);
        return targetContact;
    }

    private List<ContactItem> getFromAccountMergedItems(List<ContactItem> changedItems, List<ContactItem> targetItems, int type) {
        List<ContactItem> newItems = new ArrayList<ContactItem>();

        //1.get the newest item from account
        Map<String, ContactItem> changedItemMap = new HashMap<String, ContactItem>();
        if (changedItems != null) {
            for (ContactItem changedItem : changedItems) {
                if (type == TYPE_EMAIL) {
                    changedItemMap.put(Utility.getValidEmail(changedItem.getValue()), changedItem);
                } else if (type == TYPE_PHONE) {
                    changedItemMap.put(Utility.cleanPhoneNumber(changedItem.getValue()), changedItem);
                } else {
                    changedItemMap.put(changedItem.getValue(), changedItem);
                }
            }
        }

        //2.merge the private item into newest item.
        if (targetItems != null) {
            for (ContactItem targetItem : targetItems) {
                if(targetItem.isPrivate()){
                    if (type == TYPE_EMAIL) {
                        if (!changedItemMap.containsKey(Utility.getValidEmail(targetItem.getValue()))) {
                            changedItemMap.put(Utility.getValidEmail(targetItem.getValue()), targetItem);
                        }
                    } else if (type == TYPE_PHONE) {
                        if (!changedItemMap.containsKey(Utility.cleanPhoneNumber(targetItem.getValue()))) {
                            changedItemMap.put(Utility.cleanPhoneNumber(targetItem.getValue()), targetItem);
                        }
                    } else {
                        if (!changedItemMap.containsKey(targetItem.getValue())) {
                            changedItemMap.put(targetItem.getValue(), targetItem);
                        }
                    }
                }
            }
        }

        //3.the newItems is the merged items,return it
        Collection<ContactItem> itemCollections = changedItemMap.values();
        for (ContactItem item : itemCollections) {
            newItems.add(item);
        }
        return newItems;
    }
    
    private List<ContactItem> getFromPhoneMergedItems(List<ContactItem> changedItems,List<ContactItem> targetItems){
        List<ContactItem> newItems = new ArrayList<ContactItem>();

        //1.get the newest item from phone
        Map<String,ContactItem> changedItemMap = new HashMap<String, ContactItem>();
        if(changedItems != null){
            for(ContactItem changedItem:changedItems){
                changedItemMap.put(changedItem.getValue(), changedItem);
            }
        }

        //2.merge the private item into newest item.
        if(targetItems != null){
            for(ContactItem targetItem:targetItems){
                if(!targetItem.isPrivate()){
                    changedItemMap.put(targetItem.getValue(), targetItem);
                }else if(changedItemMap.containsKey(targetItem.getValue())){
                    changedItemMap.put(targetItem.getValue(),targetItem);
                }
            }
        }

        //3.the newItems is the merged items,return it
        Collection<ContactItem> itemCollections = changedItemMap.values();
        for(ContactItem item:itemCollections){
            newItems.add(item);
        }
        return newItems;
    }

    public Contact getSameContact(Contact changedContact){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String firstName = Utility.isEmpty(changedContact.getBFirstName())?"":changedContact.getBFirstName();
        String middleName = Utility.isEmpty(changedContact.getBMiddleName())?"":changedContact.getBMiddleName();
        String lastName = Utility.isEmpty(changedContact.getBLastName())?"":changedContact.getBLastName();

        List<String> params = new ArrayList<String>();

        StringBuilder query = new StringBuilder(SQL_GET_POTENTIAL_TWINS);

        StringBuilder nameSB = new StringBuilder();
        nameSB.append(lastName.trim()).append(middleName.trim()).append(firstName.trim());

        query.append(" lower(concat(trim(ifnull(c.last_name,'')),trim(ifnull(c.middle_name,'')),trim(ifnull(c.first_name,'')))) = ? ");

        query.append(SQL_STATUS_NOT_D);
        query.append(SQL_STATUS_NO_BORQSID);
        query.append(SQL_ORDER_BY_ID);
        mLogger.info("getSameContact,query same contact sql statement:" + query.toString());
        params.add(changedContact.getOwnerId());
        params.add(nameSB.toString().toLowerCase());
        mLogger.info("getSameContact params:" + params.toString());
        try {
            con = mContext.getSqlConnection();
            ps = con.prepareStatement(query.toString());
            int cont = 1;
            for (String param : params) {
                ps.setString(cont++, param);
            }
            rs = ps.executeQuery();
            List<ContactItem> phones = new ArrayList<ContactItem>();
            List<ContactItem> emails = new ArrayList<ContactItem>();
            while(rs.next()){
                ContactItem item = new ContactItem();
                item.setContact(rs.getLong(1));
                item.setType(rs.getInt(2));
                item.setValue(rs.getString(3));

                switch(rs.getInt(2)){
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
                    case ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER:
                    case ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER:
                    case ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER:
                    case ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER:
                        phones.add(item);
                        break;
                    case ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS:
                    case ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS:
                    case ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS:
                        emails.add(item);
                        break;
                }
            }
            //check the same phone or same email
            ContactItem samePhoneItem = getSamePhone(changedContact.getTelephones(), phones);
            ContactItem sameEmailItem = getSameEmail(changedContact.getEmails(), emails);
            boolean isSame = (samePhoneItem != null || sameEmailItem != null);
            if(isSame){
                long contactId = samePhoneItem != null?samePhoneItem.getContact():sameEmailItem.getContact();
                Contact sameContact = mContactProvider.getItem(String.valueOf(contactId));
                return sameContact;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DBUtility.close(con,ps,rs);
        }
        return null;
    }

    private ContactItem getSamePhone(List<ContactItem> changedPhones, List<ContactItem> targetPhones) {
        if (changedPhones != null && targetPhones != null) {
            for (ContactItem serverItem : changedPhones) {
                for (ContactItem localItem : targetPhones) {
                    if (serverItem.getValue() != null && localItem.getValue() != null
                            && Utility.cleanPhoneNumber(serverItem.getValue())
                            .equals(Utility.cleanPhoneNumber(localItem.getValue()))) {
                        return localItem;
                    }
                }
            }
        }
        return null;
    }

    private ContactItem getSameEmail(List<ContactItem> changedEmails, List<ContactItem> targetEmails) {
        if (changedEmails != null && targetEmails != null) {
            for (ContactItem serverItem : changedEmails) {
                for (ContactItem localItem : targetEmails) {
                    if (serverItem.getValue() != null
                            && localItem.getValue() != null
                            && Utility.getValidEmail(serverItem.getValue()).equals(Utility.getValidEmail(localItem.getValue()))) {
                        return localItem;
                    }
                }
            }
        }
        return null;
    }
}
