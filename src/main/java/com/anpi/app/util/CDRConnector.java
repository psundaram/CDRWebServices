package com.anpi.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.anpi.app.constants.Constants;

public class CDRConnector {


		private static String userName;
		private static String password;
		private static String dbms;
		private static String serverName;
		private static String portNumber;
		private static String dbName;
		private static String driverName;

		public CDRConnector() {
			userName = Constants.USERNAME;
			password = Constants.PASSWORD;
			dbms = Constants.DBMS;
			serverName = Constants.SERVERNAME;
			portNumber = Constants.PORTNUMBER;
			dbName = Constants.DBNAME;
			driverName = Constants.DRIVERNAME;

		}

		public static Connection getDBConnection() throws Exception {
			System.out.println("GetDB connection");
			Connection conn = null;
			Properties connectionProps = new Properties();
			System.out.println("username:"+Constants.USERNAME);
			connectionProps.put("user", Constants.USERNAME);
			connectionProps.put("password", Constants.PASSWORD);

//			try {
				if (Constants.DBMS.equals("mysql")) {
					Class.forName(Constants.DRIVERNAME).newInstance();
					conn = DriverManager.getConnection("jdbc:" + Constants.DBMS + "://" + Constants.SERVERNAME + ":" + Constants.PORTNUMBER + "/" + Constants.DBNAME, connectionProps);

				} else if (dbms.equals("derby")) {
					conn = DriverManager.getConnection("jdbc:" + dbms + ":" + dbName + ";create=true", connectionProps);
				}
				System.out.println("conn"+conn);
//			} catch (SQLException ex) {
//				System.out.println("SQLException:" + ex.getMessage());
//				ex.printStackTrace();
//			} catch (ClassNotFoundException ex) {
//				System.out.println("ClassNotFoundException:" + ex.getMessage());
//				ex.printStackTrace();
//			} catch (InstantiationException ex) {
//				System.out.println("InstantiationException:" + ex.getMessage());
//				ex.printStackTrace();
//			} catch (IllegalAccessException ex) {
//				System.out.println("IllegalAccessException:" + ex.getMessage());
//				ex.printStackTrace();
//			}
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
