package com.borqs.sync.server.common.sql;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SqlConnectionFactoryBuilder {
	public SqlConnectionFactory build(InputStream in) {
		Properties config = new Properties();
		
		try {
			config.load(in);
			return build(config);
		} catch (IOException e) {
			throw new SqlConnectionException("Failed to load data source configuration. cause: ", e);
		}
		
	}
	
	public SqlConnectionFactory build(Properties config) {
		return new SqlConnectionFactory(config);
	}
}
