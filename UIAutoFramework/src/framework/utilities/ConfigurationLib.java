package framework.utilities;

import framework.constants.IFrameworkConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigurationLib implements IFrameworkConstants {

	private PropertiesLib property = new PropertiesLib();
	private Properties configProperties = property
			.readProperties(System.getProperty("user.dir") + ConfigKeyWords.CONFIG_PROPERTIES.toString());

	/**
	 * @param propertyValue
	 * @return Property names passed in to the method returns the String from Java
	 *         Property or Config.properties
	 */
	private String getPropertyValue(String propertyValue) {
		List<SystemPathList> sysPathList = Arrays.asList(SystemPathList.values());
		String returnValue;
		if ((System.getProperty(propertyValue) != null) && (!(System.getProperty(propertyValue).isEmpty()))) {
			returnValue = System.getProperty(propertyValue);
		}
		else {
			if (sysPathList.toString().contains(propertyValue))
				returnValue = System.getProperty("user.dir") + configProperties.getProperty(propertyValue);
			else if (propertyValue.contains(ConfigKeyWords.TYPE.toString()))
				returnValue = ConfigKeyWords.NO_VALUE.toString();
			else if (propertyValue.contains(ConfigKeyWords.IS_CIS.toString())
					|| propertyValue.contains(ConfigKeyWords.IS_HEADLESS_MODE.toString()))
				returnValue = "False";
			else
				returnValue = configProperties.getProperty(propertyValue);
		}
		return returnValue;
	}

	/**
	 * @return Returns Value of DBHost from Environment Property or
	 *         Config.Properties
	 */
	public String getDatabaseHost() {
		return getPropertyValue(ConfigKeyWords.DB_HOST.toString());
	}

	/**
	 * @return Returns Value of DBUsername from Environment Property or
	 *         Config.Properties
	 */
	public String getDatabaseUsername() {
		return getPropertyValue(ConfigKeyWords.DB_USER_NAME.toString());
	}

	/**
	 * @return Returns Value of DBPassword from Environment Property or
	 *         Config.Properties
	 */
	public String getDatabasePassword() {
		return getPropertyValue(ConfigKeyWords.DB_PASSWORD.toString());
	}

	/**
	 * @return Returns Value of UIurl from Environment Property or Config.Properties
	 */
	public String getUIurl() {
		return getPropertyValue(ConfigKeyWords.UI_URL.toString());
	}

	/**
	 * @return Returns Value of SchemaName from Environment Property or
	 *         Config.Properties
	 */
	public String getSchemaName() {
		return getPropertyValue(ConfigKeyWords.SCHEMA_NAME.toString());
	}

	/**
	 * @return Returns Value of CommonDB from Environment Property or
	 *         Config.Properties
	 */
	public String getCommonDB() {
		return getPropertyValue(ConfigKeyWords.COMMON_DB.toString());
	}

	/**
	 * @return Returns Value of LoginUsername from Environment Property or
	 *         Config.Properties
	 */
	public String getLoginUsername() {
		return getPropertyValue(ConfigKeyWords.LOGIN_USERNAME.toString());
	}

	/**
	 * @return Returns Value of LoginPassword from Environment Property or
	 *         Config.Properties
	 */
	public String getLoginPassword() {
		return getPropertyValue(ConfigKeyWords.LOGIN_PASSWORD.toString());
	}

	/**
	 *
	 * @return Returns Value of TestDataPath from Environment Property or
	 * Config.Properties
	 */
	public String getTestDataPath() {
		return getPropertyValue(ConfigKeyWords.TEST_DATA_PATH.toString());
	}

	/**
	 * @return Returns Value of SQLPath from Environment Property or
	 *         Config.Properties
	 */
	public String getSQLPath() {
		return getPropertyValue(ConfigKeyWords.SQL_PATH.toString());
	}

	/**
	 * @return Returns Value of Type from Environment Property or Config.Properties
	 */
	public String getType() {
		return getPropertyValue(ConfigKeyWords.TYPE.toString());
	}

	/**
	 *
	 * @return Returns Value of Type from Environment Property or Config.Properties
	 */
	public String getIsCIS() {
		return getPropertyValue(ConfigKeyWords.IS_CIS.toString());
	}

	/**
	 * To get the connect to existing session configuration
	 *
	 * @return ConnectToExisingSession
	 */
	public String getConnectToExistingSessionConfig() {
		return getPropertyValue(ConfigKeyWords.CONNECT_TO_EXISING_SESSION.toString());
	}

	/**
	 * To get the thread count from configuration
	 *
	 * @return ThreadCount
	 */
	public String getThreadCountConfig() {
		return getPropertyValue(ConfigKeyWords.THREAD_COUNT.toString());
	}

	/**
	 * To get the incognito mode configuration
	 *
	 * @return IncognitoMode
	 */
	public String getIncognitoModeConfig() {
		return getPropertyValue(ConfigKeyWords.INCOGNITO_MODE.toString());
	}

	/**
	 * To get rewrite existing report config
	 *
	 * @return RewriteExistingReport
	 */
	public String getRewriteExistingReportConfig() {
		return getPropertyValue(ConfigKeyWords.REWRITE_EXISTING_REPORT.toString());
	}

	/**
	 * To get the clear screenshots folder configuration
	 *
	 * @return ClearScreenshotsFolder
	 */
	public String getClearScreenshotsFolderConfig() {
		return getPropertyValue(ConfigKeyWords.CLEAR_SCREENSHOTS_FOLDER.toString());
	}

	/**
	 * To get clear report folder configuration
	 *
	 * @return ClearReportFolder
	 */
	public String getClearReportFolderConfig() {
		return getPropertyValue(ConfigKeyWords.CLEAR_REPORT_FOLDER.toString());
	}

	/**
	 * To get the report document title
	 *
	 * @return ReportDocumentTitle
	 */
	public String getReportDocumentTitle() {
		return getPropertyValue(ConfigKeyWords.REPORT_DOCUMENT_TITLE.toString());
	}

	/**
	 * To get the time out toString() in miniutes from the config file
	 *
	 * @return TimeOut
	 */
	public String getTimeOut() {
		return getPropertyValue(ConfigKeyWords.TIME_OUT.toString());
	}

	/**
	 * To get the retry attempts count from the configuration file
	 *
	 * @return RetryAttrempts
	 */
	public String getRetryAttempts() {
		return getPropertyValue(ConfigKeyWords.RETRY_ATTEMPTS.toString());
	}

	/**
	 * To get the execution speed toString() from the configuration file
	 *
	 * @return ExecSpeed
	 */
	public String getExecSpeed() {
		return getPropertyValue(ConfigKeyWords.EXEC_SPEED.toString());
	}

	/**
	 * To get the retry delay in seconds from configuration file
	 *
	 * @return RetryDelay
	 */
	public String getRetryDelay() {
		return getPropertyValue(ConfigKeyWords.RETRY_DELAY.toString());
	}

	/**
	 * To get the configuration toString() of send email on failure
	 *
	 * @return SendMailOnFailure
	 */
	public String getSendMailOnFailure() {
		return getPropertyValue(ConfigKeyWords.SEND_MAIL_ON_FAILURE.toString());
	}

	/**
	 * To get the configuration toString() of send email after suite execution
	 *
	 * @return SendMailAfterSuiteExec
	 */
	public String getSendMailAfterSuiteExec() {
		return getPropertyValue(ConfigKeyWords.SEND_MAIL_AFTER_SUITE_EXEC.toString());
	}

	/**
	 * To get the sender from the config file
	 *
	 * @return Sender
	 */
	public String getSender() {
		return getPropertyValue(ConfigKeyWords.SENDER.toString());
	}

	/**
	 * To get the subject from the mail for the email which will be sent after suite
	 * execution
	 *
	 * @return
	 */
	public String getSubject() {
		return getPropertyValue(ConfigKeyWords.SUBJECT.toString());
	}

	/**
	 * To get the recipients list from config file
	 *
	 * @return Recipients
	 */
	public String getRecipients() {
		return getPropertyValue(ConfigKeyWords.RECIPIENTS.toString());
	}

	/**
	 * To get the system monitor configuration toString()
	 *
	 * @return SystemMonitor
	 */
	public String getSystemMonitor() {
		return getPropertyValue(ConfigKeyWords.SYSTEM_MONITOR.toString());
	}

	/**
	 * To get the monitor interval in seconds from config file
	 *
	 * @return MonitorInterval
	 */
	public String getMonitorInterval() {
		return getPropertyValue(ConfigKeyWords.MONITOR_INTERVAL.toString());
	}

	/**
	 * To get the load alert limit to monitor system performance
	 *
	 * @return LoadAlertLimit
	 */
	public String getLoadAlertLimit() {
		return getPropertyValue(ConfigKeyWords.LOAD_ALERT_LIMIT.toString());
	}

	/**
	 * To get the firefox driver path from config file
	 *
	 * @return GeckoDriver
	 */
	public String getGeckoDriverPath() {
		return getPropertyValue(ConfigKeyWords.GECKO_DRIVER.toString());
	}

	/**
	 * To get the chrome driver path from config file
	 *
	 * @return ChromeDriver
	 */
	public String getChromeDriverPath() {
		return getPropertyValue(ConfigKeyWords.CHROME_DRIVER.toString());
	}

	/**
	 * To get the IE driver path from config file
	 *
	 * @return IEDriver
	 */
	public String getIEDriverPath() {
		return getPropertyValue(ConfigKeyWords.IE_DRIVER.toString());
	}

	/**
	 * To get the object repository path from config file
	 *
	 * @return ORPath
	 */
	public String getORPath() {
		return getPropertyValue(ConfigKeyWords.OR_PATH.toString());
	}

	/**
	 * To get the pop up message path containing all the application pop ups to be
	 * validated
	 *
	 * @return PopUpMessagePath
	 */
	public String getPopUpMessagePath() {
		return getPropertyValue(ConfigKeyWords.POP_UP_MESSAGE_PATH.toString());
	}

	/**
	 * To get the report path from config file
	 *
	 * @return ReportPath
	 */
	public String getReportPath() {
		return getPropertyValue(ConfigKeyWords.REPORT_PATH.toString());
	}

	/**
	 * To get the report file name containing the complete suite execution report
	 *
	 * @return ReportFileName
	 */
	public String getReportFileName() {
		return getPropertyValue(ConfigKeyWords.REPORT_FILE_NAME.toString());
	}

	/**
	 * To get the screenshot path from config file
	 *
	 * @return screenshotPath
	 */
	public String getscreenshotPath() {
		return getPropertyValue(ConfigKeyWords.SCREENSHOT_PATH.toString());
	}

	/**
	 * To get the log4j configuration path from config file
	 *
	 * @return log4jPath
	 */
	public String getlog4jPath() {
		return getPropertyValue(ConfigKeyWords.LOG4J_PATH.toString());
	}

	/**
	 * To get the log file path from config file
	 *
	 * @return logFilePath
	 */
	public String getlogFilePath() {
		return getPropertyValue(ConfigKeyWords.LOG_FILE_PATH.toString());
	}

	/**
	 * To get the log file name from config file
	 *
	 * @return logFileName
	 */
	public String getlogFileName() {
		return getPropertyValue(ConfigKeyWords.LOG_FILE_NAME.toString());
	}

	/**
	 * To get the shared folder path from config file
	 *
	 * @return SharedFolderPath
	 */
	public String getSharedFolderPath() {
		return getPropertyValue(ConfigKeyWords.SHARED_FOLDER_PATH.toString());
	}

	/**
	 * To get the downloaded files path from config file
	 *
	 * @return DownloadedFilesPath
	 */
	public String getDownloadedFilesPath() {
		return getPropertyValue(ConfigKeyWords.DOWNLOADED_FILES_PATH.toString());
	}

	/**
	 * To get test suite path from config file
	 *
	 * @return TestSuitePath
	 */
	public String getTestSuitePath() {
		return getPropertyValue(ConfigKeyWords.TEST_SUITE_PATH.toString());
	}

	/**
	 * To get the binary path from config file
	 *
	 * @return BinaryPath
	 */
	public String getBinaryPath() {
		return getPropertyValue(ConfigKeyWords.BINARY_PATH.toString());
	}

	public String getIsHeadlessMode() {
		return getPropertyValue(ConfigKeyWords.IS_HEADLESS_MODE.toString());
	}

	public String getBrowser() {
		return getPropertyValue(ConfigKeyWords.BROWSER.toString());
	}

	private enum SystemPathList {
		VALUE1(ConfigKeyWords.TEST_DATA_PATH.toString()),
		VALUE2(ConfigKeyWords.TEST_SUITE_PATH.toString()),
		VALUE3(ConfigKeyWords.BINARY_PATH.toString()),
		VALUE4(ConfigKeyWords.CHROME_DRIVER.toString()),
		VALUE5(ConfigKeyWords.GECKO_DRIVER.toString()),
		VALUE6(ConfigKeyWords.IE_DRIVER.toString()),
		VALUE7(ConfigKeyWords.LOG4J_PATH.toString()),
		VALUE8(ConfigKeyWords.REPORT_PATH.toString()),
		VALUE9(ConfigKeyWords.OR_PATH.toString()),
		VALUE10(ConfigKeyWords.SQL_PATH.toString()),
		VALUE11(ConfigKeyWords.POP_UP_MESSAGE_PATH.toString()),
		VALUE13(ConfigKeyWords.LOG_FILE_PATH.toString()),
		VALUE14(ConfigKeyWords.SCREENSHOT_PATH.toString());

		private final String value;

		SystemPathList(final String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}
}
