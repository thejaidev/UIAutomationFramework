/* DatabaseLib Library contains the most commonly used methods to perform actions on DB
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * This class contains all the methods / actions that can be performed on a
 * database
 */
public class DatabaseLib {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private ConfigurationLib configLib = new ConfigurationLib();

	/**
	 * Connect to database based on the details provided in the config.properties
	 * file
	 *
	 */
	public Connection connectToDB() throws Exception {
		String databaseURL = "jdbc:sqlserver://" + configLib.getDatabaseHost();
		String databaseUsername = configLib.getDatabaseUsername();
		String databasePassword = configLib.getDatabasePassword();
		Connection con = null;
		try {
			if (con == null || con.isClosed()) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				con = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
				logger.debug("Connected to DB");
			}
		}
		catch (SQLServerException e) {
			throw new Exception("Unable to conect to DB");
		}
		catch (Exception e) {
			throw new Exception("Please check the DB connection details. Host: " + databaseURL + ", username: "
					+ databaseUsername + ", password: " + databasePassword);
		}
		return con;
	}

	/**
	 * To execute a query - query after connecting to a database and store the
	 * result in resulSettMap with column names and values. In order to get the
	 * results call getDBData(int row, String colName)
	 *
	 * @param query
	 *            Query to be executed
	 * @param waitUntilDataIsLoaded
	 *            Pass true only if you are sure that the query result will NOT BE
	 *            NULL and the data will be populated late in the table. Pass false
	 *            if the insertion is real time and not database wait is required
	 */
	public List<LinkedHashMap<String, String>> executeQuery(String query, boolean waitUntilDataIsLoaded)
			throws Exception {
		int dbWait = 0, rowCount = 0;
		boolean dataIsLoaded = false;
		Statement stmt;
		LinkedHashMap<String, String> resulSettMap = null;
		List<LinkedHashMap<String, String>> dbRow = new ArrayList<LinkedHashMap<String, String>>();
		Connection con = connectToDB();
		logger.info("Executing the sql query - " + query);
		int retryCount = Integer.parseInt(configLib.getRetryAttempts());
		try {
			do {
				try {
					stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = stmt.executeQuery(query);

					ResultSetMetaData meta = rs.getMetaData();

					if ((!dataIsLoaded) && (waitUntilDataIsLoaded)) {
						rs.last();
						rowCount = rs.getRow();
						if (rowCount == 0) {
							logger.warn("Record is not created in the DB");
							throw new Exception("Record is not created in the DB");
						}
						dataIsLoaded = true;
					}
					rs.first();
					do {
						resulSettMap = new LinkedHashMap<String, String>();
						for (int i = 1; i <= meta.getColumnCount(); i++) {
							String key = meta.getColumnName(i);
							String value = rs.getString(key);
							resulSettMap.put(key, value);
						}
						dbRow.add(resulSettMap);
					} while (rs.next());
					logger.info("Successfully executed the query");
					break;
				}
				catch (Exception e) {
					if (waitUntilDataIsLoaded) {
						dbWait++;
						logger.warn("Trying " + dbWait + " more time");
						Thread.sleep(Integer.parseInt(configLib.getRetryDelay()) * 1000);
					}
					else {
						logger.warn("No records retrieved after executing the query");
						break;
					}

				}
			} while (dbWait < retryCount * 2);
		}
		catch (Exception e) {
			logger.error("Unable to execute. Please check the SQL query: " + query, e);
			e.printStackTrace();
		}
		finally {
			con.close();
		}
		return dbRow;
	}

	/**
	 * To get data from the result of executeQuery() which has row - row and column
	 * name - colName
	 *
	 * @param dbRow
	 *            DB result set to be parsed
	 * @param row
	 *            Row to fetch data from the DB resultset
	 * @param colName
	 *            to fetch data from the DB resultset
	 * @return value Returns the value which is fetched from the DB result set
	 * @note - row starts from 0 (and not 1)
	 */
	public String getDBData(List<LinkedHashMap<String, String>> dbRow, int row, String colName) {
		String value = null;
		try {
			value = dbRow.get(row).get(colName);
			logger.info("Retrieved " + value + " from row " + row + " and column " + colName);
		}
		catch (NullPointerException e) {
			logger.warn("Retrieved null from db. Please check the row number " + row + " and column name " + colName
					+ " if the value is not expected to be null");
		}
		catch (IndexOutOfBoundsException e) {
			logger.warn("Result set is empty after executing the query");
		}
		catch (Exception e) {
			logger.error("Unable to fetch the column value from  " + colName + " from row " + row);
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * To get data from the result of executeQuery() which has row - row and column
	 * name - colName
	 *
	 * @param dbRow
	 *            DB result set to be parsed
	 * @param colName
	 *            Column name whose values should be retrieved
	 * @return values String[] of values in the column colName
	 */
	public String[] getDBData(List<LinkedHashMap<String, String>> dbRow, String colName) {
		String[] values = null;
		int totalRecords = -1, i = -1;
		try {
			totalRecords = dbRow.size();
			values = new String[totalRecords];
			for (i = 0; i < totalRecords; i++) {
				values[i] = getDBData(dbRow, i, colName);
			}
		}
		catch (NullPointerException e) {
			logger.warn("Retrieved null from db. Please check the row number " + i + " and column name " + colName
					+ " if the value is not expected to be null");
		}
		catch (IndexOutOfBoundsException e) {
			logger.warn("Result set is empty after executing the query");
		}
		catch (Exception e) {
			logger.error("Unable to fetch the column value from  " + colName + " from row " + i);
			e.printStackTrace();
		}
		return values;
	}

	/**
	 * To get data from the result of executeQuery() which has primary column as
	 * primaryColName and secondary column as secondaryColName
	 *
	 * @param primaryColName
	 *            Name of the primary column (with mostly primary key constraint)
	 * @param secondaryColName
	 *            Name of the secondary column
	 * @return dbMap with key as primary column value and value as secondary column
	 *         value
	 */
	@SuppressWarnings("null")
	public LinkedHashMap<String, String> getDBData(List<LinkedHashMap<String, String>> dbRow, String primaryColName,
			String secondaryColName) {
		LinkedHashMap<String, String> dbMap = null;
		int totalRecords = -1, i = -1;
		String key = null, val = null;
		try {
			totalRecords = dbRow.size();
			for (i = 0; i < totalRecords; i++) {
				key = getDBData(dbRow, i, primaryColName);
				val = getDBData(dbRow, i, secondaryColName);
				dbMap.put(key, val);
			}
		}
		catch (NullPointerException e) {
			logger.warn(
					"Retrieved null from db. Please check the row number " + i + " and primary column " + primaryColName
							+ " and secondary column " + secondaryColName + " if the value is not expected to be null");
		}
		catch (IndexOutOfBoundsException e) {
			logger.warn("Result set is empty after executing the query");
		}
		catch (Exception e) {
			logger.error("Unable to fetch the column value from  primary column" + primaryColName
					+ " and secondary column " + secondaryColName + " from row " + i);
			e.printStackTrace();
		}
		return dbMap;
	}

	/**
	 * To print the complete result set
	 *
	 * @return bs.dbRow Returns a list of linked hash map
	 */
	public void printCompleteResultSet(List<LinkedHashMap<String, String>> dbRow) {
		try {
			String val = null;
			int numOfRows = dbRow.size();
			for (int row = 0; row < numOfRows; row++) {
				Set<String> keyset = dbRow.get(0).keySet();
				for (String col : keyset) {
					val = dbRow.get(row).get(col) + "\t";
					System.out.print(val);
				}
				System.out.println("");
			}
		}
		catch (NullPointerException e) {
			logger.warn("Result set doesn't contain any data");
		}
		catch (Exception e) {
			logger.error("Unable to fetch values from result set");
			e.printStackTrace();
		}
	}

	/**
	 * To validate data in DB
	 *
	 * @param sql
	 *            Query to be executed
	 * @param resultColumnName
	 *            To fetch data from the DB resultset
	 * @param expResult
	 *            Expected result to be validated
	 * @param waitUntilDataIsLoaded
	 *            Pass true only if you are sure that the query result will NOT BE
	 *            NULL and the data will be populated late in the table. Pass false
	 *            if the insertion is real time and not database wait is required
	 * @return validationSuccessful Returns true if validation is successful else
	 *         returns false
	 */
	public boolean validateDBDataIfEquals(String sql, String resultColumnName, String expResult,
			boolean waitUntilDataIsLoaded) throws Exception {
		List<LinkedHashMap<String, String>> dbRow = new ArrayList<LinkedHashMap<String, String>>();
		boolean validationSuccessful = false;
		String actResult = null;
		for (int i = 0; i < Integer.parseInt(configLib.getRetryAttempts()); i++) {
			dbRow = executeQuery(sql, waitUntilDataIsLoaded);
			actResult = getDBData(dbRow, 0, resultColumnName);
			if (actResult == null && expResult == null) {
				validationSuccessful = true;
				logger.info("DB validation is successful. Both actual and expected result is null");
				break;
			}
			else if (actResult == null && expResult != null)
				logger.warn("Actual value is null and expected value is " + expResult);
			else if (actResult != null && expResult == null)
				logger.warn("Expected value is null and actual value is " + expResult);
			else if (actResult.equals(expResult)) {
				validationSuccessful = true;
				logger.info("DB validation is successful. Both actual and expected result is " + expResult);
				break;
			}
			else
				logger.warn("Actual and Expected values are different");
			Thread.sleep(1000 * Integer.parseInt(configLib.getRetryDelay()));
			logger.warn("Trying " + (i + 1) + " more time");
		}
		if (!validationSuccessful)
			throw new Exception("Actual result is different from expected result: " + expResult);
		return validationSuccessful;
	}

	public boolean validateDBDataIfNotEquals(String sql, String resultColumnName, String expResult,
			boolean waitUntilDataIsLoaded) throws Exception {
		List<LinkedHashMap<String, String>> dbRow = new ArrayList<LinkedHashMap<String, String>>();
		boolean validationSuccessful = false;
		String actResult = null;
		for (int i = 0; i < Integer.parseInt(configLib.getRetryAttempts()); i++) {
			dbRow = executeQuery(sql, waitUntilDataIsLoaded);
			actResult = getDBData(dbRow, 0, resultColumnName);
			if (actResult == null && expResult == null) {
				validationSuccessful = false;
				logger.warn("Both actual and expected result is null");
			}
			else if (actResult == null && expResult != null) {
				validationSuccessful = true;
				logger.info("Actual value is null and expected value is " + expResult);
			}
			else if (actResult != null && expResult == null) {
				validationSuccessful = true;
				logger.warn("Expected value is null and actual value is " + expResult);
			}
			else if (!actResult.equals(expResult)) {
				validationSuccessful = true;
				logger.info("DB validation is successful. Both actual and expected result is " + expResult);
				break;
			}
			else
				logger.warn("Actual and Expected values are same");
			Thread.sleep(1000 * Integer.parseInt(configLib.getRetryDelay()));
			logger.warn("Trying " + (i + 1) + " more time");
		}
		if (!validationSuccessful)
			throw new Exception("Both actual and expected result are same: " + expResult);
		return validationSuccessful;
	}
}