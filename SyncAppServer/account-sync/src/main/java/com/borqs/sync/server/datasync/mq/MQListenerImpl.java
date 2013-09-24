package com.borqs.sync.server.datasync.mq;

import java.sql.Connection;

import org.apache.commons.lang.math.NumberUtils;

import com.borqs.sync.server.common.account.AccountManager;
import com.borqs.sync.server.common.account.ProfileRecord;
import com.borqs.sync.server.common.account.UserInfo;
import com.borqs.sync.server.common.account.UserInfoFetcher;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.json.JSONException;
import com.borqs.sync.server.common.json.JSONObject;
import com.borqs.sync.server.common.mq.MQConnection;
import com.borqs.sync.server.common.mq.MQListener;
import com.borqs.sync.server.common.providers.ContactsMappingDao;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.datasync.DSLog;
import com.borqs.sync.server.datasync.engine.AccountSyncException;
import com.borqs.sync.server.datasync.engine.BaseSyncItem;
import com.borqs.sync.server.datasync.pim.contact.BorqsContactSource;
import com.borqs.sync.server.datasync.pim.contact.BorqsProfile;
import com.borqs.sync.server.datasync.pim.contact.FunambolContactSource;

public class MQListenerImpl extends MQListener {
	
	public static final String MSG_USER_ADD = "PlatformHook.onUserCreated";
	public static final String MSG_USER_UPDATE = "PlatformHook.onUserProfileChanged";
	public static final String MSG_USER_DESTROY = "PlatformHook.onUserDestroyed";
	public static final String MSG_FRIENDSHIP_CHANGE = "PlatformHook.onFriendshipChange";
	public static final String MSG_SET_FRIEND_CHANGE = "PlatformHook.onSetFriendChange";

	private static final String MQ_CONTACTS_MAPPINGS_CHANGED = "SyncContacts.onContactsMappingsChanged";
	
	private Context mContext;
	private DSLog log;
	
	private AccountManager accountManager;
	
	public MQListenerImpl(Context context){
		mContext = context;
		log = DSLog.getInstnace(mContext);
		
		accountManager = new AccountManager(mContext);
	}
	
	@Override
	public void processMessage(String channel, String msg) {
		log.info("MQServer" + channel + ":" + msg);
		
		if(MSG_USER_ADD.equals(channel)) {
			processUserAdd(msg);
		} else if(MSG_USER_UPDATE.equals(channel)) {
			processUserUpdate(msg);
		} else if(MSG_USER_DESTROY.equals(channel)) {
			processUserDestroy(msg);
		} else if(MSG_FRIENDSHIP_CHANGE.equals(channel)) {
			processFriendship(msg);
		} else if(MSG_SET_FRIEND_CHANGE.equals(channel)) {
			// waiting decide
			processSetFriendChange(msg);
		} else {
			log.info("undefined channel!");
		}
	}
	
	protected BaseSyncItem parseRecord(ProfileRecord record,
			BorqsContactSource source) {
		if (record == null) {
			return null;
		}

		// get the item's borqsId as syncId
		String syncId = record.asContact().getBorqsId();

		// basic_updated_time, profile_updated_time, contact_info_updated_time,
		// address_updated_time
		long basicUpdated = record.getBasicInfoLastUpdateTime();
		long profileUpdated = record.getProfileLastUpdateTime();
		long contactUpdated = record.getContactInfoLastUpdateTime();
		long addressUpdated = record.getAddressLastUpdateTime();

		long lastUpdate = NumberUtils.max(new long[] { basicUpdated,
				profileUpdated, contactUpdated, addressUpdated });

		return new BorqsProfile(lastUpdate, -1, syncId, source);
	}
    
	private void processSetFriendChange(String msg) {
		log.info("processSetFriendChange->"+msg);
		try {
			JSONObject jobj = new JSONObject(msg);
			String userId = jobj.getString("user");
			String friend = jobj.getString("friend");
			log.info("userid is:" + userId);
			log.info("friend is:" + friend);
			
			log.info("processSetFriendChange->user_id is "+userId);
			
//			// sync contact
//			try {
//				ProfileRecord record = accountManager.getAccount(userId, friend);
//				FunambolContactSource syncMLSource = new FunambolContactSource(userId, mContext);
//				
//				BaseSyncItem item = parseRecord(record, new BorqsContactSource(userId, mContext));
//				syncMLSource.addItem(item , System.currentTimeMillis());
//			} catch (DataAccessError e) {
//				e.printStackTrace();
//				log.error("MQListenerImpl.processSetFriendChange: failed to access account data!", e);
//			} catch (AccountSyncException e) {
//				e.printStackTrace();
//				log.error("MQListenerImpl.processSetFriendChange: failed to add item to contacts!", e);
//			}
//			
//			// refresh contacts mapping table
//			refreshMappings(userId);
			
			Connection conn = mContext.getSqlConnection();
			try {
				IContactsMappingMQService service = new ContactsMappingMQService(new ContactsMappingDao(conn));
				service.createMapping(userId, -1L, friend);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtility.close(conn, null, null);
			}

			// notify clients that have finished process
			MQConnection.getInstance(mContext).publish(MQ_CONTACTS_MAPPINGS_CHANGED, "{\"userid\":\""+userId+"\"}");
		} catch (JSONException e) {
			log.error("MQListenerImpl.processUpdate: failed to parse change message", e);
		}
	}

