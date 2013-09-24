package com.borqs.sync.server.webagent.fastlogin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;

/**
 * Created by IntelliJ IDEA.
 * User: b335
 * Date: 12-5-23
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public class UserSimPhoneDAO {
    private Context mContext;

    private static final String TAG_GUID = "guid";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_SIM = "sim";
    private static final String TAG_VERIFYCODE = "verifycode";
    private static final String TAG_EXTRA = "extra";
    private static final String TAG_CREATE = "create";

    private static final String SQL_GET_VALUE_BY_KEY =
            "SELECT %s FROM user_phone_sim where %s=?";
    private static final String SQL_UPDATE_VALUE_BY_KEY =
            "update user_phone_sim set %s=? where %s=?";
    private static final String SQL_INSERT =
            "insert into user_phone_sim values (?,?,?,?,?,?)";
    public UserSimPhoneDAO(Context context) {
        mContext = context;
    }
    
    public String getGuidByPhone(String phone)
    {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try { 
            ps = connection.prepareStatement(
                    String.format(SQL_GET_VALUE_BY_KEY,TAG_GUID,TAG_PHONE));
            ps.setString(1,phone);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString(TAG_GUID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }
        return null;
    }

    public String getGuidBySim(String sim) {

        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    String.format(SQL_GET_VALUE_BY_KEY,TAG_GUID,TAG_SIM));
            ps.setString(1,sim);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString(TAG_GUID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }
        return null;
    }

    public void addUserSimPhoneData(String guid, String phone, String sim,String verifyCode) {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(SQL_INSERT);
            
            java.util.Date date=new java.util.Date();
            Timestamp create_time = new Timestamp(date.getTime());
            
            ps.setString(1,guid);
            ps.setString(2,phone);
            ps.setString(3,sim);
            ps.setString(4,verifyCode);
            ps.setInt(5,0);
            ps.setTimestamp(6, create_time);
            
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }
    }

    public void updatetSim(String guid, String sim) {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    String.format(SQL_UPDATE_VALUE_BY_KEY,TAG_SIM,TAG_GUID));
            ps.setString(1,sim);
            ps.setString(2,guid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }
    }
    public void updateVerifyCode(String phone, String verifycode) {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    String.format(SQL_UPDATE_VALUE_BY_KEY,TAG_VERIFYCODE,TAG_PHONE));
            ps.setString(1,verifycode);
            ps.setString(2,phone);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }

    }

    public void updatetExtra(String phone,int extra) {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    String.format(SQL_UPDATE_VALUE_BY_KEY,TAG_EXTRA,TAG_PHONE));
            ps.setInt(1,extra);
            ps.setString(2,phone);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }
    }

    public int getExtraByPhone(String phone) {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    String.format(SQL_GET_VALUE_BY_KEY,TAG_EXTRA,TAG_PHONE));
            ps.setString(1,phone);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(TAG_EXTRA);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }
        return -1;
    }

    public String getVerifyByPhone(String phone) {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    String.format(SQL_GET_VALUE_BY_KEY,TAG_VERIFYCODE,TAG_PHONE));
            ps.setString(1,phone);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString(TAG_VERIFYCODE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }  finally {
            DBUtility.close(connection,ps,rs);
        }
        return null;
    }

    public String getPhoneByGuid(String guid) {
        Connection connection = mContext.getSqlConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    String.format(SQL_GET_VALUE_BY_KEY,TAG_PHONE,TAG_GUID));
            ps.setString(1,guid);
            rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString(TAG_PHONE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(connection,ps,rs);
        }
        return null;
    }

    public int addAndGetExtraCode(String phone) {
        int value = getExtraByPhone(phone);
        if(value<0)
            return value;
        value++;
        updatetExtra(phone,value);
        return value;
    }

}
