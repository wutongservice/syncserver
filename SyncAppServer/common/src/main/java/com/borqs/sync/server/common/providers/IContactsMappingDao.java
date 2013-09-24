package com.borqs.sync.server.common.providers;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

public interface IContactsMappingDao {
	/**
	 * query mappings of contacts by owner ID
	 * @param ownerid
	 * @return
	 */
	public abstract List<ContactMapping> queryContactsMappingsByOID(String ownerid);
	
	/**
	 * query mappings of contacts by Borqs ID
	 * @param borqsId
	 * @return
	 */
	public abstract List<ContactMapping> queryContactsMappingsByBID(String borqsId);

	/**
	 * update mappings by phone and BorqsID
	 * @param borqsid
	 * @param value
	 * @return TODO
	 */
	public abstract boolean updateMappingsByPhone(String borqsid, String value);
	
	/**
	 * update mappings by email and BorqsID
	 * @param borqsid
	 * @param value
	 * @return 
	 */
	public abstract boolean updateMappingsByEmail(String borqsid, String value);

	/**
	 * delete mappings by BorqsID
	 * @param borqsId
	 * @return TODO
	 */
	public abstract boolean deleteMappingsByBorqsID(String borqsId);
	
	/**
	 * delete mappings by OwnerID
	 * @param ownerId
	 * @return TODO
	 */
	public abstract boolean deleteMappingsByOwnerID(String ownerId);

	/**
	 * find contact by item value
	 * @param ownerid
	 * @param value
	 * @return
	 */
	public abstract List<ContactMapping> findContactByItem(String ownerid,
			String value);

	/**
	 * update mapping of contact
	 * @param ownerid
	 * @param contactid
	 * @param borqsid
	 * @return TODO
	 */
	public abstract boolean updateMapping(String ownerid, Long contactid,
			String borqsid);

	/**
	 * count mappings of owner
	 * @param ownerid
	 * @return
	 */
	public abstract int countMappingByOwner(String ownerid);
	
	/**
	 * count mappings of BorqsID
	 * @param borqsid
	 * @return
	 */
	public abstract int countMappingByBorqsId(String borqsid);

	/**
	 * select from contacts table to mapping table by owner ID
	 * @param ownerid
	 */
	public abstract void selectIntoMapping(String ownerid);
	
	/**
	 * incrementally select from contacts table to mapping table by owner ID
	 * @param ownerid
	 */
	public abstract void selectIntoMappingA(String ownerid);

	/**
	 * fetch the items of contact from database
	 * @param ownerid
	 * @return
	 */
	public abstract List<MappingContactItem> fetchNoMappingContacts(String ownerid);

	public abstract DataSource setupDataSource();

	public abstract void shutdownDataSource() throws SQLException;

	/**
	 * delete contacts mappings with status 'D'
	 * @return
	 */
	public abstract boolean deleteMappingsWithStatusD();
	public abstract boolean deleteMappingsWithStatusD(String ownerid);

	public abstract boolean createMapping(String ownerid, Long contactid, String borqsid);

}