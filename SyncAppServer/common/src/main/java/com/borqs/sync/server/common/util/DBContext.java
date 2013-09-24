/*
 * Copyright (C) 2007-2011 Borqs Ltd.
 *  All rights reserved.
 */
package com.borqs.sync.server.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Date: 8/31/11
 * Time: 6:27 PM
 */
public class DBContext {
	private Connection mConnection;
	private PreparedStatement mStatement;

	public DBContext(Connection conn){
		this(conn, null);
	}


	public DBContext(Connection conn, PreparedStatement st){
		mConnection = conn;
		mStatement = st;
	}

	public Connection getConnection() {
		return mConnection;
	}

	public PreparedStatement getStatement() {
		return mStatement;
	}

	public void setStatement(PreparedStatement st){
		mStatement = st;
	}

	public void release(){
		DBUtility.close(mConnection, mStatement, null);
	}
}
