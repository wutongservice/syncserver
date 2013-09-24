package com.borqs.sync.server.common.sql;

public class SqlConnectionException extends RuntimeException {

	public SqlConnectionException(String desc, Exception e) {
		super(desc, e);
	}

	public SqlConnectionException(String desc) {
		super(desc);
	}

}
