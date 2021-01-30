package database;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * a sql commander
 * @author Xuan Zhang
 */
public class SQLCMD{
		
	private Connection con = null;

	/**
	 *  execute update sql command
	 * 
	 * @param sql
	 * @return effect line number
	 */
	public int executeUpdate(String sql) {
		if (sql == null || sql.length() == 0) {
			System.err.println("executeSQL: sql can't be empty!");
			return -1;
		}
		int nRowsAffected = 0;

		Statement stmt = null;
		System.out.println("update of " + sql);

		try {
			if(con == null){
				MysqlConnector connector = new MysqlConnector();
				con = connector.getConnection();
			}
			
			if (con == null) {
				return 0;
			}

			stmt = con.createStatement();

			nRowsAffected = stmt.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
			nRowsAffected = -1;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return nRowsAffected;
	}

	/**
	 *  execute query sql command
	 * 
	 * @param sql
	 * @return result list
	 */
	public List<Map<String, Object>> executeQuery(String sql) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		System.out.println("DB Query: " + sql);

		Statement stmt = null;
		ResultSet rs = null;
		try {
			if(con == null){
				MysqlConnector connector = new MysqlConnector();
				con = connector.getConnection();
			}
			
			if (con == null) {
				return null;
			}
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {

				HashMap<String, Object> row = new HashMap<String, Object>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int nColumn = rsmd.getColumnCount();
				for (int i = 1; i <= nColumn; i++) {
					int nType = rsmd.getColumnType(i);
					String strColumnName = rsmd.getColumnName(i);
					switch (nType) {
					case Types.INTEGER: {
						int value = rs.getInt(i);
						row.put(strColumnName, value + "");
						break;
					}
					case Types.BIGINT: {
						long value = rs.getLong(i);
						row.put(strColumnName, value + "");
						break;
					}
						/*
						 * case Types.DECIMAL:{ double value = rs.getDouble(i);
						 * row.put(strColumnName,value+""); break; }
						 */
					case Types.DOUBLE: {
						double value = rs.getDouble(i);
						row.put(strColumnName, value + "");
						break;
					}
					case Types.FLOAT: {
						float value = rs.getFloat(i);
						row.put(strColumnName, value + "");
						break;
					}
						/*
						 * case Types.REAL:{ double value = rs.getDouble(i);
						 * row.put(strColumnName,value+""); break; }
						 * 
						 * case Types.NUMERIC:{ double value = rs.getDouble(i);
						 * row.put(strColumnName,value+""); break; }
						 */
					case Types.SMALLINT: {
						short value = rs.getShort(i);
						row.put(strColumnName, value + "");
						break;
					}
					case Types.LONGVARBINARY: {
						Blob blob = rs.getBlob(i);
						row.put(strColumnName, blob);
						break;
					}
					case Types.DATE: {
						Date date = rs.getDate(i);
						row.put(strColumnName, date);
						break;
					}
					default:
						Object obj = rs.getObject(i);
						row.put(strColumnName, obj);
					}
				}
				result.add(row);
			}
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			result = null;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();					
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * @return the connection
	 */
	public Connection getCon() {
		return con;
	}
	
	public void closeConnection() {
		if(this.con != null)
			try {
				this.con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

}
