package com.borqs.sync.server.webagent.api;

import java.util.List;

import com.borqs.sync.server.common.exception.AccountException;
import com.borqs.sync.server.common.httpservlet.ResponseWriter;

public interface IContactsAPIService {

	public abstract void addContacts(String userId, List<String> contacts, ResponseWriter writer, String callback) throws AccountException;

	public abstract void loadContacts(String userid, String fields, ResponseWriter writer, String callback) throws AccountException;
	public abstract void loadContact(String userid, String[] contactIds, String fields, ResponseWriter writer, String callback) throws AccountException;
	public abstract void loadContactsWithLimit(String userid, String fields, int offset, int count, ResponseWriter writer, String callback) throws AccountException;
	
	public abstract void updateContact(String userId, String contactId, String contactJson, ResponseWriter writer, String callback) throws AccountException;

	public abstract void count(String userId, ResponseWriter writer, String callback) throws AccountException;

	public abstract void deleteContacts(String userId, ResponseWriter writer, String callback) throws AccountException;
	public abstract void deleteContact(String userId, String[] ids, ResponseWriter writer, String callback) throws AccountException;
	public abstract void batchDeleteContact(String userId, String ids, ResponseWriter writer, String callback) throws AccountException;

}