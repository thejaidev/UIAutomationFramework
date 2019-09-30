package framework.core;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import driver.TestngDriver;
import framework.constants.ITestdataEnums;
import framework.libraryinit.PageFactory;
import framework.utilities.CommonUtilLib;
import framework.utilities.ConfigurationLib;
import framework.utilities.ExcelLib;
import framework.utilities.FileSystemLib;
import framework.utilities.MonitorSystem;
import framework.utilities.ReportLib;

public abstract class SetUpTearDownScript implements ITestdataEnums {

	public static int testScriptNo = 0, totalNumOfScriptsToBeExec = 0;
	public static String createBrowserPool;
	public static boolean sysMonitor = false;
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent = new ExtentReports();
	public static ExcelLib testData = new ExcelLib();
	private static ConfigurationLib configLib = new ConfigurationLib();
	public PageFactory bs;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String scriptName;
	private int iteration;
	private CommonUtilLib utilLib;
	private FileSystemLib fileSystem;
	private ReportLib report;

	/**
	 * To initialize the test suite
	 */
	@BeforeSuite
	protected void onStart() {
		String reportPath, logFilePath;
		Thread tSysMonitor;
		try {
			utilLib = new CommonUtilLib();
			fileSystem = new FileSystemLib();
			report = new ReportLib();

			PropertyConfigurator.configure(configLib.getlog4jPath());
			logFilePath = configLib.getlogFilePath() + configLib.getlogFileName();
			fileSystem.clearFile(logFilePath);

			logger.info("Started suite execution");

			utilLib.killBrowserDriverProcess();
			if (configLib.getRewriteExistingReportConfig().equals("0")) {
				StringBuilder fileName = new StringBuilder(configLib.getReportFileName());
				fileName.insert(fileName.indexOf("."), "_" + utilLib.createTimeStamp());
				reportPath = configLib.getReportPath() + fileName.toString();
			}
			else
				reportPath = configLib.getReportPath() + configLib.getReportFileName();

			htmlReporter = new ExtentHtmlReporter(reportPath);
			logger.info("Creating the report file");

			fileSystem.createCleanFolder(configLib.getscreenshotPath(),
					configLib.getClearScreenshotsFolderConfig().equals("1"));
			fileSystem.createCleanFolder(configLib.getReportPath(), configLib.getClearReportFolderConfig().equals("1"));

			report.reportConfig(extent, htmlReporter);
			testData.connectToExcel(configLib.getTestDataPath());
			if (TestngDriver.suiteExec == true)
				totalNumOfScriptsToBeExec = testData.searchText(Sheetname.TEST_MAP.toString(),
						Testmap.EXECUTE.toString(), Flag.YES.toString());

			if (configLib.getSystemMonitor().equals("1")) {
				sysMonitor = true;
				tSysMonitor = new Thread(new MonitorSystem());
				tSysMonitor.start();
			}
			createBrowserPool = configLib.getConnectToExistingSessionConfig();

			logger.info("Suite Initialization - Done");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To create a script sandbox
	 *
	 * @param launchBrowser
	 *            Pass true if the scenario required to launch a browser else pass
	 *            false
	 */
	protected void setUp(boolean launchBrowser) throws Exception {
		String testCase, testCaseDescription;
		scriptName = this.getClass().getSimpleName();
		iteration = iteration + 1;
		Thread.currentThread().setName(scriptName + " | " + iteration + " - Thread " + Thread.currentThread().getId());
		bs = new PageFactory();
		bs.testData = testData;
		bs.initializeLibs(bs);
		bs.scriptName = scriptName;
		bs.iteration = iteration;
		logger.debug("Executing the setup method");
		logger.info("Check if the Script: " + scriptName + " | Iteration: " + iteration
				+ " should be executed as per the execution flag");
		skipTestCheck();
		testCase = (String) bs.testData.getCellData(Sheetname.TEST_MAP.toString(), bs.scriptName, bs.iteration,
				Testmap.TEST_SCENARIO.toString(), false);
		testCaseDescription = (String) bs.testData.getCellData(Sheetname.TEST_MAP.toString(), bs.scriptName,
				bs.iteration, Testmap.TEST_SCENARIO_DESC.toString(), false);
		testScriptNo++;
		bs.test = extent.createTest(testScriptNo + ". " + testCase,
				scriptName + " | Iteration: " + iteration + " | " + testCaseDescription);
		bs.testStepNo = 0;
		if (launchBrowser) {
			BrowserFactory browser = new BrowserFactory();
			String strBrowser = (String) bs.testData.getCellData(Sheetname.TEST_DATA.toString(), bs.scriptName,
					bs.iteration, Testdata.BROWSER.toString(), false);
			bs.driver = browser.launchBrowserAndNavigate(strBrowser);
		}
	}

	/**
	 * This is called to connect to existing browser session and create custom
	 * report
	 */
	@AfterMethod
	protected void afterInvocation() {
		try {
			if (createBrowserPool.equalsIgnoreCase("1")) {
				String strBrowser = null;
				if (bs.driver == null)
					logger.debug("There is no browser open");
				else if (bs.isTestSkipped)
					logger.debug("Current test is skipped");
				else if (bs.driver.toString().contains("(null)")) {
					strBrowser = (String) bs.testData.getCellData(Sheetname.TEST_DATA.toString(), bs.scriptName,
							bs.iteration, Testdata.BROWSER.toString(), false);
					if (strBrowser == null || strBrowser.equals(Browser.CHROME.toString()))
						chromePool.remove(bs.driver);
					else if (strBrowser.equals(Browser.FIREFOX.toString()))
						firefoxPool.remove(bs.driver);
					else if (strBrowser.equals(Browser.IE.toString()))
						iePool.remove(bs.driver);
					logger.error("****************************************** Script Failed: " + bs.scriptName
							+ " | Iteration: " + bs.iteration + " ******************************************");
				}
				else if (!bs.driver.toString().contains("(null)")) {
					strBrowser = (String) bs.testData.getCellData(Sheetname.TEST_DATA.toString(), bs.scriptName,
							bs.iteration, Testdata.BROWSER.toString(), false);
					if (strBrowser == null || strBrowser.equals(Browser.CHROME.toString())) {
						chromePool.put(bs.driver, 0);
					}
					else if (strBrowser.equals(Browser.FIREFOX.toString())) {
						firefoxPool.put(bs.driver, 0);
					}
					else if (strBrowser.equals(Browser.IE.toString())) {
						iePool.put(bs.driver, 0);
					}
					logger.info(
							"****************************************** Successfully executed Script: " + bs.scriptName
									+ " | Iteration: " + bs.iteration + " ******************************************");
				}
				bs.report.updateCurrentExecutionStatus();
			}
			else {
				String browser = (String) bs.testData.getCellData(Sheetname.TEST_DATA.toString(), bs.scriptName,
						bs.iteration, Testdata.BROWSER.toString(), false);
				bs.ui.closeBrowser(bs.driver, browser, true);
				bs.report.updateCurrentExecutionStatus();
			}
			if (configLib.getThreadCountConfig().equalsIgnoreCase("1")) {
				if (TestngDriver.suiteExec == true) {
					if (testScriptNo == totalNumOfScriptsToBeExec)
						onFinish();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is called to send the report after suite execution
	 */
	@SuppressWarnings("deprecation")
	@AfterSuite
	protected void onFinish() {
		try {
			String recipients;
			String browser = (String) bs.testData.getCellData(Sheetname.TEST_DATA.toString(), bs.scriptName,
					bs.iteration, Testdata.BROWSER.toString(), false);
			bs.ui.closeBrowser(chromePool);
			bs.ui.closeBrowser(firefoxPool);
			bs.ui.closeBrowser(iePool);
			bs.ui.closeBrowser(bs.driver, browser, true);

			if ((configLib.getRecipients()).trim().isEmpty())
				recipients = System.getProperty("user.name") + "@verifone.com";
			else
				recipients = configLib.getRecipients();

			if (configLib.getSystemMonitor().equals("1"))
				sysMonitor = false;

			if (configLib.getSendMailAfterSuiteExec().equalsIgnoreCase("1"))
				bs.email.sendMail(recipients, configLib.getSubject(),
						"PFA the automation execution report, log, and screenshot(s).", null, true);

			logger.info("Completed suite execution");
			Thread.currentThread().stop();
		}
		catch (Exception e) {
			logger.error("Exception occurred in suite tear down", e);
			Thread.currentThread().stop();
		}
	}

	/**
	 * To check whether a script should executed or skipped based on the execute
	 * flag provided in the test data sheet
	 */
	@SuppressWarnings("deprecation")
	private void skipTestCheck() {
		String execute, executeType;
		try {
			execute = (String) bs.testData.getCellData(Sheetname.TEST_MAP.toString(), bs.scriptName, bs.iteration,
					Testmap.EXECUTE.toString(), false);
			if (execute.equalsIgnoreCase("Yes")) {
				if (configLib.getIsCIS().equalsIgnoreCase("True")) {
					executeType = (String) bs.testData.getCellData(Sheetname.TEST_MAP.toString(), bs.scriptName,
							bs.iteration, Testmap.TYPE.toString(), false);
					if (!executeType.contains(bs.configLib.getType()))
						skipTest(false);
				}
				logger.info("****************************************** Started executing Script : " + bs.scriptName
						+ " | Iteration: " + bs.iteration + " ******************************************");
			}
			else
				skipTest(true);
		}
		catch (Exception e) {
			Thread.currentThread().stop();
		}
	}

	/**
	 * To skip a test
	 *
	 * @param skippedDueToExecFlag
	 *            Pass true to skip due to execute flag. Pass false to skip due to
	 *            execution type mismatch
	 */
	private void skipTest(boolean skippedDueToExecFlag) {
		logger.warn("****************************************** Skipping Script: " + bs.scriptName + " | Iteration: "
				+ bs.iteration + " ******************************************");
		bs.isTestSkipped = true;
		if (skippedDueToExecFlag)
			throw new SkipException(
					"Run flag is set to No for Script: " + bs.scriptName + " | Iteration: " + bs.iteration);
		else
			throw new SkipException(
					"Exection Type doesn't match for Script: " + bs.scriptName + " | Iteration: " + bs.iteration);
	}
}
