package com.borqs.sync.server.common.providers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.borqs.sync.server.common.util.Utility;

public class ContactsMappingDao implements IContactsMappingDao {
	private static Logger log = Logger.getLogger(ContactsMappingDao.class); 

	private String connectURI = "jdbc:mysql://192.168.5.208:3306/borqs_sync?useUnicode=true&characterEncoding=UTF-8";
	private String dbDriver = "com.mysql.jdbc.Driver";
	private String userName = "root";
	private String password = "root";
	
	private BasicDataSource datasource = new BasicDataSource();
	private Connection conn = null;
	
	private static final String PHONES_WHERE = "i.type="+ContactItem.CONTACT_ITEM_TYPE_ASSISTANT_NUMBER 
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_BUSINESS_FAX_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_BUSINESS_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_CALLBACK_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_CAR_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_COMPANY_MAIN_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_HOME_FAX_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_MOBILE_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_OTHER_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_PAGER_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_PRIMARY_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_HOME_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_HOME_2_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_BUSINESS_2_TELEPHONE_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_OTHER_FAX_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_TELEX_NUMBER
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_RADIO_TELEPHONE_NUMBER;
	
	private static final String EMAILS_WHERE = "i.type="+ContactItem.CONTACT_ITEM_TYPE_EMAIL_1_ADDRESS
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_EMAIL_2_ADDRESS
			+ " or i.type=" + ContactItem.CONTACT_ITEM_TYPE_EMAIL_3_ADDRESS;
	
	private static final String COUNT_SQL_BY_OID = "select count(*) c from borqs_contactid_borqsid where ownerid=?";
	private static final String COUNT_SQL_BY_BID = "select count(*) c from borqs_contactid_borqsid where borqsid=?";
	
	private static final String SELECT_INTO_MAPPING_BY_OID = "insert into borqs_contactid_borqsid " +
			"select userid as ownerid, id as contactid, IFNULL(borqsid,'') as borqsid from borqs_pim_contact where userid=? and status!='D'";
	private static final String SELECT_INTO_MAPPING_BY_OID_A = "insert into borqs_contactid_borqsid " + 
			"select distinct c.userid as ownerid, c.id as contactid, IFNULL(c.borqsid,'') as borqsid from borqs_pim_contact c " +
			"left join borqs_contactid_borqsid m on(c.id=m.contactid and c.userid=m.ownerid) " +
			"where c.userid=? and c.status!='D' and m.contactid is null and m.ownerid is null";
	
	private static final String SELECT_NO_MAPPING_CONTACTS = "select t.contactid,t.ownerid,t.borqsid,i.type,i.value from borqs_contactid_borqsid t " +
			"join borqs_pim_contact_item i on (t.contactid=i.contact) where t.ownerid=? and (t.borqsid is NULL or t.borqsid='') "
			+"and (("+PHONES_WHERE+") or ("+EMAILS_WHERE+"))";
	
	private static final String UPDATE_MAPPING = "update borqs_contactid_borqsid set borqsid=? where ownerid=? and contactid=?";
	
	private static final String CREATE_MAPPING = "insert into borqs_contactid_borqsid (borqsid,ownerid,contactid) values(?, ?, ?)";
	
	private static final String SQL_FIND_CONTACT = "select distinct c.userid as ownerid,c.id as contactid, IFNULL(c.borqsid,?) as borqsid from borqs_pim_contact c " +
			"join borqs_pim_contact_item i on (c.id=i.contact) where c.status!='D' and i.value=? "
			+"and (("+PHONES_WHERE+") or ("+EMAILS_WHERE+"))";
	
	private static final String CLEAR_MAPPING = "delete from borqs_contactid_borqsid";
	private static final String SQL_DEL_MAPPINGS_BY_BORQSID = "delete from borqs_contactid_borqsid where borqsid=?";
	private static final String SQL_DEL_MAPPINGS_BY_OWNERID = "delete from borqs_contactid_borqsid where ownerid=?";
	
