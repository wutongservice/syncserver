package com.borqs.sync.server.task.profilesuggestion;

import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: b211
 * Date: 5/29/12
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileSuggestionDAO {
    
    private static final String SQL_GET_SCHEDULED_BORQS_IDS = "select distinct borqsid from borqs_sync_rel.borqs_pim_contact where borqsid >= (select distinct borqsid from borqs_sync_rel.borqs_pim_contact where borqsid is not null and status !='D' order by borqsid  limit ?,1) and status !='D' limit ?";
    private static final String SQL_GET_BORQS_IDS_COUNT = "select count(distinct borqsid) from borqs_pim_contact where borqsid is not null and status !='D' ";
    
    private Context mContext;
    
    public ProfileSuggestionDAO(Context context){
        mContext = context;
    }
    
    public long getBorqsIdsCount(){
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_BORQS_IDS_COUNT);
            rs = ps.executeQuery();

            if(rs.next()){
               return rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            DBUtility.close(con, ps, rs);
        }
        return 0;
    }

    public List<String> getScheduledBorqsIds(int currentPage,int queryOffset){
        List<String> borqsIds = new ArrayList<String>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = mContext.getSqlConnection();
            con.setReadOnly(true);
            ps = con.prepareStatement(SQL_GET_SCHEDULED_BORQS_IDS);
            ps.setInt(1,(currentPage-1)*queryOffset);
            ps.setInt(2,queryOffset);
            rs = ps.executeQuery();

            while(rs.next()){
                String borqsId = rs.getString(1);
                borqsIds.add(borqsId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            DBUtility.close(con, ps, rs);
        }
        return borqsIds;
    }
}
