package com.borqs.sync.server.datasync.ass.jms;

import com.borqs.sync.server.common.notification.ActionResponser;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.datasync.push.PushHelper;
import com.borqs.sync.server.common.util.Utility;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class AccountChangeListener extends ActionResponser {
	private static final String ACTION_ADD_FRIEND = "jms.ptp.consumer.identifier.ass.AddFriend";
	private static final String ACTION_CHANGE_PROFILE = "jms.ptp.consumer.identifier.ass.ChangeProfile";
	private static final String ACTION_REMOVE_FRIEND = "jms.ptp.consumer.identifier.ass.RemoveFriend";
	private static final String ACTION_JOIN_GROUP = "jms.ptp.consumer.identifier.ass.JoinGroup";
	private static final String ACTION_QUIT_GROUP = "jms.ptp.consumer.identifier.ass.QuitGroup";
	private static final String ACTION_CHANGE_FRIENDSHIP = "jms.ptp.consumer.identifier.ass.ChangeFriendship";
	
	private static final int ADDRESS_BOOK_CIRCLE = 5;
	private static final String TAG = "AccountChangeListener";
	
//	AccountSyncService accountSyncService;
	private Context context;
	private Logger logger;
    private PushHelper mPushHelper;

	
	public AccountChangeListener(Context context, String id) {
		super(context, id);
		this.context = context;
		logger = context.getLogger(TAG);
//		accountSyncService = AccountSyncServiceFactory.getService(context);
        mPushHelper = new PushHelper(context);
	}

	@Override
	protected void handle(String action, String data) {
		
		if(ACTION_CHANGE_FRIENDSHIP.equalsIgnoreCase(action)) {
			executeChangeFriendship(data);
		} else if(ACTION_CHANGE_PROFILE.equalsIgnoreCase(action)) {
			if (logger != null) {
				logger.info("executeUpdate: " + data);
			}
			executeUpdate(data);
		}
	}
	
	
	
	private void executeChangeFriendship(String data) {
        Set<String> userIds = new HashSet<String>();
		JSONArray ja = JSONArray.fromObject(data);
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			
			String userId = jo.getString("user");
			String friendId = jo.getString("friend");
			String[] circles = jo.getString("circle").split(",");
			
			if (circles != null) {
				for (String circle : circles) {
					if (String.valueOf(ADDRESS_BOOK_CIRCLE).equals(circle)) {
						logger.info(">>>>>>>> Account Sync Service INFO: Begin to add a friend <<<<<<<<");
//						boolean added = accountSyncService.addContact(friendId, userId);
                        boolean added = true;
                        if(added){
                            userIds.add(friendId);
                        }
						break;
					}
				}
			}

			logger.info(">>>>>>>> Account Sync Service INFO: Begin to remove a friend <<<<<<<<");
//			boolean removed = accountSyncService.removeContact(friendId, userId);
            boolean  removed = true;
            if(removed){
                userIds.add(friendId);
            }
		}

        if(userIds.size() > 0){
            onFriendShipChange(userIds);
        }

	}

	private void executeUpdate(String data) {
        Set<String> friendIds = new HashSet<String>();
		JSONArray ja = JSONArray.fromObject(data);
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			
			String userId = null;
			
			if (jo.has("user_id")) {
				userId = jo.getString("user_id");
			} else {
				if (logger != null) {
					logger.info("No user id. The process terminates.");
				}
				return;
			}
			
			if (logger != null) {
				logger.info("user_id: " + userId);
			}
			
			if (logger != null) {
				logger.info("Change profile for: " + userId + ", " + jo.toString());
			}
//			boolean changed = accountSyncService.changeProfile(userId, jo);
            boolean changed = true;
            if(changed){
                friendIds.add(userId);
            }
		}

        if(friendIds.size() > 0){
            onProfileChange(friendIds);
        }
	}

    private void onFriendShipChange(Set<String> userIds){
        List<String> userIdList = (List<String>)Utility.setToList(userIds);
        mPushHelper.onFriendShipChange(userIdList);
    }

    private void onProfileChange(Set<String> friendIds){
        List<String> friendList = (List<String>)Utility.setToList(friendIds);
        mPushHelper.onProfileChange(friendList);
    }
}
