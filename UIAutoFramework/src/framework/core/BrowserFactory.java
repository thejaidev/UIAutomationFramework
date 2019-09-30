package framework.core;

import framework.constants.IFrameworkConstants;
import framework.utilities.ConfigurationLib;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BrowserFactory implements IFrameworkConstants {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private WebDriver driver = null;
	private ConfigurationLib configLib = new ConfigurationLib();

	/**
	 * To launch respective browser and navigate to the login or dashboard page
	 *
	 * @param strBrowser
	 *            Browser to be launched
	 * @return driver Returns the WebDriver object
	 */
	public WebDriver launchBrowserAndNavigate(String strBrowser) {
		String strEnv;
		try {
			strEnv = configLib.getUIurl();
			if (strBrowser == null)
				strBrowser = Browser.CHROME.toString();
			if (SetUpTearDownScript.createBrowserPool.equalsIgnoreCase("1")) {
				if (strBrowser.equalsIgnoreCase(Browser.CHROME.toString()))
					connectToExistingBrowserIfExist(strBrowser, chromePool);
				else if (strBrowser.equalsIgnoreCase(Browser.FIREFOX.toString()))
					connectToExistingBrowserIfExist(strBrowser, firefoxPool);
				else if (strBrowser.equalsIgnoreCase(Browser.IE.toString()))
					connectToExistingBrowserIfExist(strBrowser, iePool);
				else
					throw new Exception("Invalid browser. Please select either Chrome, Firefox or IE");
			}
			else
				driver = openNewBrowser(strBrowser);
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
			driver.get(strEnv);
			logger.info(" Launched " + strBrowser + " and navigated to VHQ - " + strEnv);
		}
		catch (Exception e) {
			logger.error("Unable to navigate to app", e);
			e.printStackTrace();
		}
		return driver;
	}

	/**
	 * To connect to existing browser if exists otherwise open a new browser
	 *
	 * @param strbrowser
	 *            Browser to be launched
	 * @param browserPool
	 *            Browser pool to be searched
	 */
	private void connectToExistingBrowserIfExist(String strbrowser, Map<WebDriver, Integer> browserPool) {
		try {
			if (browserPool.isEmpty())
				driver = openNewBrowser(strbrowser);
			else {
				cleanUpAbandonedBrowsers(browserPool);
				for (Map.Entry<WebDriver, Integer> browser : browserPool.entrySet()) {
					if (browser.getValue() == 0)
						driver = browser.getKey();
				}
				if (driver == null || driver.toString().contains("(null)"))
					driver = openNewBrowser(strbrowser);
				else {
					browserPool.put(driver, 1);
					logger.info("Connected to existing browser");
				}
			}
		}
		catch (Exception e) {
			logger.error("Unable to connect to existing browser", e);
		}
	}

	/**
	 * To open browser - chrome, firefox, IE
	 *
	 * @param browser
	 *            Browser to be opened
	 * @return driver WebDriver object
	 */
	@SuppressWarnings("deprecation")
	private WebDriver openNewBrowser(String browser) {
		try {
			logger.info("Launched new browser: " + browser);
			if (browser.equalsIgnoreCase(Browser.CHROME.toString())) {
				System.setProperty("webdriver.chrome.driver", configLib.getChromeDriverPath());
				HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
				chromePrefs.put("download.default_directory",
						configLib.getSharedFolderPath() + configLib.getDownloadedFilesPath());
				chromePrefs.put("download.directory_upgrade", "true");
				chromePrefs.put("profile.default_content_settings.popups", 0);
				chromePrefs.put("download.prompt_for_download", "false");
				chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
				chromePrefs.put("safebrowsing.enabled", "true");
				ChromeOptions options = new ChromeOptions();
				if (configLib.getIncognitoModeConfig().equals("1"))
					options.addArguments("incognito");
				options.setExperimentalOption("prefs", chromePrefs);
				options.addArguments("--test-type");
				options.addArguments("--disable-extensions");
				options.addArguments("--safebrowsing-disable-extension-blacklist");
				options.addArguments("--safebrowsing-disable-download-protection");
				if (configLib.getIsHeadlessMode().equalsIgnoreCase("True")) {
					options.addArguments("--window-size=1920,1080");
					options.addArguments("--headless");
				}
				DesiredCapabilities capabilities = DesiredCapabilities.chrome();
				capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				driver = new ChromeDriver(capabilities);
				if (SetUpTearDownScript.createBrowserPool.equalsIgnoreCase("1"))
					chromePool.put(driver, 1);
			}
			else if (browser.equalsIgnoreCase(Browser.FIREFOX.toString())) {
				System.setProperty("webdriver.gecko.driver", configLib.getGeckoDriverPath());
				FirefoxProfile profile = new FirefoxProfile();
				FirefoxOptions options = new FirefoxOptions();
				if (configLib.getIncognitoModeConfig().equals("1"))
					profile.setPreference("browser.privatebrowsing.autostart", true);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.manager.showWhenStarting", false);
				profile.setPreference("browser.download.dir",
						configLib.getSharedFolderPath() + configLib.getDownloadedFilesPath());
				profile.setPreference("browser.download.useDownloadDir", true);
				profile.setPreference("browser.helperApps.neverAsk.openFile",
						"application/xml, text/xml, application/json, application/x-tar, application/x-compressed, application/x-zip-compressed,application/zip, multipart/x-zip, application/gnutar");
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
						"application/xml, text/xml, application/json, application/x-tar, application/x-compressed, application/x-zip-compressed,application/zip, multipart/x-zip, application/gnutar");
				profile.setPreference("browser.helperApps.alwaysAsk.force", false);
				profile.setAcceptUntrustedCertificates(true);
				profile.setAssumeUntrustedCertificateIssuer(false);
				options.setProfile(profile);
				driver = new FirefoxDriver(options);
				if (SetUpTearDownScript.createBrowserPool.equalsIgnoreCase("1"))
					firefoxPool.put(driver, 1);
			}
			else if (browser.equalsIgnoreCase(Browser.IE.toString())) {
				System.setProperty("webdriver.ie.driver", configLib.getIEDriverPath());
				DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
				if (configLib.getIncognitoModeConfig().equals("1")) {
					capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
					capabilities.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
				}
				capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
						true);
				capabilities.setCapability("requireWindowFocus", true);
				driver = new InternetExplorerDriver(capabilities);
				if (SetUpTearDownScript.createBrowserPool.equalsIgnoreCase("1"))
					iePool.put(driver, 1);
			}
		}
		catch (Exception e) {
			logger.error("Unable to open a new browser of type " + browser, e);
		}
		return driver;
	}

	/**
	 * To clean up abandoned browser objects
	 *
	 * @param browserPool
	 *            Browser pool to be cleaned
	 */
	private void cleanUpAbandonedBrowsers(Map<WebDriver, Integer> browserPool) {
		try {
			for (Map.Entry<WebDriver, Integer> browser : browserPool.entrySet()) {
				if (browser.getKey().toString().contains("(null)"))
					browserPool.remove(browser.getKey());
			}
		}
		catch (Exception e) {
			logger.error("Unable to clear the abandoned browser session", e);
		}
	}
}