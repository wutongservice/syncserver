/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.webagent.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.avro.AvroRemoteException;

import com.borqs.pim.jcontact.JContactBuilder;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;
import com.borqs.sync.server.common.json.JSONArray;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.providers.ContactProvider;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.sync.SyncProvider;
import com.borqs.sync.server.rpc.service.datasync.syncML.SyncService;
import com.borqs.sync.server.syncml.converter.JContactConverter;
import com.borqs.sync.server.webagent.util.ResponseWriterUtil;

/**
 * Contacts Open API Service Implementation
 * @author b534
 *
 */
public class ContactsAPIServiceImpl implements IContactsAPIService {
    private Context mContext;
    private Logger log;
    
    private ContactProvider provider;
    private SyncService mSyncService;
    
    private SyncProvider syncProvider;
    
    public ContactsAPIServiceImpl(Context context){
        mContext = context;
        log = context.getLogger("ContactsAPIServiceImpl");
        
        mSyncService = new SyncService(context);
        provider = new ContactProvider(context);
        
        syncProvider = new SyncProvider(context);
    }
    
    private boolean isSyncing(String username) {
    	String deviceId = UUID.randomUUID().toString();
		return syncProvider.isSyncing(username, deviceId );
    }

    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#addContacts(java.lang.String, java.util.List, com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void addContacts(String userId, List<String> contacts, ResponseWriter writer, String callback) throws AccountException {
        try {
        	if(isSyncing(userId)) {
        		ResponseWriterUtil.writeResultJsonp(writer, 500, "syncing", callback);
        		return;
        	}
        	
            long now = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            for(String c : contacts){
                // insert into database
                long contactId = mSyncService.addItem(userId, c, now);
                log.info("contact inserted,the id is : " + contactId);
                if(sb.length()>0) {
                	sb.append(",");
                }
                sb.append(contactId);
            }
            
            ResponseWriterUtil.writeResultJsonp(writer, 200, sb.toString(), callback);
        } catch (AvroRemoteException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }


    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#loadContacts(java.lang.String, com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void loadContacts(String userid, String fields, ResponseWriter writer, String callback) throws AccountException {
        log.info("loadContacts: " + userid);
        
        List<Long> contactList = provider.listContactIds(userid, false);

        List<String> strIds = new ArrayList<String>(contactList.size());
        for(long id : contactList){
            strIds.add(String.valueOf(id));
        }
        
