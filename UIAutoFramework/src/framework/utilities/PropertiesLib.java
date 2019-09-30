/* PropertiesLib Library contains the most commonly used methods to perform actions on properties
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import framework.constants.IFrameworkConstants;
import framework.libraryinit.PageFactory;

/**
 * This class contains all the methods / actions that can be performed on a
 * properties file
 */
public class PropertiesLib implements IFrameworkConstants {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	PageFactory bs;
	private Properties prop;

	public PropertiesLib(PageFactory bs) {
		this.bs = bs;
	}

	public PropertiesLib() {
	}

	/**
	 * To read the properties file. This is called by setUp() method to load all the
	 * properties file
	 *
	 * @param strPath
	 *            Path of the properties file to be read
	 * @return prop Returns the property file object
	 */

	public Properties readProperties(String strPath) {
		prop = new Properties();

		try {
			FileInputStream fis = new FileInputStream(strPath);
			prop.load(fis);
			fis.close();
		}
		catch (IOException | NullPointerException e) {
			System.out.println("Invalid path or file name: " + strPath);
		}
		return prop;
	}

	/**
	 * Update the Object repository (OR) for dynamic elements or SQL properties file
	 * E.g. To update the DB selection in "Use <DB - TBU>" statement.
	 *
	 * @param prpObj
	 *            prpOR or prpSQL is passed to update an object property or a SQL
	 *            query
	 * @param ele
	 *            Is the property name to be updated
	 * @param oldVal
	 *            Is the previous string being replaced e.g. TBU - To be updated
	 * @param newVal
	 *            Is the new (dynamic) string being updated
	 */

	public void updateProperty(Properties prpObj, String ele, String oldVal, String newVal) {
		String updatedProp = null;
		String existingProp = null;

		try {
			existingProp = prpObj.getProperty(ele);
			logger.debug("Current property value of " + ele + " is " + existingProp);
			updatedProp = existingProp.replaceFirst(oldVal.trim(), newVal.trim());
			prpObj.setProperty(ele, updatedProp);
			logger.info("Updated property value of " + ele + " is " + updatedProp);
		}
		catch (NullPointerException ne) {
			logger.warn("�ne of the update property value is null");
		}
		catch (Exception e) {
			logger.info("Unable to update the property value");
		}
	}

	/**
	 * To append the SQL query with the where clause or Object repository for
	 * dynamic elements
	 *
	 * @param prpObj
	 *            prpSQL or prpOR is passed to update an object property or a SQL
	 *            query
	 * @param ele
	 *            Is the property name to be updated
	 * @param valToAppend
	 *            Value to be appended after the separator E.g. Old value +
	 *            Separator + New Value
	 */
	public void appendProperty(Properties prpObj, String ele, String valToAppend) {
		String updatedProp = null;
		String existingProp = null;
		try {
			existingProp = prpObj.getProperty(ele);
			logger.debug("Current property value of " + ele + " is " + existingProp);
			updatedProp = existingProp + valToAppend;
			prpObj.setProperty(ele, updatedProp);
			logger.info("Updated property value of " + ele + " is " + updatedProp);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info("Unable to append the property value");
		}
	}

	/**
	 * Method retrievePropertyString retries propertyFilename on Property Files
	 * request basis and replaces
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String retrievePropertyString(String propertyFileName, String propertyName,
			HashMap<String, String> TBUReplace) {
		String processProperty = null;
		Properties property;
		try {
			property = readProperties(propertyFileName);
			processProperty = property.getProperty(propertyName);

			for (Map.Entry replaceTBU : TBUReplace.entrySet()) {
				logger.info("Replacing " + replaceTBU.getKey().toString() + " to "
						+ replaceTBU.getValue().toString().trim());
				processProperty = processProperty.replaceFirst(replaceTBU.getKey().toString().trim(),
						replaceTBU.getValue().toString().trim());
			}
			logger.info("Retrieved Property for Property String " + propertyName + " and constructed string is "
					+ processProperty);
		}
		catch (Exception e) {
			logger.error("Method retrievePropertyString failed due to ", e);
		}
		return processProperty;
	}
}