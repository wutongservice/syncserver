/*
 * Copyright (C) 2007-2012 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.providers;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;
import com.borqs.sync.server.common.util.LogHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Date: 3/27/12
 * Time: 11:19 AM
 * Borqs project
 */
public class GroupProvider {
    private static final String SQL_FILTER_GROUP_BY_CONTACT =
            " WHERE "+ ContactItem.COLUMN_CONTACT+" = ? AND " +
            ContactItem.COLUMN_TYPE+" = " + ContactItem.TYPE_X_TAG_GROUP;

    private Context mContext;
    private Logger mLogger;

    public GroupProvider(Context context){
        mContext = context;        
    }
    
    public void setLogger(Logger logger){
        mLogger = logger;
    }

    public List<ContactGroup> getGroupsOfContact(long raw_contact_id){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        final String get_groups =
                "SELECT "+ ContactItem.COLUMN_VALUE +" FROM " + ContactItem.TABLE_NAME
                        +SQL_FILTER_GROUP_BY_CONTACT;
        try{
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(get_groups);
            ps.setLong(1, raw_contact_id);
            rs = ps.executeQuery();

            String groups = null;
            if (rs.first()) {
                groups = rs.getString(ContactItem.COLUMN_VALUE);
            }
            return parseGroups(groups);
        } catch (Exception e){
            LogHelper.logW(mLogger, "Exception: " + e);
        } finally{
            DBUtility.close(conn, ps, rs);
        }

        return Collections.emptyList();
    }

    public void updateGroups(long raw_contact_id, List<ContactGroup> groups, long timestamp){
        LogHelper.logD(mLogger,"update group,the last_update is :" + timestamp);

        Connection conn = null;
        PreparedStatement ps = null;
        final String update_group =
                "UPDATE "+ContactItem.TABLE_NAME+" SET "+ ContactItem.COLUMN_VALUE +" = ?, "
                        + ContactItem.COLUMN_PRIVATE +" = ?, "
                        + ContactItem.COLUMN_LAST_UPDATE +" = ? "
                        + SQL_FILTER_GROUP_BY_CONTACT;

        try{
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(update_group);
            ps.setString(1, composeGroups(groups));

            ps.setBoolean(2, false);
            ps.setLong(3, timestamp);

            ps.setLong(4, raw_contact_id);
            ps.executeUpdate();
            DBUtility.close(conn, ps, null);

            //update last update of contact
            ContactProvider contactProvider = new ContactProvider(mContext);
            contactProvider.useLogger(mLogger);
            contactProvider.updateLastUpdate(raw_contact_id, timestamp);
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logW(mLogger, "Exception: " + e.getStackTrace());
        } finally{
            DBUtility.close(conn, ps, null);
        }
    }

    public void insertGroups(long raw_contact_id, List<ContactGroup> groups, long timestamp){
        LogHelper.logD(mLogger,"insert group,the last_update is :" + timestamp);

        Connection conn = null;
        PreparedStatement ps = null;
        final String update_group =
                "INSERT INTO "+ContactItem.TABLE_NAME+" ( "+ContactItem.COLUMN_CONTACT +", " +
                        ContactItem.COLUMN_TYPE +" , " + ContactItem.COLUMN_VALUE +", " + ContactItem.COLUMN_PRIVATE +
                        ", " + ContactItem.COLUMN_LAST_UPDATE +") VALUES (?, ?, ?,?,?)";


        try{
            conn = mContext.getSqlConnection();
            ps = conn.prepareStatement(update_group);
            ps.setLong(1, raw_contact_id);
            ps.setInt(2, ContactItem.TYPE_X_TAG_GROUP);
            ps.setString(3, composeGroups(groups));
            ps.setBoolean(4, false);
            ps.setLong(5, timestamp);

            ps.executeUpdate();
            DBUtility.close(conn, ps, null);

            //update last update of contact
            ContactProvider contactProvider = new ContactProvider(mContext);
            contactProvider.useLogger(mLogger);
            contactProvider.updateLastUpdate(raw_contact_id, timestamp);
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logW(mLogger, "Exception: " + e);
        } finally{
            DBUtility.close(conn, ps, null);
        }
    }

    private String composeGroups(List<ContactGroup> groups) {
        StringBuffer sbuffer = new StringBuffer();
        for(ContactGroup cg : groups){
            String name = cg.name();
            if(name != null && !name.isEmpty()){
                if(sbuffer.length()>0){
                    sbuffer.append(",");
                }
                sbuffer.append(name);
            }
        }
        return sbuffer.toString();
    }

    //groups like "group1,group2"
    private List<ContactGroup> parseGroups(String groups) {
        if(groups == null || groups.isEmpty()){
            return Collections.emptyList();
        }

        List<ContactGroup> lc = new ArrayList<ContactGroup>();
        String[] array = groups.split(",");
        for(String s: array){
            if(!s.isEmpty()){
                lc.add(new ContactGroup(s));
            }
        }

        return lc;
    }

    public void useLogger(Logger logger) {
        mLogger = logger;
    }
}