        loadContact(userid, strIds.toArray(new String[0]), fields, writer, callback);
    }
    
    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#loadContactsWithLimit(java.lang.String, java.lang.Integer, java.lang.Integer com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void loadContactsWithLimit(String userid, String fields, int offset, int count, ResponseWriter writer, String callback) throws AccountException {
        log.info("loadContactsWithLimit: " + userid);
        try {
        	List<Contact> contacts = provider.getItems(userid, offset, count);
        	log.info("loadContactsWithLimit: contacts size is " + ((null==contacts)?0:contacts.size()));

        	Set<String> fieldsSet = convertFields(fields);
        	log.info("loadContactsWithLimit: fieldsSet is " + fieldsSet);
        	
        	StringBuffer sb = new StringBuffer();
        	sb.append("[");
            for(Contact contact : contacts){
                log.info("contactid:" + contact.getId());
                String content = toContactJson(contact, fieldsSet);
                //String content = mSyncService.getItem(id);
                log.info("toContactJson result:" + content);
                
                if(null == content || "".equals(content)) {
                	continue;
                }
                
                if(!"[".equals(sb.toString())) {
                	sb.append(",");
                }
                sb.append(content);
            }
            sb.append("]");
            
            if(null != callback && !"".equals(callback.trim())) {
    			ResponseWriterUtil.writeObjectJson(callback+"("+sb.toString()+");", writer);
    		} else {
    			ResponseWriterUtil.writeObjectJson(sb.toString(), writer);
    		}
        } catch(Exception e) {
        	log.warning("failed to load contacts with limit!");
        }
    }

    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#loadContact(java.lang.String, java.lang.String[], com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void loadContact(String userid, String[] contactIds, String fields, ResponseWriter writer, String callback) throws AccountException {
        log.info("loadContact: " + contactIds.toString());
        try {
        	Set<String> fieldsSet = convertFields(fields);
        	log.info("loadContact: fieldsSet is " + fieldsSet);
        	
        	StringBuffer sb = new StringBuffer();
        	sb.append("[");
            for(String id : contactIds){
                log.info("getitem contactid:" + id);
                Contact contact = provider.getItem(id);
                String content = toContactJson(contact, fieldsSet);
                //String content = mSyncService.getItem(id);
                log.info("getitem resutl:" + content);
                
                if(null == content || "".equals(content)) {
                	continue;
                }
                
                if(!"[".equals(sb.toString())) {
                	sb.append(",");
                }
                sb.append(content);
            }
            sb.append("]");
            
            if(null != callback && !"".equals(callback.trim())) {
    			ResponseWriterUtil.writeObjectJson(callback+"("+sb.toString()+");", writer);
    		} else {
    			ResponseWriterUtil.writeObjectJson(sb.toString(), writer);
    		}
        } catch(Exception e) {
        	log.warning("failed to load contact "+Arrays.toString(contactIds));
        }
    }

    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#updateContacts(java.lang.String, com.borqs.sync.server.common.json.JsonReader, com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void updateContact(String userId, String contactId, String contactJson, ResponseWriter writer, String callback) throws AccountException {
    	log.info("updateItem,userid:" + userId + ",contactId" + contactId + ",contactJson :" + contactJson);
    	long now = System.currentTimeMillis();
        boolean updated = false;
        
		try {
        	if(isSyncing(userId)) {
        		ResponseWriterUtil.writeResultJsonp(writer, 500, "syncing", callback);
        		return;
        	}

			updated = mSyncService.updateItem(userId, contactId, contactJson, now);
			
			if(updated) {
				ResponseWriterUtil.writeResultJsonp(writer, 200, contactId, callback);
			} else {
				ResponseWriterUtil.writeResultJsonp(writer, 500, contactId, callback);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("failed to update contact "+contactId);
		}
        log.info("contact: " + contactId + "  updated,the result is : " + updated);
    }

    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#count(java.lang.String, com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void count(String userId, ResponseWriter writer, String callback) throws AccountException {
