package com.borqs.sync.server.webagent.contacts.mapping.logic;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.borqs.sync.server.common.account.BorqsIDFetcher;
import com.borqs.sync.server.common.account.UserInfo;
import com.borqs.sync.server.common.json.JsonWriter;
import com.borqs.sync.server.common.providers.ContactMapping;
import com.borqs.sync.server.common.providers.IContactsMappingDao;
import com.borqs.sync.server.common.providers.MappingContactItem;
import com.borqs.sync.server.common.runtime.Context;
import com.borqs.sync.server.common.util.Utility;
import com.borqs.sync.server.webagent.util.WebLog;

public class ContactsMappingService implements IContactsMappingService {
	private static java.util.logging.Logger log = null;
	
	private static final String JSON_FIELD_OWNERID = "oid";
	private static final String JSON_FIELD_CONTACTID = "cid";
	private static final String JSON_FIELD_BORQSID = "bid";

	private IContactsMappingDao dao;
	private Context mContext;

	public ContactsMappingService() {
		super();
	}

	public ContactsMappingService(Context context, IContactsMappingDao dao) {
		super();
		this.dao = dao;
		mContext = context;
		log = WebLog.getLogger(context);
	}

	/* (non-Javadoc)
	 * @see com.borqs.contacts.mapping.logic.IContactsMappingService#fetchMapping(java.lang.String)
	 */
	public List<ContactMapping> fetchMappingByOID(String ownerid) {
		// count 
		log.log(Level.INFO, "start to count mappings of owner!");
		int mappingCount = dao.countMappingByOwner(ownerid);
		log.log(Level.INFO, "the mappings count of the owner "+ownerid+" is "+mappingCount);
		
		// at first time get the mappings of owner
		if(0 == mappingCount) {
			log.log(Level.INFO, "start to select mappings from contacts!");
			dao.selectIntoMapping(ownerid);
		} else {
//			dao.deleteMappingsByOwnerID(ownerid);
			dao.selectIntoMappingA(ownerid);
		}
		
		// clear the deleted contacts mappings
		dao.deleteMappingsWithStatusD(ownerid);
		
		// query mails or phones of the contact who haven't mapping
		log.log(Level.INFO, "start to fetch contacts's items by owner!");
		List<MappingContactItem> items = dao.fetchNoMappingContacts(ownerid);
		log.log(Level.INFO, "items' size is "+((null==items)?0:items.size()));
		
		// have find BorqsIDs by mails or phones
		if(items.size()>0) {
			// fetch BorqsID by items
			log.log(Level.INFO, "start to fetch BorqsID by items!");
			
			// convert to request string
			String request = genFetchBorqsIDsRequest(items);
			log.log(Level.INFO, "genFetchBorqsIDsRequest -> " + request);
			
			String host = mContext.getConfig().getSetting("account_server_host");
			
			// fetch BorqsIDs
			Map<String, String> mapBorqsIDs = null;
			try {
				String borqsids = BorqsIDFetcher.fetchBorqsIDs(host , request);
				log.log(Level.INFO, "BorqsIDFetcher.fetchBorqsIDs -> "+borqsids);
				mapBorqsIDs = BorqsIDFetcher.fromJSON(borqsids);
				log.log(Level.INFO, "mapBorqsIDs size is "+mapBorqsIDs.size());
			} catch (Exception e) {
				e.printStackTrace();
				log.warning("failed to fetch BorqsIDs!");
			}
			
			if(null != mapBorqsIDs && !mapBorqsIDs.isEmpty()) {
				// write BorqsIDs to database
				for(MappingContactItem item : items) {
					if(null != item.getOwnerid() && null != item.getContactid()) {
						String borqsId = item.getBorqsid();
						if(null==borqsId || "".equals(borqsId)) {
							if(Utility.isMail(item.getType())) {
								borqsId = mapBorqsIDs.get("m-"+item.getValue());
							}
							if(Utility.isPhone(item.getType())) {
								borqsId = mapBorqsIDs.get("p-"+item.getValue());
							}
						}
						if(null == borqsId) borqsId = "";
						log.log(Level.INFO, "write item[ownerid="+item.getOwnerid()
								+",contactid="+item.getContactid()
								+",borqsid="+borqsId);
						dao.updateMapping(ownerid, item.getContactid(), borqsId);
					}
				}
			}
		}
		
		// query mappings of owner
		List<ContactMapping> mappings = dao.queryContactsMappingsByOID(ownerid);
		
		return mappings;
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
			log.log(Level.WARNING, "convertToJson: unsupported encoding -> ", e);
		} catch (IOException e) {
			log.log(Level.WARNING, "convertToJson: I/O error -> ", e);
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
			log.log(Level.WARNING, "convertToJson: unsupported encoding -> ", e);
		} catch (IOException e) {
			log.log(Level.WARNING, "convertToJson: I/O error -> ", e);
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
	public List<ContactMapping> fetchMappingByBID(String borqsId) {
		return dao.queryContactsMappingsByBID(borqsId);
	}
}