	private static final String SQL_UPDATE_MAPPINGS_BY_PHONE = "insert into borqs_contactid_borqsid " +
			"select distinct c.userid as ownerid,c.id as contactid, IFNULL(c.borqsid,?) as borqsid from borqs_pim_contact c " +
			"join borqs_pim_contact_item i on (c.id=i.contact) where c.status!='D' and i.value=? and ("+PHONES_WHERE+")";
	
	private static final String SQL_UPDATE_MAPPINGS_BY_EMAIL = "insert into borqs_contactid_borqsid " +
			"select distinct c.userid as ownerid,c.id as contactid, IFNULL(c.borqsid,?) as borqsid from borqs_pim_contact c " +
			"join borqs_pim_contact_item i on (c.id=i.contact) where c.status!='D' and i.value=? and ("+EMAILS_WHERE+")";
	
//	private static final String SQL_QUERY_MAPPINGS_BY_OID = "select ownerid,contactid,borqsid from borqs_contactid_borqsid where ownerid=? and borqsid is not null and borqsid!=''";
	private static final String SQL_QUERY_MAPPINGS_BY_OID = "select distinct ownerid,contactid,borqsid from borqs_contactid_borqsid where ownerid=?";
	private static final String SQL_QUERY_MAPPINGS_BY_BID = "select distinct ownerid,contactid,borqsid from borqs_contactid_borqsid where borqsid=?";

	private static final String SQL_DEL_MAPPINGS_WITH_STATUS_D = "DELETE FROM borqs_contactid_borqsid " +
			"USING borqs_contactid_borqsid " +
			"INNER JOIN borqs_pim_contact " +
			"ON borqs_contactid_borqsid.contactid = borqs_pim_contact.id " +
			"WHERE borqs_pim_contact.status='D'";

	private static final String SQL_DEL_OWNER_MAPPINGS_WITH_STATUS_D = "DELETE FROM borqs_contactid_borqsid " +
			"USING borqs_contactid_borqsid " +
			"INNER JOIN borqs_pim_contact " +
			"ON borqs_contactid_borqsid.contactid = borqs_pim_contact.id " +
			"WHERE borqs_pim_contact.status='D' AND borqs_pim_contact.userid=?";
	