//        List<Long> contactList = provider.listContactIds(userid, false);
    	long count = provider.countByUser(userId, false);
        try {
			if(-1 == count) {
        		ResponseWriterUtil.writeResultJsonp(writer, 500, "-1", callback);
			} else {
        		ResponseWriterUtil.writeResultJsonp(writer, 200, ""+count, callback);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("failed to count contacts of "+userId);
		}
    }

    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#deleteContacts(java.lang.String, com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void deleteContacts(String userId, ResponseWriter writer, String callback) throws AccountException {
		try {
        	if(isSyncing(userId)) {
        		ResponseWriterUtil.writeResultJsonp(writer, 500, "syncing", callback);
        		return;
        	}
			
			long now = System.currentTimeMillis();
			boolean deleted = mSyncService.deleteAllItemsByUser(userId, now);
			
			ResponseWriterUtil.writeResultJsonp(writer, 200, ""+deleted, callback);
			log.info("all contacts of " + userId + " have been update to 'D',result :" + deleted);
		} catch (IOException e) {
			e.printStackTrace();
			log.warning("failed to delete contacts of "+userId);
		}
    }

    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#deleteContact(java.lang.String, java.lang.String[], com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void deleteContact(String userId, String[] ids, ResponseWriter writer, String callback) throws AccountException {
        try {
        	if(isSyncing(userId)) {
        		ResponseWriterUtil.writeResultJsonp(writer, 500, "syncing", callback);
        		return;
        	}
        	
            long now = System.currentTimeMillis();
            StringBuffer sb = new StringBuffer();
            for(String id : ids){
                boolean deleted = mSyncService.deleteItem(userId, Long.parseLong(id), now);
                log.info("contact :" + id + " removed, the result is : " + deleted);
                
                if(deleted) {
	                if(sb.length()>0) {
	                	sb.append(",");
	                }
	                sb.append(id);
                }
            }
            
			ResponseWriterUtil.writeResultJsonp(writer, 200, sb.toString(), callback);
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }
    
    /* (non-Javadoc)
	 * @see com.borqs.sync.server.webagent.api.IContactsAPIService#batchDeleteContact(java.lang.String, java.lang.String, com.borqs.sync.server.common.json.ResponseWriter)
	 */
    @Override
	public void batchDeleteContact(String userId, String ids, ResponseWriter writer, String callback) throws AccountException {
		
        try {
        	if(isSyncing(userId)) {
        		ResponseWriterUtil.writeResultJsonp(writer, 500, "syncing", callback);
        		return;
        	}
        	
            long now = System.currentTimeMillis();
            boolean deleted = provider.batchDeleteItemWithTimestamp(ids, now);
            
            if(deleted) {
            	ResponseWriterUtil.writeResultJsonp(writer, 200, ids, callback);
            } else {
            	ResponseWriterUtil.writeResultJsonp(writer, 500, "", callback);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw AccountException.create(e);
        }
    }

    public static List<String> toContactList(String jsonContacts){
        ArrayList<String> contactList = new ArrayList<String>();
        try {
            JSONArray contacts = new JSONArray(jsonContacts);
            for(int i=0; i<contacts.length(); i++){
                String o = contacts.getJSONObject(i).toString();
                contactList.add(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contactList;
    }
    
    private String toContactJson(Contact contact, Set<String> fields){
        JContactBuilder builder = new JContactBuilder();
        
        if(null == fields || fields.contains("name")) {
        	log.info("start to convert name field!");
        	JContactConverter.toContactNameJson(builder,contact);
        }
        
        if(null == fields || fields.contains("phone")) {
        	log.info("start to convert phone field!");
        	JContactConverter.toContactPhoneJson(builder,contact);
        }
        
        if(null == fields || fields.contains("email")) {
        	log.info("start to convert email field!");
        	JContactConverter.toContactEmailJson(builder,contact);
        }
        
        if(null == fields || fields.contains("address")) {
        	log.info("start to convert address field!");
        	JContactConverter.toContactAddresslJson(builder,contact);
        }
        
        if(null == fields || fields.contains("org")) {
        	log.info("start to convert org field!");
        	JContactConverter.toContactOrgJson(builder,contact);
        }
        
        if(null == fields || fields.contains("web")) {
        	log.info("start to convert web field!");
        	JContactConverter.toContactWebJson(builder,contact);
        }
        
        if(null == fields || fields.contains("im")) {
        	log.info("start to convert im field!");
        	JContactConverter.toContactIMJson(builder,contact);
        }
        
        if(null != fields && fields.contains("photo")) {
        	log.info("start to convert photo field!");
        	JContactConverter.toContactPhotoJson(builder,contact);
        }
        
        if(null != fields && fields.contains("other")) {
        	log.info("start to convert other field!");
        	JContactConverter.toContactOthersJson(builder,contact);
        }
        
        if(null != fields && fields.contains("xtag")) {
        	log.info("start to convert xtag field!");
        	JContactConverter.toContactXTagJson(builder,contact);
        }
        
        return builder.createJson();
    }
    
    private static Set<String> convertFields(String fields) {
    	Set<String> result = null;
    	if(null != fields && !"".equals(fields)) {
    		result = new HashSet<String>();
    		String[] arr = fields.split(",");
    		for(String fld : arr) {
    			result.add(fld);
    		}
    	}
    	return result;
    }
}
