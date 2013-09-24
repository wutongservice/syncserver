/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */

package com.borqs.sync.server.webagent.dao;

import com.borqs.sync.server.common.profilesuggestion.ProfileSuggestionParser;
import com.borqs.sync.server.common.providers.Address;
import com.borqs.sync.server.common.providers.ContactItem;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.webagent.util.WebLog;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ContactDAO {
    
    private static final String SQL_GET_SYNC_SOURCE_VERSION = "select sync_source_version from borqs_user_sync_version where "
            + "username=? ";
    private static final String SQL_GET_SYNC_VERSION = "select sync_version from fnbl_principal where "
            + "username=? and device=?";
    private static final String SQL_GET_FRIEND_IDS = "select id from borqs_pim_contact where userid=? and status !='D' and (borqsid is not null and borqsid !='')";

    private static final String SQL_GET_ITEM_BY_BORQSID_AND_TYPE = "SELECT i.id " +
            " FROM borqs_pim_contact c LEFT OUTER JOIN borqs_pim_contact_item i" +
            " ON (c.id = i.contact) WHERE c.borqsid = ? and c.status != 'D' and i.type in ( ";
    
    private static final String SQL_UPDATE_ITEM_LAST_UPDATE_BY_ID = "update borqs_pim_contact_item set last_update=0 where id in (";
    
    //for query GUID
    private static final String SQL_QUERY_GUID_ID = "select luid,guid from fnbl_principal inner join fnbl_client_mapping on " +
            "fnbl_principal.id=fnbl_client_mapping.principal and fnbl_principal.username=? and " +
            "fnbl_principal.device=? and fnbl_client_mapping.sync_source='JCard' ";

//    static Map<Integer,List<Integer>> mItemTypeMap = new HashMap<Integer,List<Integer>>();
      static List<Integer> mTypes = new ArrayList<Integer>();

    static{
        //phone

//        List<Integer> phones = new ArrayList<Integer>();
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER);
        //address
//        List<Integer> address = new ArrayList<Integer>();
        mTypes.add(Address.ADDRESS_TYPE_HOME);
        mTypes.add(Address.ADDRESS_TYPE_OTHER);
        mTypes.add(Address.ADDRESS_TYPE_WORK);
        //emails
//        List<Integer> emails = new ArrayList<Integer>();
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS);
        //ims
//        List<Integer> ims = new ArrayList<Integer>();
        mTypes.add(ContactItem.TYPE_X_TAG_IM_YAHOO);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_AIM);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_GTALK);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_ICQ);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_JABBER);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_MSN);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_NETMEETING);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_SKYPE);
        mTypes.add(ContactItem.TYPE_X_TAG_IM_WIN_LIVE);
        //webpages
//        List<Integer> webs = new ArrayList<Integer>();
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_WEB_PAGE);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_BUSINESS_WEB_PAGE);
        mTypes.add(ContactItem.CONTACT_ITEM_TYPE_HOME_WEB_PAGE);