	public ContactsMappingDao(String connectURI, String dbDriver, String userName, String password) {
		super();
		this.connectURI = connectURI;
		this.dbDriver = dbDriver;
		this.userName = userName;
		this.password = password;
		
		// initialize database connection
		setupDataSource();
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			log.error("failed to get connection!", e);
		}
	}
	
	public ContactsMappingDao(Connection conn) {
		this.conn = conn;
	}
	

    /* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#queryContactsMappings(String ownerid)
	 */
	public List<ContactMapping> queryContactsMappingsByOID(String ownerid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        List<ContactMapping> results = new ArrayList<ContactMapping>();
        try {
        	log.info("queryContactsMappings->"+String.format(SQL_QUERY_MAPPINGS_BY_OID.replace("?", "%s"), ownerid));
            stmt = conn.prepareStatement(SQL_QUERY_MAPPINGS_BY_OID);
            stmt.setString(1, ownerid);
            rset = stmt.executeQuery();
            while(rset.next()) {
            	results.add(new ContactMapping(
            			rset.getString("ownerid"),
            			rset.getLong("contactid"),
            			rset.getString("borqsid")));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to queryContactsMappings -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        
        return results;
	}
	

	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#queryContactsMappingsByBID(String borqsId)
	 */
	public List<ContactMapping> queryContactsMappingsByBID(String borqsId) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        List<ContactMapping> results = new ArrayList<ContactMapping>();
        try {
        	log.info("queryContactsMappingsByBID->"+String.format(SQL_QUERY_MAPPINGS_BY_BID.replace("?", "%s"), borqsId));
            stmt = conn.prepareStatement(SQL_QUERY_MAPPINGS_BY_BID);
            stmt.setString(1, borqsId);
            rset = stmt.executeQuery();
            while(rset.next()) {
            	results.add(new ContactMapping(
            			rset.getString("ownerid"),
            			rset.getLong("contactid"),
            			rset.getString("borqsid")));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to queryContactsMappingsByBID -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        
        return results;
	}

	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#updateMappingsByPhone(java.lang.String, java.lang.String)
	 */
	public boolean updateMappingsByPhone(String borqsid, String value) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("updateMappingsByPhone->"+String.format(SQL_UPDATE_MAPPINGS_BY_PHONE.replace("?", "%s"), borqsid, value));
            stmt = conn.prepareStatement(SQL_UPDATE_MAPPINGS_BY_PHONE);
            stmt.setString(1, borqsid);
            stmt.setString(2, value);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to updateMappingsByPhone -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#updateMappingsByEmail(java.lang.String, java.lang.String)
	 */
	public boolean updateMappingsByEmail(String borqsid, String value) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("updateMappingsByEmail->"+String.format(SQL_UPDATE_MAPPINGS_BY_EMAIL.replace("?", "%s"), borqsid, value));
            stmt = conn.prepareStatement(SQL_UPDATE_MAPPINGS_BY_EMAIL);
            stmt.setString(1, borqsid);
            stmt.setString(2, value);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to updateMappingsByEmail -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#deleteMappingsByBorqsID(java.lang.String)
	 */
	public boolean deleteMappingsByBorqsID(String borqsId) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("deleteMappingsByBorqsID->"+String.format(SQL_DEL_MAPPINGS_BY_BORQSID.replace("?", "%s"), borqsId));
            stmt = conn.prepareStatement(SQL_DEL_MAPPINGS_BY_BORQSID);
            stmt.setString(1, borqsId);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to deleteMappingsByBorqsID -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#deleteMappingsByOwnerID(java.lang.String)
	 */
	public boolean deleteMappingsByOwnerID(String ownerId) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("deleteMappingsByOwnerID->"+String.format(SQL_DEL_MAPPINGS_BY_OWNERID.replace("?", "%s"), ownerId));
            stmt = conn.prepareStatement(SQL_DEL_MAPPINGS_BY_OWNERID);
            stmt.setString(1, ownerId);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to deleteMappingsByBorqsID -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#findContactByItem(java.lang.String, java.lang.String)
	 */
	public List<ContactMapping> findContactByItem(String ownerid, String value) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        List<ContactMapping> results = new ArrayList<ContactMapping>();
        try {
        	log.info("findContactByItem->"+String.format(SQL_FIND_CONTACT.replace("?", "%s"), ownerid, value));
            stmt = conn.prepareStatement(SQL_FIND_CONTACT);
            stmt.setString(1, ownerid);
            stmt.setString(2, value);
            rset = stmt.executeQuery();
            while(rset.next()) {
            	results.add(new ContactMapping(
            			rset.getString("ownerid"),
            			rset.getLong("contactid"),
            			rset.getString("borqsid")));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to findContactByItem -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        
        return results;
	}
	
	@Override
	public boolean deleteMappingsWithStatusD(String ownerid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("deleteMappingsWithStatusD->"+SQL_DEL_OWNER_MAPPINGS_WITH_STATUS_D);
            stmt = conn.prepareStatement(SQL_DEL_OWNER_MAPPINGS_WITH_STATUS_D);
            stmt.setString(1, ownerid);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to deleteMappingsWithStatusD -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	@Override
	public boolean deleteMappingsWithStatusD() {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("deleteMappingsWithStatusD->"+SQL_DEL_MAPPINGS_WITH_STATUS_D);
            stmt = conn.prepareStatement(SQL_DEL_MAPPINGS_WITH_STATUS_D);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to deleteMappingsWithStatusD -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#updateMapping(java.lang.String, java.lang.Long, java.lang.String)
	 */
	public boolean updateMapping(String ownerid, Long contactid, String borqsid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("updateMapping->"+String.format(UPDATE_MAPPING.replace("?", "%s"), borqsid, ownerid, contactid));
            stmt = conn.prepareStatement(UPDATE_MAPPING);
            stmt.setString(1, borqsid);
            stmt.setString(2, ownerid);
            stmt.setLong(3, contactid);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to updateMapping -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/**
	 * create Mapping
	 * @param ownerid
	 * @param contactid
	 * @param borqsid
	 * @return
	 */
	@Override
	public boolean createMapping(String ownerid, Long contactid, String borqsid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("createMapping->"+String.format(CREATE_MAPPING.replace("?", "%s"), borqsid, ownerid, contactid));
            stmt = conn.prepareStatement(CREATE_MAPPING);
            stmt.setString(1, borqsid);
            stmt.setString(2, ownerid);
            stmt.setLong(3, contactid);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to createMapping -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/**
	 * clear mappings
	 * @return
	 */
	public boolean clearMapping() {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("clearMapping->"+CLEAR_MAPPING);
            stmt = conn.prepareStatement(CLEAR_MAPPING);
            stmt.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to createMapping -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        return false;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#countMappingByOwner(java.lang.String)
	 */
	public int countMappingByOwner(String ownerid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        int count = 0;
        try {
        	log.info("countMappingByOwner->"+String.format(COUNT_SQL_BY_OID.replace("?", "%s"), ownerid));
            stmt = conn.prepareStatement(COUNT_SQL_BY_OID);
            stmt.setString(1, ownerid);
            rset = stmt.executeQuery();
            while(rset.next()) {
            	count = rset.getInt("c");
            }
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to countMappingByOwner -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        
		return count;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#countMappingByBorqsId(java.lang.String)
	 */
	public int countMappingByBorqsId(String borqsid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        int count = 0;
        try {
        	log.info("countMappingByBorqsId->"+String.format(COUNT_SQL_BY_BID.replace("?", "%s"), borqsid));
            stmt = conn.prepareStatement(COUNT_SQL_BY_BID);
            stmt.setString(1, borqsid);
            rset = stmt.executeQuery();
            while(rset.next()) {
            	count = rset.getInt("c");
            }
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to countMappingByBorqsId -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        
		return count;
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#selectIntoMapping(java.lang.String)
	 */
	public void selectIntoMapping(String ownerid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("selectIntoMapping->"+String.format(SELECT_INTO_MAPPING_BY_OID.replace("?", "%s"), ownerid));
            stmt = conn.prepareStatement(SELECT_INTO_MAPPING_BY_OID);
            stmt.setString(1, ownerid);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to selectIntoMapping -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#selectIntoMappingA(java.lang.String)
	 */
	public void selectIntoMappingA(String ownerid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
        	log.info("selectIntoMappingA->"+String.format(SELECT_INTO_MAPPING_BY_OID_A.replace("?", "%s"), ownerid));
            stmt = conn.prepareStatement(SELECT_INTO_MAPPING_BY_OID_A);
            stmt.setString(1, ownerid);
            stmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to selectIntoMappingA -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
	}
	
	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#fetchContacts(java.lang.String)
	 */
	public List<MappingContactItem> fetchNoMappingContacts(String ownerid) {
        PreparedStatement stmt = null;
        ResultSet rset = null;

        List<MappingContactItem> contactItems = new ArrayList<MappingContactItem>();
        try {
        	log.info("fetchNoMappingContacts->"+String.format(SELECT_NO_MAPPING_CONTACTS.replace("?", "%s"), ownerid));
            stmt = conn.prepareStatement(SELECT_NO_MAPPING_CONTACTS);
            stmt.setString(1, ownerid);
            rset = stmt.executeQuery();
            while(rset.next()) {
            	int type = rset.getInt("type");
            	String value = rset.getString("value");

            	// phone
				if(Utility.isPhone(type)) {
					value = Utility.formatPhone(value);
				}
				
				// mail
				if(Utility.isMail(type)) {
					value = Utility.formatMail(value);
				}
				contactItems.add(new MappingContactItem(
            			rset.getString("ownerid"),
            			rset.getLong("contactid"),
            			rset.getString("borqsid"),
            			type,
            			value));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            log.error("failed to fetchContacts -> ", e);
        } finally {
        	try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
        }
        
		return contactItems;
	}

	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#setupDataSource()
	 */
	public DataSource setupDataSource() {
		datasource.setDriverClassName(dbDriver);
		datasource.setUsername(userName);
		datasource.setPassword(password);
    	datasource.setUrl(connectURI);
    	return datasource;
	}
    
    /* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.dao.IContactsMappingDao#shutdownDataSource()
	 */
    public void shutdownDataSource() throws SQLException {
    	datasource.close();
	}
}
