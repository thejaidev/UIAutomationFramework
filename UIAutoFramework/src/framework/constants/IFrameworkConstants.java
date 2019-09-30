package framework.constants;

import framework.utilities.ConfigurationLib;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;

/**
 * IFrameworkConstants interface contains base suite reference, browser
 * references, browser, test data, and generic enums
 *
 */
public interface IFrameworkConstants {
	ConfigurationLib configLib = new ConfigurationLib();
	public static HashMap<WebDriver, Integer> chromePool = new HashMap<WebDriver, Integer>();
	public static HashMap<WebDriver, Integer> firefoxPool = new HashMap<WebDriver, Integer>();
	public static HashMap<WebDriver, Integer> iePool = new HashMap<WebDriver, Integer>();

	/**
	 * Enum of browsers supported
	 */
	public enum Browser {
		FIREFOX("Firefox"),
		CHROME("Chrome"),
		IE("IE");

		private String name;

		Browser(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Time Stamp Format Values
	 */
	enum TIMESTAMP {
		SECONDS,
		MILLISECONDS,
		MINUTES,
		HOURS,
		DAYS;
	}

	/**
	 * Enum of boolean flags
	 */
	public enum Flag {

		TRUE("True"),
		FALSE("False"),
		YES("Yes"),
		NO("No"),
		ZERO("0"),
		ONE("1");

		private String name;

		Flag(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Enum of all the Days of week
	 */
	public enum DaysOfWeek {

		SUNDAY("Sunday"),
		MONDAY("Monday"),
		TUESDAY("Tuesday"),
		WEDNESDAY("Wednesday"),
		THURSDAY("Thursday"),
		FRIDAY("Friday"),
		SATURDAY("Saturday");

		private String name;

		DaysOfWeek(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	enum ConfigKeyWords {

		CONFIG_PROPERTIES("/Configurations/Config.properties"),
		TEST_DATA_PATH("TestDataPath"),
		TEST_SUITE_PATH("TestSuitePath"),
		BINARY_PATH("BinaryPath"),
		CHROME_DRIVER("ChromeDriver"),
		GECKO_DRIVER("GeckoDriver"),
		IE_DRIVER("IEDriver"),
		LOG4J_PATH("log4jPath"),
		REPORT_PATH("ReportPath"),
		OR_PATH("ORPath"),
		SQL_PATH("SQLPath"),
		POP_UP_MESSAGE_PATH("PopUpMessagePath"),
		DB_HOST("DBHost"),
		DB_USER_NAME("DBUsername"),
		DB_PASSWORD("DBPassword"),
		UI_URL("UIurl"),
		SCHEMA_NAME("SchemaName"),
		COMMON_DB("CommonDB"),
		LOGIN_USERNAME("LoginUsername"),
		LOGIN_PASSWORD("LoginPassword"),
		TYPE("Type"),
		IS_CIS("isCIS"),
		CONNECT_TO_EXISING_SESSION("ConnectToExisingSession"),
		THREAD_COUNT("ThreadCount"),
		INCOGNITO_MODE("IncognitoMode"),
		REWRITE_EXISTING_REPORT("RewriteExistingReport"),
		CLEAR_SCREENSHOTS_FOLDER("ClearScreenshotsFolder"),
		CLEAR_REPORT_FOLDER("ClearReportFolder"),
		REPORT_DOCUMENT_TITLE("ReportDocumentTitle"),
		TIME_OUT("TimeOut"),
		RETRY_ATTEMPTS("RetryAttempts"),
		EXEC_SPEED("ExecSpeed"),
		RETRY_DELAY("RetryDelay"),
		SEND_MAIL_ON_FAILURE("SendMailOnFailure"),
		SEND_MAIL_AFTER_SUITE_EXEC("SendMailAfterSuiteExec"),
		SENDER("Sender"),
		SUBJECT("Subject"),
		RECIPIENTS("Recipients"),
		SYSTEM_MONITOR("SystemMonitor"),
		MONITOR_INTERVAL("MonitorInterval"),
		LOAD_ALERT_LIMIT("LoadAlertLimit"),
		REPORT_FILE_NAME("ReportFileName"),
		SCREENSHOT_PATH("screenshotPath"),
		LOG_FILE_PATH("logFilePath"),
		LOG_FILE_NAME("logFileName"),
		SHARED_FOLDER_PATH("SharedFolderPath"),
		DOWNLOADED_FILES_PATH("DownloadedFilesPath"),
		EXCEL_REPORT_NAME("ExcelReportName"),
		NO_VALUE("NoValue"),
		IS_HEADLESS_MODE("IsHeadlessMode"),
		BROWSER("Browser");

		private final String value;

		ConfigKeyWords(final String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}

	enum PropertiesPath {
		OR_PROP(configLib.getORPath()),
		POP_UP_PROP(configLib.getPopUpMessagePath()),
		SQL_PROP(configLib.getSQLPath());

		private final String value;

		PropertiesPath(final String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}
}
