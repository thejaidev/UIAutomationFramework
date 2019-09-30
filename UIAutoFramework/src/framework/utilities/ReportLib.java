/* ReportLib Library contains the most commonly used methods to perform actions on extent report
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import driver.TestngDriver;
import framework.constants.ITestdataEnums;
import framework.core.SetUpTearDownScript;
import framework.libraryinit.PageFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * This class contains all the methods / actions for Reports
 */
public class ReportLib implements ITestdataEnums {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private PageFactory bs;
	private ExcelLib testData;
	private ConfigurationLib configLib = new ConfigurationLib();

	public ReportLib(PageFactory bs) {
		this.bs = bs;
		this.testData = bs.testData;
	}

	public ReportLib() {

	}

	/**
	 * To pass a step in the report
	 *
	 * @param message
	 *            Success message to be written in the the report for a passed step
	 */

	public void pass(String message) {
		try {
			bs.testStepNo++;
			bs.test.pass(bs.testStepNo + ". " + message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To add an informational step in the report
	 *
	 * @param message
	 *            Informational message to be written in the the report
	 */

	public void info(String message) {
		try {
			bs.testStepNo++;
			bs.test.info(bs.testStepNo + ". " + message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To report a warning
	 *
	 * @param message
	 *            Warning message to be written in the the report
	 */

	public void warning(String message) {
		try {
			bs.testStepNo++;
			bs.test.warning(bs.testStepNo + ". " + message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To report a skipped step
	 *
	 * @param message
	 *            Skipped step message to be written in the the report
	 */

	public void skip(String message) {
		try {
			bs.testStepNo++;
			bs.test.skip(bs.testStepNo + ". " + message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is the teardown method which should be called in the exception handling
	 * (catch) block to report failure, capture the screenshot, close the browser
	 * and stop the execution in case of an error
	 *
	 * @param message
	 *            Failure message to be written in the report in case of a failed
	 *            step
	 * @param e
	 *            Exception object to print the stack trace in the log and the
	 *            report
	 * @param hardFail
	 *            If true is passed then the script will stop in case of the
	 *            failure. If false is passed then script will continue despite of
	 *            the failure
	 */

	@SuppressWarnings("deprecation")
	public void fail(String message, Throwable e, boolean hardFail) {
		String emailAdd = System.getProperty("user.name") + "@VERIFONE.com";
		String errorMessage, stackTrace = null, mailBody = null;
		if (e != null)
			stackTrace = e.getStackTrace()[0] + System.lineSeparator() + e.getStackTrace()[1] + System.lineSeparator()
					+ e.getStackTrace()[2];
		String logFilePath = configLib.getlogFilePath() + configLib.getlogFileName();
		String testCase = (String) testData.getCellData(Sheetname.TEST_MAP.toString(), bs.scriptName, bs.iteration,
				Testmap.TEST_SCENARIO.toString(), false);
		String testCaseDescription = (String) testData.getCellData(Sheetname.TEST_MAP.toString(), bs.scriptName,
				bs.iteration, Testmap.TEST_SCENARIO_DESC.toString(), false);
		try {
			bs.testStepNo++;
			logger.error(testCase + " ended with an error" + System.lineSeparator(), e);
			if (e == null)
				errorMessage = "";
			else
				errorMessage = e.getMessage();

			String consoleError = bs.ui.getConsoleError(bs.driver);

			if (e != null)
				bs.test.fail(bs.testStepNo + ". " + message + ". " + errorMessage + "<br/> <br/>"
						+ " Error occurred at: " + stackTrace + "<br/> <br/>" + "Console Message: " + consoleError
						+ " <a href='" + logFilePath + "'>" + "View Log" + "</a>");
			else
				bs.test.fail(bs.testStepNo + ". " + message + ". " + errorMessage + "<br/> <br/>" + "Console Message: "
						+ consoleError + " <a href='" + logFilePath + "'>" + "View Log" + "</a>");
			String screenshotPath = getScreenshot();
			bs.test.addScreenCaptureFromPath(screenshotPath);

			if (configLib.getSendMailOnFailure().equalsIgnoreCase("1")) {
				if (e != null)
					mailBody = testCaseDescription + " - Failed. " + message + ". " + errorMessage
							+ " Error occurred at - " + stackTrace + System.lineSeparator() + "Console Message is "
							+ consoleError;
				else
					mailBody = testCaseDescription + " - Failed. " + message + ". " + errorMessage
							+ System.lineSeparator() + "Console Message is " + consoleError;
				bs.email.sendMail(emailAdd, "Script Failed: " + bs.scriptName + " | Iteration: " + bs.iteration
						+ " - Check the report for more detail", mailBody, screenshotPath, false);
			}
		}
		catch (Exception e1) {
			logger.error("Exception occurred while failing the test", e1);
		}
		finally {
			if (hardFail) {
				bs.driver.quit();
				Thread.currentThread().stop();
			}
		}
	}

	/**
	 * To configure the environment section of extent report
	 */

	public void reportConfig(ExtentReports extent, ExtentHtmlReporter htmlReporter) throws Exception {
		CommonUtilLib utilLib = new CommonUtilLib();
		extent.attachReporter(SetUpTearDownScript.htmlReporter);
		extent.setSystemInfo("Host Name", utilLib.getHostname());
		extent.setSystemInfo("OS", System.getProperty("os.name"));
		extent.setSystemInfo("User", System.getProperty("user.name"));
		htmlReporter.config().setDocumentTitle(configLib.getReportDocumentTitle());
		htmlReporter.config().setReportName(configLib.getReportDocumentTitle());
		htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReporter.config().setTheme(Theme.STANDARD);
		logger.info("Configured the report");
	}

	/**
	 * To get browser screenshot. This is called by the fail(). Don't call this
	 * method directly.
	 */

	private String getScreenshot() {
		String screenshotPath = null;
		try {
			String testCase = (String) testData.getCellData(Sheetname.TEST_MAP.toString(), bs.scriptName, bs.iteration,
					Testmap.TEST_SCENARIO.toString(), false);
			logger.info("Taking a screenshot");
			TakesScreenshot ts = (TakesScreenshot) bs.driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			screenshotPath = configLib.getscreenshotPath() + testCase + "_" + bs.utilLib.createTimeStamp() + ".png";
			File destination = new File(screenshotPath);
			FileUtils.copyFile(source, destination, true);
		}
		catch (IOException e) {
			logger.error("Invalid file name. Please change the name of the script in the test data sheet");
		}
		catch (Exception e) {
			logger.error("Unable to take the screenshot. The browser is closed", e);
		}
		return screenshotPath;
	}

	/**
	 * Convert image to base64 to embed in the report
	 *
	 * @param imagePath
	 *            Path of the image to be converted
	 * @return base64 Returns the base64 string value
	 */
	public String imageToBase64(String imagePath) {
		String base64 = "";
		try {
			InputStream iSteamReader = new FileInputStream(imagePath);
			byte[] imageBytes = IOUtils.toByteArray(iSteamReader);
			base64 = Base64.getEncoder().encodeToString(imageBytes);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "data:image/png;base64," + base64;
	}

	/**
	 * To update the current execution status
	 */
	public void updateCurrentExecutionStatus() {
		if (TestngDriver.suiteExec == true) {
			try {

				int percentage = (SetUpTearDownScript.testScriptNo * 100)
						/ SetUpTearDownScript.totalNumOfScriptsToBeExec;
				SetUpTearDownScript.htmlReporter.config()
						.setReportName(configLib.getReportDocumentTitle() + " Status: " + percentage + "% completed. "
								+ SetUpTearDownScript.testScriptNo + " out of "
								+ SetUpTearDownScript.totalNumOfScriptsToBeExec + " scripts executed");
			}
			catch (ArithmeticException e) {
				logger.error("Unable to find the percentage completed", e);
			}
		}
		SetUpTearDownScript.extent.flush();
	}
}