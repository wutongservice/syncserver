package com.borqs.sync.server.rpc.service.datasync.syncML;

import com.borqs.sync.avro.XResponse;
import com.borqs.sync.server.common.mq.MQConnection;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactMerge;
import com.borqs.sync.server.common.providers.ContactProvider;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.service.SocialContactSyncServiceImpl;
import com.borqs.sync.server.rpc.service.datasync.RpcServiceLogger;
import com.borqs.sync.server.syncml.converter.JContactConverter;
import com.borqs.sync.server.syncml.dao.ContactDAO;
import com.borqs.sync.server.syncml.push.PushSyncHelper;
import com.borqs.sync.server.syncml.util.ContactLogger;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 8/8/12
 * Time: 10:15 AM
 * sync service for Sync logic
 */
public class SyncService {

    private static final String MQ_CONTACTS_MAPPINGS_CHANGED = "SyncContacts.onContactsMappingsChanged";

    private Context mContext;
    private ContactProvider mContactProvider;
    private ContactDAO mContactDAO;
    private Logger mLog;
    private SyncManager mSyncManager;

    public SyncService(Context context){
        mContext = context;
        mContactProvider = new ContactProvider(context);
        mContactDAO = new ContactDAO(context);
        mLog = RpcServiceLogger.getLogger(context);
        mContactProvider.useLogger(mLog);
        mSyncManager = new SyncManager();
    }

    private long caculateSynctimestamp(long origSince){
        long timestamp = origSince;
        //sync from account,set TimeStamp is used for the last_update of every Contact.
        if(timestamp <= 0){
            //slow sync/refresh from server/refresh required
            timestamp = System.currentTimeMillis()-1000;
        } else {
            timestamp = timestamp - 1; //make sure all the change from account will be in the sync range of [mTimestampOfsync, ...]
        }

        return timestamp;
    }

