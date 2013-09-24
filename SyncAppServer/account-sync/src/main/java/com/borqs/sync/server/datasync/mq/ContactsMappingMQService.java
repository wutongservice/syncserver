package com.borqs.sync.server.datasync.mq;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.borqs.sync.server.common.account.UserInfo;
import com.borqs.sync.server.common.json.JsonWriter;
import com.borqs.sync.server.common.providers.ContactMapping;
import com.borqs.sync.server.common.providers.IContactsMappingDao;
import com.borqs.sync.server.common.providers.MappingContactItem;
import com.borqs.sync.server.common.util.Utility;

public class ContactsMappingMQService implements IContactsMappingMQService {
	private static Logger log = Logger.getLogger(ContactsMappingMQService.class);
	
	private static final String JSON_FIELD_OWNERID = "oid";
	private static final String JSON_FIELD_CONTACTID = "cid";
	private static final String JSON_FIELD_BORQSID = "bid";

	private IContactsMappingDao dao;

	public ContactsMappingMQService() {
		super();
	}

	public ContactsMappingMQService(IContactsMappingDao dao) {
		super();
		this.dao = dao;
	}
	
	private String genFetchBorqsIDsRequest(List<MappingContactItem> items) {
		String request;
		// construct request body
		StringWriter sw = new StringWriter();
		try {
			JsonWriter writer = new JsonWriter(sw);
			writer.beginArray();
			if(null != items) {
				for(MappingContactItem item : items) {
					if(item.getValue()==null || "".equals(item.getValue())) {
						continue;
					}
					
					if(!Utility.isMail(item.getType()) && !Utility.isPhone(item.getType())) {
						continue;
					}
					
					writer.beginObject();
					
					// phone
					if(Utility.isPhone(item.getType())) {
						writer.name("phone").value(Utility.formatPhone(item.getValue()));
//						writer.name("phone").value(item.getValue());
					}
					
					// mail
					if(Utility.isMail(item.getType())) {
						writer.name("email").value(Utility.formatMail(item.getValue()));
//						writer.name("email").value(item.getValue());
					}
					
					writer.endObject();
				}
			}
			writer.endArray();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			log.error("convertToJson: unsupported encoding -> ", e);
		} catch (IOException e) {
			log.error("convertToJson: I/O error -> ", e);
		}
		
		request = sw.toString()
				.replace(" ", "")
				.replace(System.getProperty("line.separator"), "");
		return request;
	}
	

	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.logic.IContactsMappingService#convertToJson(java.util.List<ContactMapping>)
	 */
	public String convertToJson(List<ContactMapping> mappings, String cols) {
		String[] fields = new String[] {JSON_FIELD_OWNERID,JSON_FIELD_CONTACTID,JSON_FIELD_BORQSID};
		if(null != cols) {
			fields = cols.split(",");
		}
		
		Set<String> setCols = new HashSet<String>();
		for(String fld: fields) {
			setCols.add(fld);
		}
		
		StringWriter sw = new StringWriter();
		try {
			JsonWriter writer = new JsonWriter(sw);
			writer.setIndent("  ");
			writer.beginArray();
			if(null != mappings) {
				for (ContactMapping mapping : mappings) {
					writer.beginObject();
					
					if(setCols.contains(JSON_FIELD_OWNERID)) {
						writer.name(JSON_FIELD_OWNERID).value(mapping.getOwnerid());
					}
					
					if(setCols.contains(JSON_FIELD_CONTACTID)) {
						writer.name(JSON_FIELD_CONTACTID).value(mapping.getContactid());
					}
					
					if(setCols.contains(JSON_FIELD_BORQSID)) {
						writer.name(JSON_FIELD_BORQSID).value(mapping.getBorqsid());
					}
					
					writer.endObject();
				}
			}
			writer.endArray();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			log.error("convertToJson: unsupported encoding -> ", e);
		} catch (IOException e) {
			log.error("convertToJson: I/O error -> ", e);
		}
		
		return sw.toString();
	}
	
	public void setDao(IContactsMappingDao dao) {
		this.dao = dao;
	}

	public void deleteMappingsByBorqsId(String borqsId) {
		dao.deleteMappingsByBorqsID(borqsId);
	}

	public boolean updateMappingsByPhone(String borqsid, String value) {
		return dao.updateMappingsByPhone(borqsid, value);
	}

	public boolean updateMappingsByEmail(String borqsid, String value) {
		return dao.updateMappingsByEmail(borqsid, value);
	}
	
	public void refreshMappingsOfUser(UserInfo userInfo) {
		String userId = userInfo.getUserId();
		
		dao.deleteMappingsByBorqsID(userId);
		
		if(null != userInfo.getLogin_email1() && !"".equals(userInfo.getLogin_email1())) {
			dao.updateMappingsByEmail(userId, userInfo.getLogin_email1());
		}
		if(null != userInfo.getLogin_email2() && !"".equals(userInfo.getLogin_email2())) {
			dao.updateMappingsByEmail(userId, userInfo.getLogin_email2());
		}
		if(null != userInfo.getLogin_email3() && !"".equals(userInfo.getLogin_email3())) {
			dao.updateMappingsByEmail(userId, userInfo.getLogin_email3());
		}
		
		if(null != userInfo.getLogin_phone1() && !"".equals(userInfo.getLogin_phone1())) {
			dao.updateMappingsByPhone(userId, userInfo.getLogin_phone1());
		}
		if(null != userInfo.getLogin_phone2() && !"".equals(userInfo.getLogin_phone2())) {
			dao.updateMappingsByPhone(userId, userInfo.getLogin_phone2());
		}
		if(null != userInfo.getLogin_phone3() && !"".equals(userInfo.getLogin_phone3())) {
			dao.updateMappingsByPhone(userId, userInfo.getLogin_phone3());
		}
	}

	@Override
	public void createMapping(String ownerId, Long contactId, String borqsId) {
		dao.createMapping(ownerId, contactId, borqsId);
	}
}
