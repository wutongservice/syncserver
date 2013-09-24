package com.borqs.sync.server.common.profilesuggestion;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.datamining.*;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.LogHelper;
import com.borqs.sync.server.common.util.Utility;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/24/12
 * Time: 11:38 AM
 */
public class ProfileSuggestionGenerator {

    private static final int CHANGE_TYPE_PHONE  = 1;
    private static final int CHANGE_TYPE_EMAIL  = 2;

    private static final int TEL_ITEM_LIMIT_SIZE = 3;
    private static final int EMAIL_ITEM_LIMIT_SIZE = 3;


    private ContactDataMiningAdapter mContactDataMiningAdapter;
    private AccountManager mAccountManager;
    private Logger mLogger;
    private Context mContext;

    public ProfileSuggestionGenerator(Context context){
        mContext = context;
        mContactDataMiningAdapter = new ContactDataMiningAdapter(context);
        mAccountManager = new AccountManager(context);
    }
    
    public void setLogger(Logger logger){
        mLogger = logger;
        mContactDataMiningAdapter.setLogger(mLogger);
        mAccountManager.setLogger(mLogger);
    }

    /**
     * generate the contact's change by borqsid
     * @param borqsId
     * @return the contact's change,null if there is no change
     */
    public String generateChangeRequest(String borqsId){
        RealTimeIntegrationOperation operation = new RealTimeIntegrationOperation(mContext);
        operation.setLogger(mLogger);
        IntegrationProfile profile = operation.generateIntegrationProfile(borqsId);

        Contact account = null;
        try {
            account = mAccountManager.getAccountContact(borqsId,borqsId);
        } catch (DataAccessError e) {
            e.printStackTrace();
        }

        if(account != null){
            try {
                ProfileSuggestionBuilder builder = collectChangeRequest(profile,account);
                if(builder.hasChange()){
                    return builder.compose();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * public for test
     * @param profile
     * @param account
     * @return
     */
    public ProfileSuggestionBuilder collectChangeRequest(IntegrationProfile profile,Contact account) {
        ProfileSuggestionBuilder requestBuilder = new ProfileSuggestionBuilder();
        // contact
        collectNameChange(account, profile.getNames(), requestBuilder);
        // // compare address
        collectAddressChange(profile, account, requestBuilder);
        // compare contactitem
        collectItemChange(profile, account, requestBuilder);
        // compare photo
        // collectPhotoChange(contact, account, changedContactDetail);

        return requestBuilder;
    }

    private void collectNameChange(Contact account, List<IntegrationProfileName> names,
                                   ProfileSuggestionBuilder requestBuilder) {
        List<IntegrationProfileName> newNames = removeSelfSameName(names);

        if(names != null){
            List<String> addedNames = new ArrayList<String>();
            for(IntegrationProfileName profileName:newNames){
                String contactDisplayName = generateDisplayName(profileName.getFirstName(),
                        profileName.getMiddleName(),profileName.getLastName());
                String contactBDisplayName = generateDisplayName(profileName.getBFirstName(),
                        profileName.getBMiddleName(),profileName.getBLastName());
                //set the name then send the contact's detail name to client.
                String accountDisplayName = generateDisplayName(account.getBFirstName(),
                        account.getBMiddleName(),account.getBLastName());
                if(!Utility.isEmpty(contactDisplayName)
                        && Utility.isChinese(contactDisplayName)
                        && contactDisplayName.length() > 1
                        && !Utility.stringEqual(contactDisplayName,contactBDisplayName)
                        && !Utility.stringEqual(contactDisplayName,accountDisplayName)){
                    LogHelper.logInfo(mLogger,"=====add name suggestion:" + contactDisplayName);
                    if(!addedNames.contains(contactDisplayName)){
                        requestBuilder.addName(profileName);
                        addedNames.add(contactDisplayName);
                    }

                }
            }
        }
    }

    private void collectAddressChange(IntegrationProfile profile, Contact account,
                                      ProfileSuggestionBuilder requestBuilder){
        List<IntegrationProfileAddress> contactAddress = profile.getAddresses();
        List<Address> accountAddress = account.getAddress();
        if (Utility.isEmptyList(contactAddress) && Utility.isEmptyList(accountAddress)) {
            // no change
        } else if (Utility.isEmptyList(contactAddress) && !Utility.isEmptyList(accountAddress)) {
            // account's phone exist,but contact's phone is empty. (delete
            // contact's phone item)
        } else if (!Utility.isEmptyList(contactAddress) && Utility.isEmptyList(accountAddress)) {
            // contact's phone exist,but account's phone is empty
            LogHelper.logInfo(mLogger,"account's address is empty and contact is not");
            addValidAddressChangeRequest(requestBuilder, contactAddress);
        } else if (!Utility.isEmptyList(contactAddress) && !Utility.isEmptyList(accountAddress)) {
            // add/delete/change the phone item from device
            LogHelper.logInfo(mLogger,"contact and account's address is not empty");
            collectChangedAddress(contactAddress, accountAddress, requestBuilder);
        }
    }

    private void  collectChangedAddress(List<IntegrationProfileAddress> contactAddresses, List<Address> accountAddresses,
                                        ProfileSuggestionBuilder requestBuilder){
        //1.remove self same
        List<IntegrationProfileAddress> newContactAddress = removeSelfSameContactAddress(contactAddresses);
        List<Address> newAccountAddress = removeSelfSameAddress(accountAddresses);

        //2.remove the same both contact and account
        for (int i = 0; i < newContactAddress.size(); i++) {
            for (int j = 0; j < newAccountAddress.size(); j++) {
                IntegrationProfileAddress contactAddress = newContactAddress.get(i);
                Address accountAddress = newAccountAddress.get(j);
                    if (Utility.stringEqual(contactAddress.toValue(),accountAddress.toValue())) {
                        newContactAddress.remove(i);
                        newAccountAddress.remove(j);
                        LogHelper.logInfo(mLogger,"======the same address item ,value: " + contactAddress.toValue());
                        i = i - 1;
                        break;
                }
            }
        }
        addValidAddressChangeRequest(requestBuilder,newContactAddress);
    }

    private void addValidAddressChangeRequest(ProfileSuggestionBuilder requestBuilder,List<IntegrationProfileAddress> contactAddress){
        for(IntegrationProfileAddress addr:contactAddress){
            if(addr.isPrivate()
                    && addr.getLastUpdate() > 0
                    && !Utility.isEmpty(addr.toValue())){
                LogHelper.logInfo(mLogger,"=====add Address suggestion:" + addr.toValue());
                requestBuilder.addAddress(addr);
            }
        }
    }

    private List<Address> removeSelfSameAddress(List<Address> addresses){
        Map<String,Address> itemMap = new HashMap<String, Address>();
        for(Address item:addresses){
            itemMap.put(item.toValue(),item);
        }
        Collection<Address> itemValues = itemMap.values();
        List<Address> newItems = new ArrayList<Address>();
        for(Address item:itemValues){
            newItems.add(item);
        }
        return newItems;
    }


    private List<IntegrationProfileAddress> removeSelfSameContactAddress(List<IntegrationProfileAddress> addresses){
        Map<String,IntegrationProfileAddress> itemMap = new HashMap<String, IntegrationProfileAddress>();
        for(IntegrationProfileAddress item:addresses){
            itemMap.put(item.toValue(),item);
        }
        Collection<IntegrationProfileAddress> itemValues = itemMap.values();
        List<IntegrationProfileAddress> newItems = new ArrayList<IntegrationProfileAddress>();
        for(IntegrationProfileAddress item:itemValues){
            newItems.add(item);
        }
        return newItems;
    }

    private void collectItemChange(IntegrationProfile profile, Contact account,
                                   ProfileSuggestionBuilder requestBuilder) {
        collectPhoneItemChange(profile, account, requestBuilder);
        collectEmailItemChange(profile, account, requestBuilder);
        // collectWebPageItemChange(contact.getWebpages(),
        // account.getWebpages(),
        // changeStruct);
    }

    private void collectPhoneItemChange(IntegrationProfile profile, Contact account, ProfileSuggestionBuilder requestBuilder) {
        List<IntegrationProfileItem> contactPhones = profile.getPhones();
        List<ContactItem> accountPhones = account.getTelephones();
        if (Utility.isEmptyList(contactPhones) && Utility.isEmptyList(accountPhones)) {
            // no change
        } else if (Utility.isEmptyList(contactPhones) && !Utility.isEmptyList(accountPhones)) {
            // account's phone exist,but contact's phone is empty. (delete
            // contact's phone item)
        } else if (!Utility.isEmptyList(contactPhones) && Utility.isEmptyList(accountPhones)) {
            // contact's phone exist,but account's phone is empty
            LogHelper.logInfo(mLogger,"account's phone is empty and contact is not");
            addValidPhoneChangeRequest(requestBuilder,contactPhones,account);
        } else if (!Utility.isEmptyList(contactPhones) && !Utility.isEmptyList(accountPhones)) {
            // add/delete/change the phone item from device
            LogHelper.logInfo(mLogger,"contact and account's phone is not empty");
            collectChangedItem(requestBuilder, contactPhones, accountPhones,account,CHANGE_TYPE_PHONE);
        }
    }

    private void collectEmailItemChange(IntegrationProfile profile,Contact account, ProfileSuggestionBuilder requestBuilder) {
        List<IntegrationProfileItem> contactEmails = profile.getEmails();
        List<ContactItem> accountEmails = account.getEmails();
        if (Utility.isEmptyList(contactEmails) && Utility.isEmptyList(accountEmails)) {
            // no change
        } else if (Utility.isEmptyList(contactEmails) && !Utility.isEmptyList(accountEmails)) {
            // account's email exist,but contact's email is empty. (delete
            // contact's email item)
        } else if (!Utility.isEmptyList(contactEmails) && Utility.isEmptyList(accountEmails)) {
            // contact's email exist,but account's email is empty
            LogHelper.logInfo(mLogger,"account's email is empty and contact is not");
            addValidEmailChangeRequest(requestBuilder,contactEmails,account);
        } else if (!Utility.isEmptyList(contactEmails) && !Utility.isEmptyList(accountEmails)) {
            // add/delete/change the email item from device
            LogHelper.logInfo(mLogger,"contact and account's email is not empty");
            collectChangedItem(requestBuilder, contactEmails, accountEmails,account,CHANGE_TYPE_EMAIL);
        }
    }

    private void collectChangedItem(ProfileSuggestionBuilder requestBuilder, List<IntegrationProfileItem> contactItems,
                                    List<ContactItem> accountItems, Contact account, int type) {
        //1.remove self same
        List<IntegrationProfileItem> newContactItems = removeSelfSameContactItem(contactItems);
        List<ContactItem> newAccountItems = removeSelfSameItem(accountItems);

        //2.remove the same both contact and account
        for (int i = 0; i < newContactItems.size(); i++) {
            for (int j = 0; j < newAccountItems.size(); j++) {
                IntegrationProfileItem contactItem = newContactItems.get(i);
                ContactItem accountItem = newAccountItems.get(j);
                int contactType = contactItem.getType();
                if (CHANGE_TYPE_PHONE == type) {
                    if (Utility.stringEqual(Utility.cleanPhoneNumber(contactItem.getValue()),
                            Utility.cleanPhoneNumber(accountItem.getValue()))) {
                        newContactItems.remove(i);
                        newAccountItems.remove(j);
                        LogHelper.logInfo(mLogger,"======the same mobile phone item ,type: " + contactType + ",value: " + contactItem.getValue());
                        i = i - 1;
                        break;
                    }
                } else if (CHANGE_TYPE_EMAIL == type) {
                    if (Utility.stringEqual(Utility.getValidEmail(contactItem.getValue()).toLowerCase(),
                            Utility.getValidEmail(accountItem.getValue()).toLowerCase())) {
                        newContactItems.remove(i);
                        newAccountItems.remove(j);
                        LogHelper.logInfo(mLogger,"======the same email item ,type: " + contactType + ",value: " + contactItem.getValue());
                        i = i - 1;
                        break;
                    }
                }
            }
        }
        if(CHANGE_TYPE_PHONE == type){
            addValidPhoneChangeRequest(requestBuilder, newContactItems, account);
        }else if(CHANGE_TYPE_EMAIL == type){
            addValidEmailChangeRequest(requestBuilder, newContactItems, account);
        }

    }

    private List<ContactItem> removeSelfSameItem(List<ContactItem> items){
        Map<String,ContactItem> itemMap = new HashMap<String, ContactItem>();
        for(ContactItem item:items){
            itemMap.put(item.getValue().toLowerCase(),item);
        }
        Collection<ContactItem> itemValues = itemMap.values();
        List<ContactItem> newItems = new ArrayList<ContactItem>();
        for(ContactItem item:itemValues){
            newItems.add(item);
        }
        return newItems;
    }

    private List<IntegrationProfileItem> removeSelfSameContactItem(List<IntegrationProfileItem> items){
        Map<String,IntegrationProfileItem> itemMap = new HashMap<String, IntegrationProfileItem>();
        for(IntegrationProfileItem item:items){
            itemMap.put(item.getValue().toLowerCase(),item);
        }
        Collection<IntegrationProfileItem> itemValues = itemMap.values();
        List<IntegrationProfileItem> newItems = new ArrayList<IntegrationProfileItem>();
        for(IntegrationProfileItem item:itemValues){
            newItems.add(item);
        }
        return newItems;
    }

    private List<IntegrationProfileName> removeSelfSameName(List<IntegrationProfileName> items){
        Map<String,IntegrationProfileName> itemMap = new HashMap<String, IntegrationProfileName>();
        for(IntegrationProfileName item:items){
            itemMap.put(generateDisplayName(item.getFirstName(),item.getMiddleName(),item.getLastName())
                    + generateDisplayName(item.getBFirstName(),item.getBMiddleName(),item.getBLastName()),item);
        }
        Collection<IntegrationProfileName> itemValues = itemMap.values();
        List<IntegrationProfileName> newItems = new ArrayList<IntegrationProfileName>();
        for(IntegrationProfileName item:itemValues){
            newItems.add(item);
        }
        return newItems;
    }
    
    private String generateDisplayName(String firstName,String middleName,String lastName){
        String contactFirstName = firstName == null ? "" : firstName.trim();
        String contactMiddleName = middleName == null ? "" : middleName.trim();
        String contactLastName = lastName == null ? "" : lastName.trim();
        String displayName = contactLastName+contactMiddleName+contactFirstName;
        return displayName;
    }

    private void addValidEmailChangeRequest(ProfileSuggestionBuilder requestBuilder, List<IntegrationProfileItem> emails, Contact account) {
        int size = 0;
        if(account.getEmails() != null){
            size = account.getEmails().size();
        }
        for (IntegrationProfileItem email : emails) {
            if (size < EMAIL_ITEM_LIMIT_SIZE) {
                if (email.isPrivate()
                        && email.getLastUpdate() > 0
                        && !Utility.isEmpty(email.getValue())
                        && Utility.isValidEmail(email.getValue())
                        && !isBoundEmail(email.getValue(), account)) {
                    LogHelper.logInfo(mLogger,"=====add email suggestion:" + Utility.getValidEmail(email.getValue()));
                    email.setValue(Utility.getValidEmail(email.getValue()));
                    requestBuilder.addEmail(email);
                    size++;
                }
            }
        }
    }

    private void addValidPhoneChangeRequest(ProfileSuggestionBuilder requestBuilder, List<IntegrationProfileItem> phones, Contact account) {
        int size = 0;
        if(account.getTelephones() != null){
            size = account.getTelephones().size();
        }
        for (IntegrationProfileItem phone : phones) {
            if (size < TEL_ITEM_LIMIT_SIZE) {
                if (phone.isPrivate()
                        && phone.getLastUpdate() > 0
                        && !Utility.isEmpty(phone.getValue())
                        && Utility.isValidTel(phone.getValue())
                        && !isBoundPhone(phone.getValue(), account)) {
                    LogHelper.logInfo(mLogger,"=====add phone suggestion:" + phone.getValue());
                    phone.setValue(Utility.getValidTel(phone.getValue()));
                    requestBuilder.addPhone(phone);
                    size++;
                }
            }
        }
    }

    private boolean isBoundPhone(String phone,Contact account){
        List<String> loginPhones = account.getLoginPhones();

        for(String loginPhone:loginPhones){
            if(Utility.stringEqual(Utility.cleanPhoneNumber(loginPhone),Utility.cleanPhoneNumber(phone))){
                return true;
            }
        }
        return false;
    }

    private boolean isBoundEmail(String email,Contact account){
        List<String> loginEmails = account.getLoginEmails();
        if(loginEmails != null){
            for(String loginEmail:loginEmails){
                if(Utility.stringEqual(Utility.getValidEmail(loginEmail).toLowerCase(),Utility.getValidEmail(email).toLowerCase())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasChange(String borqsId) {
        return !Utility.isEmpty(generateChangeRequest(borqsId));
    }
}
