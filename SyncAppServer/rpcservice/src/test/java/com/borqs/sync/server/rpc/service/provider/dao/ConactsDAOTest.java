/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.rpc.service.provider.dao;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Date: 8/31/11
 * Time: 6:41 PM
 */
public class ConactsDAOTest {

	@Before
	public void setUp() throws Exception {
		initDatabase();
	}

	@After
	public void tearDown() throws Exception {
		clearDatabase();
	}

	@Test
	public void test_getContactsByUserId(){
//		String userId = "xuetong.chen@borqs.com";
//		DatabaseAdapter da = getTestDatabase();
//		ContactDAO dao = new ContactDAO(da);
//
//		List<GroupContacts> contacts;
//		try {
//			contacts = dao.getPersonalContacts(userId);
//			assertTrue(contacts.size()==1);
//			assertTrue(contacts.get(0).getAll().size()>0);
//		} catch (SQLException e) {
//			assertTrue(false);
//		}
	}

	private void initDatabase(){
	}

	private void clearDatabase(){

	}
}