	private void processFriendship(String msg) {
		log.info("processFriendship->"+msg);
		try {
			JSONObject jobj = new JSONObject(msg);
			String userId = jobj.getString("user");
			String circle = jobj.getString("circle");
			String friend = jobj.getString("friend");
			log.info("userid is:" + userId);
			log.info("friend is:" + friend);
			log.info("circle is:" + circle);
			
			if (circle.equals("")) {
				System.out.println("delete friendship "+userId+","+friend);
				log.info("delete friendship "+userId+","+friend);
				//operate = "delete";
			} else {
				System.out.println("add friendship "+userId+","+friend);
				log.info("add friendship "+userId+","+friend);
				//operate = "add";
			}
			
			log.info("processFriendship->user_id is "+userId);
			refreshMappings(userId);

			// notify clients that have finished process
			MQConnection.getInstance(mContext).publish(MQ_CONTACTS_MAPPINGS_CHANGED, "{\"userid\":\""+userId+"\"}");
		} catch (JSONException e) {
			log.error("MQListenerImpl.processUpdate: failed to parse change message", e);
		}
	}

	private void processUserAdd(String msg) {
		log.info("processAdd->"+msg);
		try {
			JSONObject jobj = new JSONObject(msg);
			String userId = null;
			if(jobj.has("user_id")) {
				userId = jobj.getString("user_id");
			}
			
			log.info("processUserAdd->user_id is "+userId);
			if(null == userId || "".equals(userId)) {
				return;
			}
			
			refreshMappings(userId);
			
			// notify clients that have finished process
			MQConnection.getInstance(mContext).publish(MQ_CONTACTS_MAPPINGS_CHANGED, "{\"userid\":\""+userId+"\"}");
		} catch (JSONException e) {
			log.error("MQListenerImpl.processUpdate: failed to parse change message", e);
		}
	}

	private void processUserUpdate(String msg) {
		log.info("processUpdate->"+msg);
		try {
			JSONObject jobj = new JSONObject(msg);
			
			String userId = null;
			if(jobj.has("user_id")) {
				userId = jobj.getString("user_id");
			}
			
			log.info("processUserUpdate->user_id is "+userId);
			if(null == userId || "".equals(userId)) {
				return;
			}
			
			refreshMappings(userId);
			
			// notify clients that have finished process
			MQConnection.getInstance(mContext).publish(MQ_CONTACTS_MAPPINGS_CHANGED, "{\"userid\":\""+userId+"\"}");
		} catch (JSONException e) {
			log.error("MQListenerImpl.processUserUpdate: failed to parse change message", e);
		}
	}
	
	private void processUserDestroy(String msg) {
		log.info("processDestroy->"+msg);
		Connection conn = mContext.getSqlConnection();
		try {
			JSONObject jobj = new JSONObject(msg);
			
			String userId = null;
			if(jobj.has("user_id")) {
				userId = jobj.getString("user_id");
			}
			
			log.info("processUserDestroy->user_id is "+userId);
			if(null == userId || "".equals(userId)) {
				return;
			}
			
			IContactsMappingMQService service = new ContactsMappingMQService(new ContactsMappingDao(conn));
		
			// 删除mapping关系
			service.deleteMappingsByBorqsId(userId);

			// notify clients that have finished process
			MQConnection.getInstance(mContext).publish(MQ_CONTACTS_MAPPINGS_CHANGED, "{\"userid\":\""+userId+"\"}");
		} catch(Exception e) {
			log.error("MQListenerImpl.processUserDestroy: failed to parse change message", e);			
		} finally {
			DBUtility.close(conn, null, null);
		}
	}

	private void refreshMappings(String userId) {
		// 刷新mapping关系
		String userUrl = mContext.getConfig().getSetting("account_server_host");
		UserInfo userInfo = UserInfoFetcher.fetchUserInfo(userUrl, userId);
		
		Connection conn = mContext.getSqlConnection();
		try {
			IContactsMappingMQService service = new ContactsMappingMQService(new ContactsMappingDao(conn));
			service.refreshMappingsOfUser(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.close(conn, null, null);
		}
	}
}
