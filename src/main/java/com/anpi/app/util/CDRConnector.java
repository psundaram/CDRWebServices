/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anpi.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.anpi.app.constants.Constants;

public class CDRConnector {

	private String userName;
	private String password;
	private String dbms;
	private String serverName;
	private String portNumber;
	private String dbName;
	private String driverName;

	public CDRConnector() {
		this.userName = Constants.USERNAME;
		this.password = Constants.PASSWORD;
		this.dbms = Constants.DBMS;
		this.serverName = Constants.SERVERNAME;
		this.portNumber = Constants.PORTNUMBER;
		this.dbName = Constants.DBNAME;
		this.driverName = Constants.DRIVERNAME;

	}

	public Connection getDBConnection() {
		System.out.println("GetDB connection");
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		try {
			if (this.dbms.equals("mysql")) {
				Class.forName(this.driverName).newInstance();
				conn = DriverManager.getConnection("jdbc:" + this.dbms + "://" + this.serverName + ":" + this.portNumber + "/" + this.dbName, connectionProps);

			} else if (this.dbms.equals("derby")) {
				conn = DriverManager.getConnection("jdbc:" + this.dbms + ":" + this.dbName + ";create=true", connectionProps);
			}
		} catch (SQLException ex) {
			System.out.println("SQLException:" + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException:" + ex.getMessage());
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			System.out.println("InstantiationException:" + ex.getMessage());
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			System.out.println("IllegalAccessException:" + ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println("Connected to database");
		return conn;
	}

	public void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException ex) {
				System.out.println("SQLException:" + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
}
