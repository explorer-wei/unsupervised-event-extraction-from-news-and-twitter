package database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * set mysql parameters in this class
 * @author Xuan Zhang
 * 			 
 */
public class MysqlConnector {
	// database name
	public static final String DBNAME = "twapperkeeper_2";
	// database host ip
	public static final String DBHOST = "128.173.237.170";
	// database port
	public static final int DBPORT = 3306;
	// database user name
	public static final String DBUSER = "vts";
	// database user password
	public static final String DBPASSWORD = "vts_twitter";
	
	public static final String MYSQL_Driver = "com.mysql.jdbc.Driver";
	public static final String MYSQL_CONPrefix = "jdbc:mysql://";
	
	/**
	 * get connection from mysql
	 * @return Connection
	 */
	public Connection getConnection() {
		return getConnection(MYSQL_Driver, MYSQL_CONPrefix);
	}
	
	/**
	 * get Connection given driver and connection prefix of certain database
	 * 
	 * @return Connection
	 */
	public Connection getConnection(String driver, String conPrefix) {
		if (DBNAME == null || DBHOST == null || DBPORT == -1 || DBUSER == null
				|| DBPASSWORD == null)
			return null;
		try {
			Class.forName(driver);
			Connection con = null;

			String conString = conPrefix + DBHOST + ":" + DBPORT + "/" + DBNAME+"?autoReconnect=true";

			con = DriverManager.getConnection(conString, DBUSER, DBPASSWORD);
			if (con != null) {
				con.setAutoCommit(true);
				return con;
			}
			return con;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
