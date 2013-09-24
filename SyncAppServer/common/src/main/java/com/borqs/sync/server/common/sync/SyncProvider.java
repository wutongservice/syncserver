package com.borqs.sync.server.common.sync;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.util.LogHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 7/4/12
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncProvider {
    
    private static final String SQL_GET_SYNC_STATUS = "select sync_status,deviceid from borqs_pim_sync_status where username=?";
    private static final String SQL_GET_SYNC_DEVICE_ITEM = "select id from borqs_pim_sync_status where username=? and deviceid=?";
    private static final String SQL_INSERT_SYNC_DEVICE = "insert into borqs_pim_sync_status (username,deviceid,sync_status) values (?,?,?)";
    private static final String SQL_UPDATE_SYNC_DEVICE = "update borqs_pim_sync_status set sync_status=? where id=?";
    private static final String SQL_UPDATE_ALL_SYNC_DEVICE = "update borqs_pim_sync_status set sync_status=? where username=?";

    private Context mContext;
    private Logger mLogger;

    public SyncProvider(Context context){
        mContext = context;
    }

    public void setLogger(Logger logger){
        mLogger = logger;
    }

    /**
     * check if the account is syncing
     * @param username
     * @param deviceId
     * @return if the account is syncing
     */
    public boolean isSyncing(String username, String deviceId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = mContext.getSqlConnection();

            // Find the record's id
            ps = conn.prepareStatement(SQL_GET_SYNC_STATUS);
            ps.setString(1, username);
            rs = ps.executeQuery();

            while (rs.next()) {
                boolean syncing = rs.getBoolean(1);
                if(syncing && !deviceId.equalsIgnoreCase(rs.getString(2))){
                    LogHelper.logInfo(mLogger, "is syncing");
                    return true;
                }
            }
        } catch (Exception e) {
            LogHelper.logInfo(mLogger, "is syncing,Exception: " + e);
        } finally {
            DBUtility.close(conn, ps, rs);
        }
        LogHelper.logInfo(mLogger, "no syncing");
        return false;
    }

    public void enterSyncBeginStatus(String username, String deviceId) {
        setSyncStatus(username,deviceId,true);
    }

    public void enterSyncEndStatus(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(SQL_UPDATE_ALL_SYNC_DEVICE);
            ps.setBoolean(1, false);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.logInfo(mLogger, "enterSyncEndStatus,Exception: " + e.getMessage());
        } finally {
            DBUtility.close(conn, ps, null);
        }
    }

    private void setSyncStatus(String username,String deviceId,boolean syncing){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = mContext.getSqlConnection();
            conn.setAutoCommit(false);

            // Find the record's id
            ps = conn.prepareStatement(SQL_GET_SYNC_DEVICE_ITEM);
            ps.setString(1, username);
            ps.setString(2, deviceId);
            rs = ps.executeQuery();


            if (rs.next()) {
                //update
                long id = rs.getLong(1);
                DBUtility.close(null, ps, rs);

                ps = conn.prepareStatement(SQL_UPDATE_SYNC_DEVICE);
                ps.setBoolean(1, syncing);
                ps.setLong(2, id);

                ps.executeUpdate();
                DBUtility.close(null, ps, null);
            } else{
                //insert
                ps = conn.prepareStatement(SQL_INSERT_SYNC_DEVICE);
                ps.setString(1, username);
                ps.setString(2, deviceId);
                ps.setBoolean(3, syncing);

                ps.executeUpdate();
                DBUtility.close(null, ps, null);
            }
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            LogHelper.logInfo(mLogger, "setSyncStatus,!!!!!rollback the sql ,Exception: " + e.getMessage());
        } finally {
            DBUtility.close(conn, ps, rs);
        }
    }
}
