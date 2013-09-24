package com.borqs.sync.server.common.account;

import com.borqs.sync.server.common.account.adapters.BPCAccountHttpAdapter;
import com.borqs.sync.server.common.exception.DataAccessError;
import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.providers.Contact;
import com.borqs.sync.server.common.runtime.ConfigurationBase;
import com.borqs.sync.server.common.runtime.Context;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountManager {
    private BPCAccountHttpAdapter mProfileConnector;
	private Logger mLogger;
    private Context mContext;	

	public AccountManager(Context context) {
        mContext = context;
        String accountHost = mContext.getConfig().getSetting(ConfigurationBase.ACCOUNT_HOST);
        mProfileConnector = new BPCAccountHttpAdapter(accountHost);
	}

    public ProfileRecord getAccountWithPrivacy(String myId, String userId, boolean privacy) throws DataAccessError {
        try {
            ProfileRecord profile = mProfileConnector.getProfileOfUser(myId, userId, privacy);
            profile.asContact().setOwnerId(myId);
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            logE("Failed to get the user info for " + userId);
            throw new DataAccessError(e);
        }
    }

    public ProfileRecord getAccountWith(String myId, String userId, boolean privacy) throws DataAccessError {
        try {
            ProfileRecord profile = mProfileConnector.getProfileOfUser(myId, userId, privacy);
            profile.asContact().setOwnerId(myId);
            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            logE("Failed to get the user info for " + userId);
            throw new DataAccessError(e);
        }
    }

	public ProfileRecord getAccount(String myId, String userId) throws DataAccessError {
		try {
            ProfileRecord profile = mProfileConnector.getProfileOfUser(myId, userId, true);
            if(profile != null){
                profile.asContact().setOwnerId(myId);
                return profile;
            } else {
                throw  new DataAccessError("Not found");
            }
		} catch (Exception e) {
			e.printStackTrace();
            logE("Failed to get the user info for " + userId);
            throw new DataAccessError(e);
		}
	}

    public Map<String, List<String>> getRelationWithUser(String viewerId, List<String> userIds) throws DataAccessError {
        try {
            return mProfileConnector.getRelationWithUser(viewerId, userIds);
        } catch (Exception e) {
            e.printStackTrace();
            logE( "Failed to get the relation list for " + viewerId);
            throw new DataAccessError(e);
        }
    }
	
	public Contact getAccountContact(String viewerId, String userId) throws DataAccessError {
        ProfileRecord account = getAccount(viewerId,userId);
        return account.asContact();
	}

	public ProfileRecordList getFriendList(String myId) throws DataAccessError {
        try {
            ProfileRecordList friends = mProfileConnector.getFriends(myId);
            for(ProfileRecord fr : friends){
                fr.asContact().setOwnerId(myId);
            }
            return friends;
        } catch (Exception e) {
            e.printStackTrace();
            logE( "Failed to get the fiends list for " + myId);
            throw new DataAccessError(e);
        }
	}

    public ProfileRecordList getVisibleContactList(String userId) throws DataAccessError {
        ProfileRecordList rs = getFriendList(userId);

        LinkedList<ProfileRecord> invisibleBuddy = new LinkedList<ProfileRecord>();

        for(ProfileRecord r : rs){
            if (!r.isContactInfoVisible()) {
                invisibleBuddy.add(r);
            }
        }
        rs.removeAll(invisibleBuddy);

        return rs;
    }
    
    public CircleList getCirclesWithBuddy(String userId) throws DataAccessError {
        try {
            return mProfileConnector.getCircles(userId, null, true);
        } catch (IOException e) {
            e.printStackTrace();
            logE( "Failed to get the circle list for " + userId);
            throw new DataAccessError(e);
        }
    }

    public CircleList getCirclesInfo(String userId) throws DataAccessError {
        try {
            return mProfileConnector.getCircles(userId, null, false);
        } catch (IOException e) {
            e.printStackTrace();
            logE( "Failed to get the circle list for " + userId);
            throw new DataAccessError(e);
        }
    }

    public String createAccount(String loginEmail,
                                String loginPhone,
                                String pwd,
                                String displayName,
                                String gender,
                                String imei,
                                String imsi,
                                String device,
                                String location) throws IOException, AccountException {
        return mProfileConnector.createAccount(loginEmail, loginPhone, pwd, displayName, gender,
                imei, imsi, device, location);
    }

    public String findUserIdByUserName(String username) throws IOException, AccountException {
        return mProfileConnector.findUserIdByUserName(username);
    }

    public boolean updatePassword(String userId, String password) throws IOException, AccountException {
        return mProfileConnector.updatePassword(userId, password);
    }

	public void setLogger(Logger logger) {
		this.mLogger = logger;
        mProfileConnector.setLogger(logger);
	}
    
    private void logD(String msg){
        if(mLogger != null){
            mLogger.log(Level.INFO, msg);
        }
    }

    private void logE(String msg){
        if(mLogger != null){
            mLogger.log(Level.WARNING, msg);
        }
    }
}