//        mItemTypeMap.put(ProfileSuggestionParser.CHANGE_REQUEST_TYPE_PHONE,phones);
//        mItemTypeMap.put(ProfileSuggestionParser.CHANGE_REQUEST_TYPE_ADDRESS,address);
//        mItemTypeMap.put(ProfileSuggestionParser.CHANGE_REQUEST_TYPE_EMAIL,emails);
//        mItemTypeMap.put(ProfileSuggestionParser.CHANGE_REQUEST_TYPE_IM,ims);
//        mItemTypeMap.put(ProfileSuggestionParser.CHANGE_REQUEST_TYPE_WEBS,webs);
    }

    private Context mContext;
    private Logger mLogger;
    
    public ContactDAO(Context context){
        mContext = context;
        mLogger = WebLog.getLogger(context);
    }

    public long getSyncSourceVersion(String uid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_SYNC_SOURCE_VERSION);
            ps.setString(1, uid);

            rs = ps.executeQuery();
            if (rs.next()) {
                long syncSourceVersion = rs.getLong(1);
                mLogger.info("===========get syncSourceVersion by :" + uid + " : " + syncSourceVersion);
                return syncSourceVersion;
            }
        } catch (Exception e) {
            mLogger.info("========getSyncSourceVersion error :" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }
        return -1;
    }

    public long getSyncVersion(String uid, String deviceId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_SYNC_VERSION);
            ps.setString(1, uid);
            ps.setString(2, deviceId);
            rs = ps.executeQuery();
            if (rs.next()) {
                long syncVersion = rs.getLong(1);
                mLogger.info("===========get getSyncVersion by :" + uid + " and deviceId: " + 
                deviceId  + " : " + syncVersion);
                return syncVersion;

            }
        } catch (Exception e) {
            mLogger.info("==========getSyncVersion error" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }
        return -1;
    }

    public boolean ignoreItem(String borqsId){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean resetResult = false;

        try {
            StringBuilder sb = new StringBuilder();
                //type
                for(int i=0;i<mTypes.size();i++){
                    if(i == mTypes.size() - 1){
                        sb.append(mTypes.get(i));
                    }else{
                        sb.append(mTypes.get(i)).append(",");
                    }
                }
            sb.append(")");

            String sql = SQL_GET_ITEM_BY_BORQSID_AND_TYPE + sb.toString();
            mLogger.info("query ignore items sql:" + sql);

            con = mContext.getSqlConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, borqsId);

            rs = ps.executeQuery();

            List<Long> itemIds = new ArrayList<Long>();
            while (rs.next()) {
                itemIds.add(rs.getLong(1));
            }
            mLogger.info("===========ignoreItem,get items: " + itemIds.toString());
            if (itemIds.size() > 0) {
                resetResult = resetItemLastUpdate(con,itemIds);
             }
        } catch (Exception e) {
            e.printStackTrace();
            mLogger.info("ignore item error!!!!" + e.getMessage());
        } finally {
            DBUtility.close(con, ps, rs);
        }
        return resetResult;
    }

    private boolean resetItemLastUpdate(Connection con,List<Long> itemIds) throws Exception {
        PreparedStatement ps = null;
        try{
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<itemIds.size();i++){
                if(i == itemIds.size() - 1){
                    sb.append(itemIds.get(i));
                }else{
                    sb.append(itemIds.get(i)).append(",");
                }
            }
            sb.append(")");
            
            String sql = SQL_UPDATE_ITEM_LAST_UPDATE_BY_ID + sb.toString();
            mLogger.info("resetItemLastUpdate sql:" + sql);

            ps = con.prepareStatement(sql);
            return ps.executeUpdate() > 0;
        } finally {
            DBUtility.close(null, ps, null);
        }
    }

    /**
     * return the borqsids to be informed to perfect the profile
      * @return
     */
    public List<String> getBorqsIdsToBeInformed(){
        List<String> borqsIds = new ArrayList<String>();
        return borqsIds;
    }
    
    
    public Map<String,String> getLuidGuidMapping(String luids,String user,String device){
        Map<String,String> luidGuidMapping = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            StringBuilder sb = new StringBuilder();
            String[] luidArray = luids.split(",");
            if(luidArray != null && luidArray.length > 0){
                sb.append(" and (");
                for(int i=0;i<luidArray.length;i++){
                    if(i == 0){
                        sb.append("fnbl_client_mapping.luid = '" + luidArray[i] + "'");
                    } else{
                        sb.append(" or fnbl_client_mapping.luid = '" + luidArray[i] + "'");
                    }
                }
                sb.append(" )");
            }
            String sql = SQL_QUERY_GUID_ID + sb.toString();
            mLogger.info("query guids sql:" + sql);
            
            con = mContext.getSqlConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, device);

            rs = ps.executeQuery();

            while (rs.next()) {
                String luid = rs.getString(1);
                String guid = rs.getString(2);
                luidGuidMapping.put(luid,guid);
            }
        } catch (Exception e) {
            mLogger.info("==========getLuidGuidMapping error" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }
        return luidGuidMapping;
    }

    public List<Long> queryFriendIds(String uid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        List<Long> friendIds = new ArrayList<Long>();

        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_FRIEND_IDS);
            ps.setString(1, uid);
            rs = ps.executeQuery();
            while (rs.next()) {
                long id = rs.getLong(1);
                mLogger.info("===========queryFriendIds :" + uid + " ,friend contact Id:" + id);
                friendIds.add(id);

            }
        } catch (Exception e) {
            mLogger.info("==========getSyncVersion error" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtility.close(con, ps, rs);
        }
        return friendIds;
    }




}
