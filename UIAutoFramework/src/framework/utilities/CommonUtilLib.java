/* Utilities Library has all the required methods to perform non-UI utility methods
 *
 * Guideline: Only add the non UI reusable methods in this file
 */

package framework.utilities;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import framework.constants.ITestdataEnums;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utilities Library has all the required methods to perform non-UI actions or
 * operations like getting the locators from OR.properties, check whether a test
 * should be skipped, extract digits form a string etc.
 *
 * @Guideline Only add the non UI reusable methods in this file
 */
public class CommonUtilLib implements ITestdataEnums {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Add or reduce number of days to today's date when the download schedule is
	 * selected as a specific date
	 *
	 * @param numOfDays
	 *            Number of days to be added or subtracted
	 * @return newDate Returns the added or subtracted date
	 * @note This is called by selectPackageAndSchedule() when specified date is
	 *       selected.
	 */

	public String addDaysToDate(int numOfDays) {
		Date dt;
		String newDate = null;
		logger.info("Started addDaysToDate method execution");
		try {
			Date date = new Date(System.currentTimeMillis());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, numOfDays);
			dt = cal.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			newDate = formatter.format(dt);
			logger.info("New date is " + newDate);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to add days to date", e);
		}
		finally {
			logger.info("addDaysToDate method execution completed");
		}
		return newDate;
	}

	/**
	 * To extract numbers or digits from a string
	 *
	 * @param str
	 *            String from which numbers should be extracted
	 * @return num Returns only the numbers
	 * @note - This is mainly used when we have to pass job name (or string which
	 *       contains #) in dynamic xpath
	 */

	public long extractDigits(String str) {
		long num = 0;
		logger.info("Extracting digits out of " + str);
		try {
			num = Long.parseLong(str.replaceAll("[^0-9]", ""));
			logger.info("Extracted: " + num);
		}
		catch (Exception e) {
			logger.error("Unable to extract digits", e);
			e.printStackTrace();
		}
		return num;
	}

	/**
	 * To create a time stamp without spaces. This is called by
	 * selectedDeviceConfirmation() method to create Job name.
	 *
	 * @return ts Returns the time stamp
	 */

	public String createTimeStamp() {
		String ts = null;
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
			ts = sdf.format(timestamp).toString().replaceAll("[.]", "");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ts;
	}

	/**
	 * To generate random number of a length - charLength
	 *
	 * @param charLength
	 *            Length of the random number to be generated
	 * @return rn Returns a random number
	 */
	public int generateRandomNumber(int charLength) {
		String rn = null;
		logger.info("Started generateRandomNumber method execution");
		try {
			rn = String.valueOf(charLength < 1 ? 0
					: new Random().nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
							+ (int) Math.pow(10, charLength - 1));
			logger.info("Generated random number " + rn);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			logger.info("generateRandomNumber method execution completed");
		}
		return Integer.parseInt(rn);
	}

	/**
	 * To kill the browser driver process
	 */

	public void killBrowserDriverProcess() {
		try {
			Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
			Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
			Runtime.getRuntime().exec("taskkill /F /IM IEDriver.exe");
			logger.info("Killed all the browser driver Processs");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param specifiedDataTimeStamp
	 * @return This Method Converts Input Time Stamp to Date.
	 * @throws ParseException
	 */
	public String getDateFromTimeStamp(long specifiedDataTimeStamp) throws ParseException {
		Date changeDate = new Date(specifiedDataTimeStamp * 1000);
		SimpleDateFormat smpDateFormat = new SimpleDateFormat("MM-dd-yyyy");
		smpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateWithoutTime = smpDateFormat.format(changeDate);

		return dateWithoutTime;
	}

	/**
	 * @param inputValue
	 * @return Time Stamp Format based on inputValue
	 */
	public String timeStampFormat(TIMESTAMP inputValue) {
		String returnValue = null;

		switch (inputValue) {
		case SECONDS:
			returnValue = Long.toString(System.currentTimeMillis());
			break;
		case MILLISECONDS:
			returnValue = Long.toString(System.currentTimeMillis() * 1000);
			break;
		case MINUTES:
			returnValue = Long.toString(System.currentTimeMillis() / 60);
			break;
		case HOURS:
			returnValue = Long.toString(System.currentTimeMillis() / (60 * 60));
			break;
		case DAYS:
			returnValue = Long.toString(System.currentTimeMillis() / (60 * 60 * 24));
			break;
		default:
			logger.info("Invalid Time Stamp Input");
			break;
		}

		return returnValue;
	}

	/**
	 * To stop a thread by ID
	 *
	 * @param threadID
	 *            ID of the thread to be stopped
	 */
	public void stopThread(long threadID) {
		logger.info("Stopping the thread: " + threadID);
		try {
			Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
			for (Thread thread : setOfThread) {
				if (thread.getId() == threadID)
					thread.interrupt();
			}
		}
		catch (Exception e) {
			logger.error("Unable to stop the thread: " + threadID);
			e.printStackTrace();
		}
	}

	/**
	 * @param valueInTextBox
	 * @return Method Increments Value by 1 and makes sure the range is between 0 to
	 *         10
	 */
	public String range1to10(String valueInTextBox) {
		int value = Integer.parseInt(valueInTextBox);
		value++;
		if (value >= 10) {
			value = value - 10;
		}

		logger.info("Generated Value between 1 to 10 is : " + value);
		return Integer.toString(value);
	}

	/**
	 * To get the name of the calling method
	 *
	 * @return methodName Returns the name of the calling method
	 * @note Need to update the logic
	 */

	public String getMethodName() {
		String methodName = null;
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stacktrace.length; i++) {
			if (stacktrace[i].getMethodName().equals("method")) {
				methodName = stacktrace[i + 1].getMethodName();
				break;
			}
		}
		return methodName;
	}

	/**
	 * To get data from Multimap
	 *
	 * @param mltMap
	 *            Multimap object name
	 * @param key
	 *            Name of the key added in Multimap object
	 * @param fetchMultiple
	 *            Pass true to split the cell containing
	 * @return arrData Returns an array of objects (which should be type casted to
	 *         String[]) if fetchMultiple is set to true or if the data span
	 *         acrosses multiple cells returns an object (which should be type
	 *         casted to String) if fetchMultiple is set to false
	 *         <p>
	 *         Update : isMergeSameValues flag
	 */

	public Object getMultimapData(Multimap<String, String> mltMap, String key, boolean isMergeSameValues,
			boolean fetchMultiple) {
		Object[] arrData = null, arrKeys = null;
		Object data = null;
		int mapLen = 0;
		try {
			mapLen = mltMap.get(key).toArray().length;
			if (mapLen == 0) {
				logger.info("No data exist in multimap for the key " + key);
				return null;
			}
			else {
				if (fetchMultiple) {
					if (mapLen == 1)
						arrData = mltMap.get(key).toString().replace("[", "").replace("]", "").split(";");
					else if (mapLen > 1)
						arrData = mltMap.get(key).toArray();

					Multimap<String, String> invertedMultimap = Multimaps.invertFrom(mltMap,
							ArrayListMultimap.<String, String>create());

					if (mapLen == 1)
						arrKeys = invertedMultimap.get(String.valueOf(arrData[0])).toString().replace("[", "")
								.replace("]", "").split(";");
					else if (mapLen > 1)
						arrKeys = invertedMultimap.get(String.valueOf(arrData[0])).toArray();

					if (verifyIfAllElementsAreSameInArray(arrData) && verifyIfAllElementsAreSameInArray(arrKeys)
							&& isMergeSameValues) {
						Object[] newArrData = new Object[1];
						newArrData[0] = arrData[0];
						logger.info("All the keys and all the respective values are same. Retrieving " + newArrData[0]
								+ " for key " + key);
						return convertObjectToStringArray(newArrData);
					}
					else {
						for (int i = 0; i < arrData.length; i++) {
							arrData[i] = ((String) arrData[i]).trim();
							logger.info("For key " + key + " map contains value: " + arrData[i]);
						}
						arrData = convertObjectToStringArray(arrData);
						return arrData;
					}
				}
				else {
					data = mltMap.get(key).toArray()[0];
					logger.info("Retrieving " + (String) data + " for key " + key);
					return data;
				}
			}
		}
		catch (Exception e) {
			logger.error("Unable to retrieve data from multimap", e);
			e.printStackTrace();
		}
		return arrData;
	}

	/**
	 * stopOnDemand method is to stop Test Execution for an Expected Logic flow
	 */
	@SuppressWarnings("deprecation")
	public void stopOnDemand() {
		Thread.currentThread().stop();
		logger.info("Stopping Test Script Execution On Demand");

	}

	/**
	 * Trim all the strings in an array
	 *
	 * @param array
	 *            String[] to be trimmed
	 * @return trimmedArray returns String[] whose string doesn't have leading or
	 *         trailing spaces
	 */
	public String[] trimStringsInArray(String[] array) {
		String[] trimmedArray = null;
		logger.info("Started trimStringsInArray method execution");
		try {
			trimmedArray = new String[array.length];
			for (int i = 0; i < array.length; i++)
				trimmedArray[i] = array[i].trim();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to trim strings in the array", e);
		}
		finally {
			logger.info("trimStringsInArray method execution completed");
		}
		return trimmedArray;
	}

	/**
	 * To convert an object array to string array
	 *
	 * @param objectArray
	 *            Object[] to be converted
	 * @return stringArr returns String[]
	 */
	public String[] convertObjectToStringArray(Object[] objectArray) {
		String[] stringArray = null;
		logger.info("Started convertObjectToStringArr method execution");
		try {
			stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to convert object array to string array", e);
		}
		finally {
			logger.info("convertObjectToStringArr method execution completed");
		}
		return stringArray;
	}

	/**
	 * @param inputValue
	 * @return Returns 0 for False and 1 for True
	 */
	public String setStringIntValue(String inputValue) {
		if (inputValue.equalsIgnoreCase("True"))
			return "1";
		else
			return "0";
	}

	/**
	 * To get a file extension from the Path / Name of the file
	 *
	 * @param filePath
	 *            Path / Name of the file whose extension should be found
	 * @return Returns file extension
	 */
	public String getFileExtension(String filePath) {
		String extension = null;
		try {
			int intPos = -1;
			for (int i = filePath.length() - 1; i > 0; i--) {
				if (filePath.charAt(i) == '.') {
					intPos = i;
					break;
				}
			}
			extension = filePath.substring(intPos);
			logger.info("Extension of " + filePath + " is " + extension);
		}
		catch (Exception e) {
			logger.error("Unable to get the file extension from " + filePath);
		}
		return extension;
	}

	/**
	 * To check if all the elements in the array are same or not
	 *
	 * @param array
	 *            Array to be verified
	 * @return True if all the elements in the array are same. False if all the
	 *         elements are not same
	 */
	public boolean verifyIfAllElementsAreSameInArray(Object[] array) {
		if (array.length == 0) {
			return true;
		}
		else {
			Object first = array[0];
			for (Object element : array) {
				if (!element.equals(first)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * To get the first element of an array and return it as another array with only
	 * the first value
	 *
	 * @param arr
	 *            Array whose first element should be extracted
	 * @return newArr Array which contains first element of arr
	 */
	public String[] getFirstElementFromArray(String[] arr) {
		String[] newArr = new String[1];
		newArr[0] = arr[0];
		return newArr;
	}

	/**
	 * To search for a keyword in a file
	 *
	 * @param path
	 *            Path (with filename and extension) where it is located
	 * @param searchKeyword
	 *            String to be searched for
	 * @return found Returns true if the searchKeywork is found else returns false
	 */
	public boolean searchInFile(String path, String searchKeyword) {
		boolean found = false;
		try {
			BufferedReader r = new BufferedReader(new FileReader(path));
			String s = r.readLine();
			logger.info("Trying to search " + searchKeyword + " in " + path);
			while (s != null) {
				logger.info(s);
				if (s.contains(searchKeyword)) {
					found = true;
					break;
				}
				s = r.readLine();
			}
			r.close();
			if (found)
				logger.info("Found " + searchKeyword + " in " + path);
			else
				logger.info(searchKeyword + " doesn't exist in " + path);
		}
		catch (Exception e) {
			logger.error("Unable to search for " + searchKeyword + " in " + path);
		}
		return found;
	}

	/**
	 * To convert database timestamp format to UI timestamp format
	 *
	 * @param dbTimeStamp
	 *            DB Timestamp value which should be converted
	 * @return returns the time stamp converted to UI format
	 */
	public String convertDBTimestampFormatToUIStringFormat(String dbTimeStamp) {
		String convertedTS = null;
		try {
			SimpleDateFormat dbformat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			SimpleDateFormat uiformat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a");
			convertedTS = uiformat.format(dbformat.parse(dbTimeStamp));
		}
		catch (Exception e) {
			logger.error("Unable to convert the DB timestamp to UI timestamp", e);
			e.printStackTrace();
		}
		return convertedTS;
	}

	/**
	 * To check whether a timestamp is DB timestamp or not
	 *
	 * @param timeStamp
	 *            Timestamp value which should be checked
	 * @return Returns true if the timeStamp is in DB timestamp format. False if its
	 *         not a DB timestamp format
	 */
	public boolean isDBTimestamp(String timeStamp) {
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		try {
			format.parse(timeStamp);
			return true;
		}
		catch (java.text.ParseException e) {
			return false;
		}
		catch (Exception e) {
			logger.error("Unable to check whether " + timeStamp + " is a timestamp or not!");
			return false;
		}
	}

	/**
	 * To format the string array to pass as a where condition in the SQL query
	 *
	 * @param whereCondition
	 *            String[] which should be formatted
	 * @return value Formatted value
	 */
	public String formatSQLWhereCondition(String[] whereCondition) {
		StringBuilder value = new StringBuilder();
		try {
			value.append("'" + whereCondition[0] + "'");
			if (whereCondition.length > 1) {
				for (int i = 1; i < whereCondition.length; i++) {
					value.append(" , ");
					value.append("'" + whereCondition[i] + "'");
				}
			}
			logger.info("Formatted where condition is " + value.toString());
		}
		catch (Exception e) {
			logger.error("Unable to format the string array " + Arrays.toString(whereCondition));
		}
		return value.toString();
	}

	/**
	 * To get the hostname where the execution is performed. This is called by
	 * ReportConfig() method. Don't call this method directly.
	 *
	 * @return hostname Hostname of the system
	 */

	public String getHostname() {
		InetAddress ip;
		String hostname = null;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
		}
		catch (UnknownHostException e) {

			e.printStackTrace();
		}
		return hostname;
	}
}