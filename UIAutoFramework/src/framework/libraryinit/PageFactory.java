package framework.libraryinit;

import com.aventstack.extentreports.ExtentTest;
import framework.constants.IFrameworkConstants;
import framework.utilities.*;
import org.openqa.selenium.WebDriver;

import java.util.Properties;

public class PageFactory implements IFrameworkConstants {

	// Object references for UI
	public UIActionsLib ui;
	public ExcelLib testData;
	public DatabaseLib db;
	public CommonUtilLib utilLib;
	public AssertionLib assertLib;
	public ReportLib report;
	public PropertiesLib property;
	public PropertiesLib propertyBs;
	// public ExtentReports extent;
	public MailLib email;
	public FileSystemLib fileSystem;
	public CSVLib csv;
	public ExcelLib excelReport;
	public ConfigurationLib configLib;

	// Script specific variables
	public String scriptName;
	public String activePage;
	public int iteration, testStepNo;
	public Properties prpOR, prpSQL, prpMsg;
	public WebDriver driver = null;
	public ExtentTest test;
	public boolean isTestSkipped = false;

	public void initializeLibs(PageFactory bs) {

		configLib = new ConfigurationLib();
		property = new PropertiesLib();
		prpOR = property.readProperties(configLib.getORPath());
		prpSQL = property.readProperties(configLib.getSQLPath());
		prpMsg = property.readProperties(configLib.getPopUpMessagePath());
		fileSystem = new FileSystemLib();
		csv = new CSVLib();
		utilLib = new CommonUtilLib();
		db = new DatabaseLib();
		email = new MailLib();
		ui = new UIActionsLib(prpOR);
		report = new ReportLib(bs);
		assertLib = new AssertionLib(bs);
		excelReport = new ExcelLib();
		propertyBs = new PropertiesLib(bs);
	}
}
