package com.borqs.sync.server.webagent.contacts.mapping.logic;

import java.util.List;

import com.borqs.sync.server.common.account.UserInfo;
import com.borqs.sync.server.common.providers.ContactMapping;

public interface IContactsMappingService {

	public abstract List<ContactMapping> fetchMappingByOID(String ownerid);
	public abstract List<ContactMapping> fetchMappingByBID(String borqsId);

	/**
	 * convert mappings to JSON
	 * @param mappings
	 * @param cols TODO
	 * @return
	 */
	public abstract String convertToJson(List<ContactMapping> mappings, String cols);

	/**
	 * delete all mappings by BorqsID
	 * @param borqsId
	 */
	public void deleteMappingsByBorqsId(String borqsId);
	
//	/**
//	 * update mappings by phone and BorqsID
//	 * @param borqsid
//	 * @param value
//	 * @return TODO
//	 */
//	public abstract boolean updateMappingsByPhone(String borqsid, String value);
	
//	/**
//	 * update mappings by email and BorqsID
//	 * @param borqsid
//	 * @param value
//	 * @return 
//	 */
//	public abstract boolean updateMappingsByEmail(String borqsid, String value);

	/**
	 * refresh contacts mappings
	 * @param userInfo
	 */
	public abstract void refreshMappingsOfUser(UserInfo userInfo);
}