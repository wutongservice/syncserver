package com.borqs.sync.server.datasync.ass.dao;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.util.Utility;
import com.borqs.sync.server.datasync.DSLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 12/31/11
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContactSyncDAO {

    private static final String SQL_GET_SYNC_SOURCE_VERSION = "select sync_source_version from borqs_user_sync_version where " +
            " username=? ";
    private static final String SQL_UPDATE_SYNC_SOURCE_VERSION = "update borqs_user_sync_version set sync_source_version =? " +
            " WHERE username=? ";
    private static final String SQL_INSERT_SYNC_SOURCE_VERSION = "insert borqs_user_sync_version (username,sync_source_version) " +
            " values (?,?) ";
    private static final String SQL_GET_USER_ID_BY_FRIEND_ID = "select distinct userid from borqs_pim_contact where borqsid in ";

    private static final String SQL_GET_DEVICE_VERSION_BY_USERNAME = "select device,sync_version from fnbl_principal where username=?";

    private Context mContext;
    private Logger mLogger;

    public ContactSyncDAO(Context context){
        mLogger = DSLog.getInstnace(context).getLogger();
        mContext = context;
    }


    private long getSyncSourceVersion(String userId){
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
                return syncSourceVersion;
            }
        } catch (Exception e) {
            mLogger.info("========query device error :" + e.getMessage());
            e.printStackTrace();
        } finally{
            DBUtility.close(con, ps, rs);
        }
        return -1;
    }

    private void updateSyncSourceVersion(String userId){
        mLogger.info("========updateSyncSourceVersion===========");
        Connection con = null;
        PreparedStatement ps = null;

        try {
            long syncSourceVersion = getSyncSourceVersion(userId);
            mLogger.info("==========="+ userId + "'s current syncSourceVersion :" + syncSourceVersion);
            con = mContext.getSqlConnection();
            if(syncSourceVersion < 0){
                //insert a new item
                ps = con.prepareStatement(SQL_INSERT_SYNC_SOURCE_VERSION);
                syncSourceVersion = 0;
            }else{
                //update item
                ps = con.prepareStatement(SQL_UPDATE_SYNC_SOURCE_VERSION);
                syncSourceVersion += 1;
            }
            ps.setLong(1, syncSourceVersion);
            ps.setString(2, userId);
            mLogger.info("=========update syncSourceVersion by userid:" + userId
                    + " syncSourceVersion: " + syncSourceVersion);
            int update = ps.executeUpdate();
            mLogger.info("=========update item:" + update);
        } catch (Exception e) {
            mLogger.info("==========update item error" + e.getMessage());
            e.printStackTrace();
        } finally{
            DBUtility.close(con,ps,null);
        }
    }

    public List<String> getUserIdByFriends(List<String> friendIds){
        mLogger.info("========getUserIdByFriend===========");
        List<String> userIds = new ArrayList<String>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = mContext.getSqlConnection();
            StringBuffer sb = new StringBuffer();
            sb.append(" (");
            for(String friend:friendIds){
                //first tiem," (".length = 2
                if(sb.length() == 2){
                    sb.append(friend);
                }else{
                    sb.append(",").append(friend);
                }
            }
            sb.append(") ");
            ps = con.prepareStatement(SQL_GET_USER_ID_BY_FRIEND_ID + sb.toString());
            mLogger.info("=====getUserIdByFriend sql: " + SQL_GET_USER_ID_BY_FRIEND_ID + sb.toString());
            rs = ps.executeQuery();
            while(rs.next()){
                userIds.add(rs.getString(1));
            }
        } catch (Exception e) {
            mLogger.info("==========getUserIdByFriends item error" + e.getMessage());
            e.printStackTrace();
        } finally{
            DBUtility.close(con,ps,rs);
        }
        return userIds;
    }

    public List<String> getNeedSyncDevices(String userId){
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
                //if the syncversion == syncSourceVersion,the device need not to sync
                mLogger.info("========sync device is " + deviceId + " syncversion : " + syncVersion
                        + " by :" + userId);
                if(syncSourceVersion == syncVersion || Utility.isEmpty(deviceId)){
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
        mLogger.info("getNeedSyncDevices,need to sync devices: " + syncDevices);
        return syncDevices;
    }

}