    /**
     * sync logic :begin since
     * @param userId
     * @param syncMode
     * @param since
     */
    public void beginSync(String userId,String syncMode,long since){
        try{
            long syncTimestamp = caculateSynctimestamp(since);

            SocialContactSyncServiceImpl impl = new SocialContactSyncServiceImpl(mContext);
            impl.syncFromAccount(userId, syncTimestamp, 0);

            //Manage the sync process prevent from syncing with same user at the same time
            mSyncManager.beginSyncOf(userId,syncTimestamp);

            mLog.info("beginsync finished ,synced contacts from account to syncml");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * sync logic,end sync
     * @param userId
     * @param syncMode
     * @param since
     * @param principlaId
     * @param deviceId
     */
    public void endSync(String userId, String syncMode, long since,long principlaId,String deviceId) {
        try {
            SyncContext syncContext = mSyncManager.getSyncContext(userId);
            if (syncContext != null) {
                if (syncContext.hasClientUpdate()) {

                    SocialContactSyncServiceImpl impl = new SocialContactSyncServiceImpl(mContext);
                    impl.syncToAccount(userId, syncContext.getSince(), 0);
                }
            } else {
                mLog.warning(userId + " end sync,the SyncContext is null,never should be here.");
            }

            //end sync ,we schedule push for notifying other devices with the same user to sync
            onSyncEnd(principlaId,deviceId,userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSyncManager.endSyncOf(userId);
    }

    /**
     * add contact item
     * @param userid
     * @param contactJson the JPIM format contact json
     * @param since
     * @return  the added contact id
     */
    public long addItem(String userid,String contactJson,long since){

        Contact contact = JContactConverter.toContact(contactJson);
        contact.setLastUpdate(since);
        contact.setOwnerId(userid);
        contact.setBorqsId(null);//remove the borqsid ,never should be here-->(the borqsid is not null)

        long addedId = mContactProvider.insertItem(contact);
        SyncContext syncContext = mSyncManager.getSyncContext(userid);
        if (syncContext != null ) {
            if(!syncContext.hasClientChange()){
                syncContext.setClientChange(addedId > 0);
            }
        } else {
            mLog.warning(userid + " addItem,the SyncContext is null,never should be here.");
        }

        return addedId ;
    }

    /**
     * get the contact json by id
     * @param contactId
     * @return  the JPIM format contact json
     */
    public String getItem(String contactId){
        Contact contact = mContactProvider.getItem(contactId);
        return JContactConverter.toContactJson(contact);
    }

    /**
     * update item
     * @param userid
     * @param contactId
     * @param contactJson the JPIM format contact json
     * @param since
     * @return if update successfully
     */
    public boolean  updateItem(String userid, String contactId, String contactJson, long since) {

        boolean updated = false;
        try {
            Contact contact = JContactConverter.toContact(contactJson);
            contact.setLastUpdate(since);
            contact.setOwnerId(userid);

            //merge client contacts into sync contacts (separate contact item into private and public)
            contact = mergeContact(contact, since, contactId);

            updated = mContactProvider.updateItem(Long.parseLong(contactId), contact);

            //mark as updated ,then we need sync the updated to account(currently,means change request)
            SyncContext syncContext = mSyncManager.getSyncContext(userid);
            if (syncContext != null) {
                if (syncContext != null ) {
                    if(!syncContext.hasClientChange()){
                        syncContext.setClientUpdate(updated);
                        syncContext.setClientChange(updated);
                    }
                }
            } else {
                mLog.warning(userid + " updateItem,the SyncContext is null,never should be here.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }

    /**
     * delete contact item with id
     * @param contactId
     * @param since
     * @return if delete successfully
     */
    public boolean deleteItem(String userId,long contactId,long since){
        boolean deleted = mContactProvider.deleteItemWithTimestamp(contactId, since);
        SyncContext syncContext = mSyncManager.getSyncContext(userId);
        if (syncContext != null ) {
            if(!syncContext.hasClientChange()){
                syncContext.setClientChange(deleted);
            }
        } else {
            mLog.warning(userId + " deleteItem,the SyncContext is null,never should be here.");
        }
        return deleted;
    }

    /**
     * delete all contact items by user
     * @param userId
     * @param since
     * @return if delete successfully
     */
    public boolean deleteAllItemsByUser(String userId,long since){
        boolean deleted = mContactProvider.deleteAllItemsWithTimestamp(userId, since);
        SyncContext syncContext = mSyncManager.getSyncContext(userId);
        if (syncContext != null ) {
            if(!syncContext.hasClientChange()){
                syncContext.setClientChange(deleted);
            }
        } else {
            mLog.warning(userId + " deleteAllItemsByUser,the SyncContext is null,never should be here.");
        }
        return deleted;
    }

    /**
     * get all items by user
     * @param userId
     * @return the itemId list
     */
    public List<Long> getAllItemsKeys(String userId){
        return mContactDAO.getAllItemKeys(userId);
    }

    /**
     * get new item keys
     * @param userId
     * @param since
     * @param until
     * @return the new itemId list
     */
    public List<Long> getNewItemKeys(String userId,long since,long until){
        return mContactDAO.getNewItemsByLastUpdate(userId,since,until);
    }

    /**
     * get update item keys
     * @param userId
     * @param since
     * @param until
     * @return the update itemId list
     */
    public List<Long> getUpdateItemKeys(String userId,long since,long until){
        return mContactDAO.getUpdatedItemsByLastUpdate(userId,since,until);
    }

    /**
     * get delete item keys
     * @param userId
     * @param since
     * @param until
     * @return the delete itemId list
     */
    public List<Long> getDeletedItemKeys(String userId, long since, long until){
        return mContactDAO.getRemovedItemsByLastUpdate(userId,since,until);
    }

    /**
     * get twin item keys for specified contact
     * @param contactJson the JPIM format contact json
     * @param userId
     * @return  the twin itemId list
     */
    public List<Long> getItemForTwins(String contactJson,String userId){
        Contact contact = JContactConverter.toContact(contactJson);
        contact.setOwnerId(userId);
        return mContactDAO.getTwinItems(contact);
    }

    //merge client contacts into sync contacts for contact private and public
    private Contact mergeContact(Contact changedContact,long timestamp,String contactId){
        ContactProvider contactProvider = new ContactProvider(mContext);
        contactProvider.useLogger(ContactLogger.getLogger(mContext));
        ContactMerge contactMerge = new ContactMerge(mContext);
        contactMerge.setLogger(mLog);

        if(!contactProvider.isBorqsFriend(contactId)){
            changedContact.setBorqsId(null);
            return changedContact;
        }

        Contact targetContact = contactProvider.getItem(contactId);
        return contactMerge.mergeFromPhone(changedContact,targetContact,timestamp);
    }

    //TODO use broadcast to notify the system sync process is end.
    private void onSyncEnd(long principalId, String deviceId, String userId ){
        //1.publish the contacts mapping change.
        SyncContext syncContext = mSyncManager.getSyncContext(userId);
        if (syncContext != null) {
            if(syncContext.hasClientChange()){
                mLog.info("onSyncEnd,publish contact mapping changed msg!");
                MQConnection.getInstance(mContext).publish(MQ_CONTACTS_MAPPINGS_CHANGED, "{\"userid\":\""+userId+"\"}");
            }else{
                mLog.info("onSyncEnd,client no change,do not publish contact mapping changed msg!");
            }
        } else {
            mLog.warning(userId + " onSyncEnd,do mapping publish,the SyncContext is null,never should be here.");
        }
        //2.schedule push job
        schedulePush(principalId, deviceId, userId);
    }

    private void schedulePush(final long principalId, final String deviceId, final String userId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                PushSyncHelper pushSyncHelper = new PushSyncHelper(mContext);
                //after sync,we should keep the sync version same with sync source version
                // bind with current device.
                pushSyncHelper.replaceSyncVersionWithSourceVersion(principalId, deviceId, userId);
                //schedule a job for push sync message
                //send change contact list for change request;
                //schedule a job for push sync message
                try {
                    mLog.info("======syncend,schedulePushJob for " + userId);
                    pushSyncHelper.schedulePushJob(userId);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
