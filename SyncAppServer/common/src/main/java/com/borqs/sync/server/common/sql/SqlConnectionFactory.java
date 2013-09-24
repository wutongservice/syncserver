package com.borqs.sync.server.common.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class SqlConnectionFactory {
	private String id;
	private DataSource dataSource;

	SqlConnectionFactory(Properties config) {
		this.id = config.getProperty("id");
		try {
			this.dataSource = BasicDataSourceFactory.createDataSource(config);
		} catch (Exception e) {
			throw new SqlConnectionException("Failed to initiate the data source. cause: ", e);
		}
	}
	
	SqlConnectionFactory(String id, DataSource dataSource) {
		this.id = id;
		this.dataSource = dataSource;
	}
	
	public String getId() {
		return id;
	}
	
	public Connection getConnection() {
		if(dataSource == null) {
			throw new SqlConnectionException("Data source is not initiated.");
		}
		
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new SqlConnectionException("Failed to get connection. cause: ", e);
		}
	}
	
	
}
