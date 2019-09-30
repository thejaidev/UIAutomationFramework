package framework.utilities;

import framework.constants.IFrameworkConstants;
import framework.core.SetUpTearDownScript;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Actions Library has all the required meth ods to perform actions or
 * operations on the UI. Guideline: Please check this file for any reusable
 * Selenium wrapper methods.
 */
public class UIActionsLib implements IFrameworkConstants {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private PropertiesLib property;
	private Properties prpOR;
	private ConfigurationLib configLib = new ConfigurationLib();

	public UIActionsLib(Properties prpOR) {
		property = new PropertiesLib();
		this.prpOR = prpOR;
	}

	/**
	 * To get a web element by passing the element name from object repository
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository
	 * @return element Returns web element
	 */
	public WebElement getElement(WebDriver driver, String ele) throws Exception {
		WebElement element = null;
		element = driver.findElement(getLocator(ele));
		return element;
	}

	/**
	 * To get a list of web elements by passing the element name from object
	 * repository
	 *
	 * @param ele
	 *            Element name in the object repository
	 * @return element Returns a list of web element
	 */
	public List<WebElement> getElements(WebDriver driver, String ele) throws Exception {
		List<WebElement> element = null;
		element = driver.findElements(getLocator(ele));
		return element;
	}

	/**
	 * To click on an element - ele
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param clickUsingJS
	 *            Pass false to click using selenium API. Pass true to click using
	 *            javascript
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void click(WebDriver driver, String ele, boolean clickUsingJS) throws Exception {
		if (clickUsingJS) {
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			jsExecutor.executeScript("arguments[0].click();", getElement(driver, ele));
		}
		else {
			if (isDisplayed(driver, ele)) {
				WebElement element = getElement(driver, ele);
				element.click();
			}
		}
		logger.info("Clicked on " + ele);
	}

	/**
	 * To click on multiple elements - ele of matching identifier e.g. xpath
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @throws Exception
	 *             which will be caught in the application component library method
	 * @note This is used to clear all the search filters in the device search
	 */
	public void clickOnMultipleElements(WebDriver driver, String ele) throws Exception {
		if (isDisplayed(driver, ele)) {
			List<WebElement> list = getElements(driver, ele);
			for (WebElement webelement : list) {
				webelement.click();
				logger.info("Clicked on " + webelement.toString());
			}
		}
	}

	/**
	 * To double click on an element
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @throws Exception
	 */
	public void doubleClick(WebDriver driver, String ele) throws Exception {
		if (isDisplayed(driver, ele)) {
			WebElement element = getElement(driver, ele);
			Actions action = new Actions(driver);
			action.moveToElement(element).doubleClick().perform();
			logger.info("Double clicked on " + ele);
		}
	}

	/**
	 * To type - strText on element - ele
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param strText
	 *            Text to type on elment
	 * @param sendKeysWithJS
	 *            Pass false to send keys using selenium API. Pass true to send keys
	 *            using javascript
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void sendKeys(WebDriver driver, String ele, String strText, boolean sendKeysWithJS) throws Exception {
		if (strText != null) {
			if (sendKeysWithJS) {
				JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
				jsExecutor.executeScript("arguments[0].setAttribute('value', arguments[1])", getElement(driver, ele),
						strText);
			}
			else {
				if (isDisplayed(driver, ele)) {
					WebElement element = getElement(driver, ele);
					element.sendKeys(strText);
				}
			}
			logger.info("Set text - " + strText + " on " + ele);
		}
		else
			logger.warn("There is no text to enter");
	}

	/**
	 * To send a single key e.g. Keys.ENTER - key on an element - ele. This is an
	 * overloaded sendKeys function to hit enter button. Primarily used to set value
	 * in filter and search.
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param key
	 *            Key to send like Keys.ENTER ETC.
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void sendKeys(WebDriver driver, String ele, Keys key) throws Exception {
		if (isDisplayed(driver, ele)) {
			WebElement element = getElement(driver, ele);
			element.sendKeys(key);
			logger.info("Set key - " + key + " on " + ele);
		}
	}

	/**
	 * To upload a file which has path - path in element (upload button) - ele
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param path
	 *            Path of the file that should be uploaded
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void uploadFile(WebDriver driver, String ele, String path) throws Exception {
		waitUntilPageLoad(driver);
		getElement(driver, ele).sendKeys(path);
		logger.info("Uploaded file " + path + " in " + ele);
	}

	/**
	 * To type - text in the active screen
	 *
	 * @param text
	 *            Text to be typed in the active screen where the cursor is set
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void robotSendKeys(String text) throws Exception {
		Robot robot = new Robot();
		robot.delay(500);
		StringSelection stringSelection = new StringSelection(text);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, stringSelection);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		logger.info("Send keys:" + text + " to the active screen");
		robot.delay(1000);
	}

	/**
	 * To press enter on the active screen
	 *
	 * @throws Exception
	 */
	public void robotSendEnterKey() throws Exception {
		Robot robot = new Robot();
		robot.delay(2000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		logger.info("Sent ENTER to the active screen");
		robot.delay(2000);
	}

	/**
	 * To clear text present in a textbox - ele
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @throws Exception
	 *             Exception which will be caught in the application component
	 *             library method
	 */
	public void clear(WebDriver driver, String ele) throws Exception {
		if (isDisplayed(driver, ele)) {
			WebElement element = getElement(driver, ele);
			element.clear();
			logger.info("Cleared the text from " + ele);
		}
	}

	/**
	 * To check or uncheck a checkbox - ele.
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param check
	 *            To check pass true in check, to uncheck pass false in check
	 * @throws Exception
	 *             Exception which will be caught in the application component
	 *             library method
	 */
	public void selectCheckbox(WebDriver driver, String ele, boolean check) throws Exception {
		if (isDisplayed(driver, ele)) {
			if (check == true) {
				if (!isSelected(driver, ele)) {
					click(driver, ele, false);
					logger.info("Selected the checkbox " + ele);
				}
				else
					logger.info("Checkbox " + ele + " is already selected");
			}
			else if (check == false) {
				if (isSelected(driver, ele)) {
					click(driver, ele, false);
					logger.info("Unselected the checkbox " + ele);
				}
				else
					logger.info("Checkbox " + ele + " is already unselected");
			}
		}
	}

	/**
	 * To select a value - strText from Select tag dropdown - ele.
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param strText
	 *            Text that should be selected in the dropdown
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void selectDropdown(WebDriver driver, String ele, String strText) throws Exception {
		if (isDisplayed(driver, ele)) {
			Select sel = new Select(getElement(driver, ele));
			sel.selectByVisibleText(strText);
			logger.info("Selected " + strText + "in " + ele);
		}
	}

	/**
	 * To select a value - strText from list ele whose tag is li or [input and
	 * type="value"]
	 *
	 * @param ele
	 *            Element whose tag is li or [input and type="value"]
	 * @param strText
	 *            Text that should be selected in the Listbox
	 * @param isList
	 *            Pass true if ele is a li tag object. Pass false if ele is input
	 *            tag object
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void selectList(WebDriver driver, String ele, String strText, boolean isList) throws Exception {
		waitUntilPageLoad(driver);
		if (isList) {
			List<WebElement> options = getElements(driver, ele);
			if (options == null || options.isEmpty())
				throw new CustomException.UIElementNotFound(ele + " not found");
			for (WebElement option : options) {
				if (option.getText().equalsIgnoreCase(strText)) {
					option.click();
					logger.info("Selected " + strText + " in list of tag <li> " + ele);
					break;
				}
			}
		}
		else {
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			WebElement element = getElement(driver, ele);
			jsExecutor.executeScript("arguments[0].setAttribute('value', arguments[1])", element, strText);
			logger.info("Selected " + strText + " in list of tag <input> " + ele);
		}
	}

	/**
	 * This method is executed until the page loads and AJAX call is finished.
	 *
	 * @param driver
	 *            Browser driver object
	 * @throws Exception
	 *             which will be caught by the isDisplayed()
	 * @note This is called by isDisplayed() method.
	 */
	public void waitUntilPageLoad(WebDriver driver) throws Exception {
		int pageLoadCtr = 0, initialSleep = 100;
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		try {
			int waitTime = Integer.parseInt(configLib.getRetryAttempts());
			Thread.sleep(initialSleep * Integer.parseInt(configLib.getExecSpeed()));
			while (true) {
				if ((Boolean) jsExecutor.executeScript("return document.readyState").equals("complete")) {
					if ((Boolean) jsExecutor.executeScript("return window.jQuery != undefined && jQuery.active == 0"))
						break;
					Thread.sleep(2000);
					logger.info("Waiting for AJAX calls to complete...");
				}
				else {
					Thread.sleep(2000);
					logger.info("Loading the page...");
				}
				if (getElements(driver, "blankPage").size() == 0)
					throw new Exception("Retrieved blank page");
				else {
					pageLoadCtr++;
					if (pageLoadCtr >= waitTime / 2)
						break;
				}
			}
		}
		catch (NumberFormatException e) {
			logger.error("Please pass appropriate config values", e);
		}
		catch (Exception e) {
			throw new Exception("Unable to load the page");
		}
	}

	/**
	 * This verifies whether an element - ele is displayed, enabled and ready for
	 * operation. This method is called by click(), sendKeys(), getText() and every
	 * other method in Actions Library.
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return flag Returns true if the element is displayed. Returns false if the
	 *         element is not displayed
	 * @throws Exception
	 * @note This method calls waitForAjax() method.
	 */
	public boolean isDisplayed(WebDriver driver, String ele) throws Exception {
		Instant startTime;
		boolean flag = false;
		int waitTime = Integer.parseInt(configLib.getRetryAttempts());
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		try {
			startTime = Instant.now();
			for (int second = 1; second <= waitTime; second++) {
				try {
					if (isTimedOut(startTime))
						break;
					try {
						waitUntilPageLoad(driver);
					}
					catch (Exception e) {
						break;
					}
					if (getElement(driver, ele).isDisplayed()) {
						try {
							waitUntilPageLoad(driver);
						}
						catch (Exception e) {
							break;
						}
						wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(ele)));
						flag = true;
						logger.debug(ele + " is ready for action in the UI");
						// verticalScrollToElement(ele);
						break;
					}
					logger.warn("Waiting for " + ele + " to be available");
					Thread.sleep(1000);
				}
				catch (Exception e) {
					logger.warn(ele + " is not visible in the UI. Trying " + (second) + " time");
					Thread.sleep(1000);
				}
			}
			if (flag == false) {
				throw new Exception("Unable to find the element");
			}
		}
		catch (Exception e) {
			throw new Exception(ele + " with identfier " + prpOR.getProperty(ele).split(("#"), 2)[1]
					+ " is not displayed in the UI. Please check the object property or synchronize the script");
		}
		return flag;
	}

	/**
	 * To check whether timeout limit has reached
	 *
	 * @return timeOut True if timed out, false if not timed out
	 */
	public boolean isTimedOut(Instant startTime) {
		boolean timeOut = false;
		Instant endTime;
		Duration timeElapsed;
		try {
			endTime = Instant.now();
			timeElapsed = Duration.between(startTime, endTime);
			if (TimeUnit.MILLISECONDS.toMinutes(timeElapsed.toMillis()) >= Integer.parseInt(configLib.getTimeOut())) {
				logger.error("Timeout occurred");
				timeOut = true;
			}
		}
		catch (Exception e) {
			logger.error("Unable to check the timeout", e);
		}
		return timeOut;
	}

	/**
	 * To vertically scroll to an element - ele and bring it in scope.
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public void verticalScrollToElement(WebDriver driver, String ele) throws Exception {
		waitUntilPageLoad(driver);
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		WebElement element = getElement(driver, ele);
		jsExecutor.executeScript("arguments[0].scrollIntoView();", element);
		logger.info("Vertically scrolled to element " + ele);
	}

	/**
	 * To vertically scroll to an element - ele and bring it in scope.
	 *
	 * @param relativeEle
	 *            Relative element which is already in the view port
	 * @param ele
	 *            Element you want to scroll to
	 * @throws Exception
	 */
	public void verticalScrollToElement(WebDriver driver, String relativeEle, String ele) throws Exception {
		waitUntilPageLoad(driver);
		Actions action = new Actions(driver);
		Thread.sleep(1000);
		action.moveToElement(getElement(driver, relativeEle));
		Thread.sleep(1000);
		verticalScrollToElement(driver, ele);

	}

	/**
	 * This is to scroll - scroll to an element - scrollToEle with offset - xOffset,
	 * yOffset
	 *
	 * @param driver
	 *            Browser driver object
	 * @param scroll
	 *            Scroll bar element name - resultGrid.scrollHorizontal in the
	 *            object repository (OR)
	 * @param scrollToEle
	 *            Element name in the object repository (OR) to which the scroll bar
	 *            should be scrolled
	 * @param xOffset
	 *            To update the X axis offset
	 * @param yOffset
	 *            To update Y axis offset
	 * @throws Exception
	 *             which will be caught in the application component library method
	 * @note To scroll right give offset as (xOffset - positiveNumber, yOffset - 0)
	 *       To scroll left give offset as (xOffset - negative, yOffset - 0) To
	 *       scroll to bottom give offset as (xOffset - 0, yOffset - positiveNumber)
	 *       To scroll to top give offset as (xOffset - 0, yOffset - negative)
	 */
	public void scroll(WebDriver driver, String scroll, String scrollToEle, int xOffset, int yOffset) throws Exception {
		Actions action = new Actions(driver);
		WebElement scrollEle = null;
		int x = 0, y = 0;
		waitUntilPageLoad(driver);
		if (!isNotDisplayed(driver, scroll)) {
			click(driver, scroll, false);
			scrollEle = getElement(driver, scroll);
			if (xOffset == 0) {
				action.clickAndHold(scrollEle).moveByOffset(0, -1000).perform();
				logger.info("Scrolled to the top end");
			}
			else if (yOffset == 0) {
				action.clickAndHold(scrollEle).moveByOffset(-1000, 0).perform();
				logger.info("Scrolled to the left end");
			}
			while (true) {
				try {
					Thread.sleep(1500);
					if (getElement(driver, scrollToEle).isDisplayed()) {
						logger.info("Element " + scrollToEle + " is displayed after scrolling");
						break;
					}
					else
						throw new Exception("Trying to scroll to element " + scrollToEle);
				}
				catch (Exception e) {
					logger.info("Scrolling to find the element " + scrollToEle);
					x = scrollEle.getLocation().getX();
					y = scrollEle.getLocation().getY();
					action.moveToElement(scrollEle);
					action.clickAndHold(scrollEle);
					action.moveByOffset(xOffset, yOffset);
					action.release(scrollEle);
					action.perform();
					logger.info("Scrolled to x - " + scrollEle.getLocation().getX() + " and y - "
							+ scrollEle.getLocation().getY());

					if (yOffset == 0) {
						if (x == scrollEle.getLocation().getX()) {
							logger.info(
									"Reached end of horizontal scroll. Scroll in the opposite direction to find the element");
							break;
						}
					}
					else if (xOffset == 0) {
						if (y == scrollEle.getLocation().getY()) {
							logger.info(
									"Reached end of vertical scroll. Scroll in the opposite direction to find the element");
							break;
						}
					}

				}
			}
		}
		else
			logger.info(scroll + " - scroll bar is not displayed");
	}

	/**
	 * This is to check whether an element is not visible in the UI
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return isNotDisplayed Returns true is element is not visible, false if the
	 *         element is visible
	 */
	public boolean isNotDisplayed(WebDriver driver, String ele) throws Exception {
		boolean isNotDisplayed = false;
		waitUntilPageLoad(driver);
		try {
			if (!getElement(driver, ele).isDisplayed()) {
				isNotDisplayed = true;
				logger.info("Element " + ele + " is not displayed in the UI");
			}
			else
				logger.error("Element " + ele + " is displayed");
		}
		catch (NoSuchElementException e) {
			isNotDisplayed = true;
			logger.error("Element " + ele + " is not displayed in the UI. Please check the locator value");
		}
		catch (Exception e) {
			logger.error("Unable to find the element " + ele, e);
		}
		return isNotDisplayed;
	}

	/**
	 * This verifies whether an element is disabled
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return isDisabled Returns true is element is disabled, false if the element
	 *         is not disabled
	 * @throws Exception
	 */
	public boolean isDisabled(WebDriver driver, String ele) throws Exception {
		boolean isDisabled = false;
		waitUntilPageLoad(driver);
		try {
			WebElement element = getElement(driver, ele);
			if (!element.isEnabled()) {
				isDisabled = true;
				logger.info("Element " + ele + " is disabled");
			}
			else
				logger.error("Element " + ele + " is enabled");
		}
		catch (Exception e) {
			logger.error("Unable to check if the element " + ele + " is enabled or disabled", e);
		}
		return isDisabled;
	}

	/**
	 * This get the runtime attribute - attr of an object - ele
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param attr
	 *            Name of the attribute
	 * @return attributeValue Returns the runtime value of the attribute
	 * @throws Exception
	 */
	public String getAttribute(WebDriver driver, String ele, String attr) throws Exception {
		String attributeValue = null;
		waitUntilPageLoad(driver);
		try {
			WebElement element = getElement(driver, ele);
			if (element.isDisplayed()) {
				attributeValue = element.getAttribute(attr);
				if (attributeValue != null)
					logger.info("Element - " + ele + " " + attr + " value is " + attributeValue);
				else {
					logger.error(attr + " doesn't exist or is null for element " + ele);
				}
			}
			else
				throw new Exception(ele + " is not displayed");
		}
		catch (Exception e) {
			logger.error("Unable to retrieve the attribute " + attr + " of element " + ele, e);
		}
		return attributeValue;
	}

	/**
	 * To fetch inner attributes from an outer attribute. E.g. to fetch left (inner
	 * attribute) from the style (outer attribute)
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @param outerAttr
	 *            Outer attribute name of the element - ele
	 * @param innerAttr
	 *            Inner attribute name of the element - ele
	 * @return attrVal Value of the inner attribute - innerAttr
	 * @throws Exception
	 */
	public int getInnerAttribute(WebDriver driver, String ele, String outerAttr, String innerAttr) throws Exception {
		String outerAttrVal, attrWithPx;
		CommonUtilLib utilLib = new CommonUtilLib();
		int attrVal = 0;
		waitUntilPageLoad(driver);
		try {
			outerAttrVal = getAttribute(driver, ele, outerAttr);
			attrWithPx = outerAttrVal.substring(outerAttrVal.indexOf(innerAttr));
			attrWithPx = attrWithPx.substring(0, attrWithPx.indexOf(";"));
			attrVal = (int) utilLib.extractDigits(attrWithPx);
			logger.info(innerAttr + " attribute value is " + attrVal);
		}
		catch (Exception e) {
			logger.error("Unabled to fetch the inner attribute " + innerAttr + " from " + outerAttr, e);
		}
		return attrVal;
	}

	/**
	 * This method waits for a specified amount of time - time for an element - ele
	 * to disappear. This is mainly used for spinner or loaders to disappear
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR). E.g.
	 *            spinner.LoadingDiv, spinner.Dashboard, spinner.Loader1,
	 *            spinner.Loader2,
	 * @param time
	 *            Timeout seconds
	 * @return flag Returns true if the element has disappeared. Returns false if
	 *         the element is still present
	 * @throws Exception
	 */
	public boolean waitToDisappear(WebDriver driver, String ele, int time) throws Exception {
		Boolean flag = false;
		logger.info("Waiting for spinner " + ele + " to disappear");
		waitUntilPageLoad(driver);
		try {
			WebDriverWait wait = new WebDriverWait(driver, time);
			flag = wait.until(ExpectedConditions.invisibilityOfElementLocated(getLocator(ele)));
			logger.info(ele + " has disppeared");
		}
		catch (Exception e) {
			logger.error("Spinner" + ele + " has not disappeared", e);
		}
		waitUntilPageLoad(driver);
		return flag;
	}

	/**
	 * To get text from an element - ele
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return text Text retrieved from element - ele
	 * @throws Exception
	 *             which will be caught in the application component library method
	 */
	public String getText(WebDriver driver, String ele, boolean fromTextBox) throws Exception {
		WebElement element;
		String text = null, errText = "internal error";
		waitUntilPageLoad(driver);
		element = getElement(driver, ele);
		if (element.isDisplayed()) {
			if (fromTextBox)
				text = element.getAttribute("value");
			else {
				text = element.getText();
				if (text.contains(errText))
					throw new Exception("An internal error has occurred in the application");
			}
			logger.info("Got text: " + text + " from " + ele);
		}
		else {
			throw new Exception("Unable to get text from " + ele
					+ ". Check if the object is visible and the locator is accurate or not");
		}
		return text.trim();
	}

	/**
	 * This is used to get total number of element - ele which matches the criteria.
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return count Number of element which has the same property as of element -
	 *         ele
	 * @throws Exception
	 */
	public int getElementsCount(WebDriver driver, String ele) throws Exception {
		int count = 0;
		List<WebElement> elments;
		waitUntilPageLoad(driver);
		try {
			elments = getElements(driver, ele);
			count = elments.size();
			logger.info("Found " + count + " elements which matches the locator of " + ele);
		}
		catch (Exception e) {
			logger.error("Unable to find number of elements", e);
		}
		return count;
	}

	/**
	 * This is used to get the last element in case if multiple xpath matches are
	 * found
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return lastEle The last element which matches the locator value
	 * @throws Exception
	 */
	public WebElement getLastElement(WebDriver driver, String ele) throws Exception {
		List<WebElement> elments;
		WebElement lastEle = null;
		waitUntilPageLoad(driver);
		try {
			elments = getElements(driver, ele);
			lastEle = elments.get(elments.size() - 1);
			logger.info("Located the last element of " + ele);
		}
		catch (Exception e) {
			logger.error("Unable to get the last element", e);
		}
		return lastEle;
	}

	/**
	 * To check whether an element - ele is in view port. This is currently not used
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return flag Returns true is element is visible in the view port. Returns
	 *         false if the element is not in the view port
	 * @throws Exception
	 * @note Need to update the logic
	 */
	public boolean isVisibleInViewport(WebDriver driver, String ele) throws Exception {
		boolean flag = false;
		waitUntilPageLoad(driver);
		try {
			WebElement element = getElement(driver, ele);
			flag = (Boolean) ((JavascriptExecutor) driver).executeScript(
					"var elem = arguments[0],                 " + "  box = elem.getBoundingClientRect(),    "
							+ "  cx = box.left + box.width / 2,         " + "  cy = box.top + box.height / 2,         "
							+ "  e = document.elementFromPoint(cx, cy); " + "for (; e; e = e.parentElement) {         "
							+ "  if (e === elem)                        " + "    return true;                         "
							+ "}                                        " + "return false;                            ",
					element);
			if (flag)
				logger.info("Element " + ele + " is in the view port");
			else
				logger.info("Element " + ele + " is not in the view port");
		}
		catch (Exception e) {
			logger.error("Unable to find if the element " + ele + " is in the view port or not!", e);
		}
		return flag;
	}

	/**
	 * To change the browser zoom settings. This is currently not used
	 *
	 * @param zoomLevel
	 *            Set the zoom level. E.g. 100 or 80 || 1 or .8
	 * @note Need to update the logic
	 */
	public void changeBrowserZoom(WebDriver driver, String strBrowser, int zoomLevel) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		if (strBrowser.equalsIgnoreCase(Browser.FIREFOX.toString())
				|| strBrowser.equalsIgnoreCase(Browser.CHROME.toString()))
			jsExecutor.executeScript("document.body.style.zoom='" + zoomLevel + "'");
		else if (strBrowser.equalsIgnoreCase(Browser.IE.toString()))
			jsExecutor.executeScript("document.body.style.MozTransform = 'scale(2)';");
	}

	/**
	 * Refresh the page
	 *
	 * @param driver
	 *            Browser driver object
	 * @throws Exception
	 */
	public void refreshPage(WebDriver driver) throws Exception {
		logger.info("Refreshing the page...");
		waitUntilPageLoad(driver);
		try {

			driver.navigate().refresh();
			Thread.sleep(3000);
		}
		catch (Exception e) {
			logger.error("Unable to refresh the page", e);
		}
		waitUntilPageLoad(driver);
	}

	/**
	 * To close browser
	 *
	 * @param closeAll
	 *            Pass true to close all or pass false to close only the current
	 *            browser tab
	 */
	public void closeBrowser(WebDriver driver, String strBrowser, boolean closeAll) {
		try {
			if (driver != null) {
				if (SetUpTearDownScript.createBrowserPool.equalsIgnoreCase("1")) {
					if (strBrowser == null || strBrowser.equals(Browser.CHROME.toString())) {
						if (chromePool.keySet().contains(driver))
							chromePool.remove(driver);
					}
					else if (strBrowser.equals(Browser.FIREFOX.toString())) {
						if (firefoxPool.keySet().contains(driver))
							firefoxPool.remove(driver);
					}
					else if (strBrowser.equals(Browser.IE.toString())) {
						if (iePool.keySet().contains(driver))
							iePool.remove(driver);
					}
				}
				if (closeAll) {
					driver.quit();
					logger.info("Closed all browser windows");
				}
				else {
					driver.close();
					logger.info("Closed currently active browser window");
				}
			}
			else
				logger.debug("There is no browser to close");
		}
		catch (Exception e) {
			logger.error("Unable to close the browser", e);
		}
	}

	/**
	 * To close all the browser driver pools
	 *
	 * @param browserPool
	 *            Map containing web drivers
	 */
	public void closeBrowser(Map<WebDriver, Integer> browserPool) {
		for (Map.Entry<WebDriver, Integer> browser : browserPool.entrySet()) {
			try {
				browser.getKey().close();
			}
			catch (Exception e) {
				logger.debug("There is no browser driver to close");
			}
		}
	}

	/**
	 * To check whether an element - radio / option button, checkbox etc is selected
	 * or not
	 *
	 * @param ele
	 *            Element to be verified
	 * @return flag Returns true if the element is selected. Returns false if the
	 *         element is not selected
	 */
	public boolean isSelected(WebDriver driver, String ele) throws Exception {
		boolean flag = false;
		if (isDisplayed(driver, ele)) {
			WebElement element = getElement(driver, ele);
			if (element.isSelected()) {
				flag = true;
				logger.info("Element " + ele + " is selected");
			}
			else
				logger.error("Element " + ele + " is not selected");
		}
		else {
			throw new Exception("Unable to click on " + ele);
		}
		return flag;
	}

	/**
	 * To get title / tooltip from an element
	 *
	 * @param ele
	 *            Element whose title / tooltip should be retrieved
	 * @return title Returns the title / tooltip of the element - ele
	 */
	public String getTitleToolTip(WebDriver driver, String ele) throws Exception {
		WebElement element;
		String title = null;
		waitUntilPageLoad(driver);
		element = getElement(driver, ele);
		if (element.isDisplayed()) {
			title = element.getAttribute("title");
			logger.info("Got title / tooltip: " + title + " from " + ele);
		}
		else {
			throw new Exception("Unable to get title / tooltip from " + ele
					+ ". Check if the object is visible and the locator is accurate or not.");
		}
		return title;
	}

	/**
	 * This is get the unique element when there are more than one element matching
	 * the object locator criteria
	 *
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @return found Returns true if the object is found else false
	 * @note Call this method only when there is more than 1 matching object locator
	 *       criteria
	 */
	public boolean findUniqueElement(WebDriver driver, String ele) {
		List<WebElement> elments;
		boolean found = false;
		int eleCount = -1;
		try {
			eleCount = getElementsCount(driver, ele);
			if (eleCount > 1) {
				for (int i = 0; i < eleCount; i++) {
					elments = getElements(driver, ele);
					if (elments.get(i).isDisplayed()) {
						if (prpOR.getProperty(ele).contains(".//")) {
							property.updateProperty(prpOR, ele, ".//", "(//");
							property.appendProperty(prpOR, ele, ")");
						}
						property.appendProperty(prpOR, ele, "[" + i + 1 + "]");
						logger.info("Found a unique element which is displayed in the UI. Updated locator value is "
								+ prpOR.getProperty(ele));
						found = true;
						break;
					}
				}
			}
		}
		catch (Exception e) {
			logger.info("Unable to find a unique element from the matching elements", e);
			e.printStackTrace();
		}
		return found;
	}

	/**
	 * To get the current URL
	 *
	 * @param driver
	 *            Browser driver object
	 * @return url Returns the current page URL
	 * @throws Exception
	 */
	public String getCurrentURL(WebDriver driver) throws Exception {
		String url;
		Thread.sleep(3000);
		waitUntilPageLoad(driver);
		url = driver.getCurrentUrl();
		if (url == null)
			logger.error("Currently URL is null. Browser is not directed to any URL");
		else
			logger.info("Browser has navigated to " + url);
		return url;
	}

	/**
	 * To get the current window handle
	 *
	 * @return currentWindowHandle Returns the current window handle
	 * @throws Exception
	 */
	public String getWindowHandle(WebDriver driver) throws Exception {
		String currentWindowHandle = driver.getWindowHandle();
		logger.info("Current window handle is " + currentWindowHandle);
		return currentWindowHandle;
	}

	/**
	 * To get all the window handles
	 *
	 * @param driver
	 *            Browser driver object
	 * @return windowHandles An array list of window handles
	 * @throws Exception
	 */
	public ArrayList<String> getWindowHandles(WebDriver driver) throws Exception {
		ArrayList<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
		for (String handle : windowHandles)
			logger.info("Window handles are " + handle);
		return windowHandles;
	}

	/**
	 * To switch windows using the window handle
	 *
	 * @param driver
	 *            Browser driver object
	 * @param windowHandle
	 *            Window handle to be switched to
	 * @throws Exception
	 */
	public void switchWindow(WebDriver driver, String windowHandle) throws Exception {
		if (windowHandle == null)
			logger.error("Window handle is null. Unable to switch to browser window");
		else {
			driver.switchTo().window(windowHandle);
			logger.info("Switched to window " + windowHandle);
		}
	}

	/**
	 * To switch to an iFrame
	 *
	 * @param driver
	 *            Browser driver object
	 * @param eleFrame
	 *            Element name in the object repository (OR)
	 * @throws Exception
	 */
	public void switchFrame(WebDriver driver, String eleFrame) throws Exception {
		driver.switchTo().frame(getElement(driver, eleFrame));
		logger.info("Switched to iFrame " + eleFrame);
	}

	/**
	 * To switch to default content after performing switchFrame
	 *
	 * @throws Exception
	 */
	public void switchToDefaultContent(WebDriver driver) throws Exception {
		driver.switchTo().defaultContent();
		logger.info("Switched to default content");
	}

	/**
	 * To move to element - ele
	 *
	 * @param driver
	 *            Browser driver object
	 * @param ele
	 *            Element name in the object repository (OR)
	 * @throws Exception
	 */
	public void moveToElement(WebDriver driver, String ele) throws Exception {
		try {
			Actions action = new Actions(driver);
			action.moveToElement(getElement(driver, ele)).perform();
			Thread.sleep(2000);
			logger.info("Moved the cursor to " + ele);
		}
		catch (Exception e) {
			logger.error("Unable to move the cursor to " + ele, e);
		}
	}

	/**
	 * To retrieve the objects from the object repository (OR) based on the locator
	 * and the locator value
	 *
	 * @param strElement
	 *            Element name in the OR
	 * @return By.<locator-type>(locatorValue) Here the loator type can be id, name,
	 *         classname, tagname, linktext, partiallinktext, cssselector, or xpath
	 */

	private By getLocator(String strElement) {
		try {
			String locator = prpOR.getProperty(strElement);
			if (locator == null)
				throw new Exception(
						"Object doesn't exist. Please check the object: " + strElement + " in the object repository");
			String locatorType = locator.split(("#"), 2)[0];
			String locatorValue = locator.split(("#"), 2)[1];

			if (locatorType.toLowerCase().equals("id"))
				return By.id(locatorValue);
			else if (locatorType.toLowerCase().equals("name"))
				return By.name(locatorValue);
			else if ((locatorType.toLowerCase().equals("classname")) || (locatorType.toLowerCase().equals("class")))
				return By.className(locatorValue);
			else if ((locatorType.toLowerCase().equals("tagname")) || (locatorType.toLowerCase().equals("tag")))
				return By.tagName(locatorValue);
			else if ((locatorType.toLowerCase().equals("linktext")) || (locatorType.toLowerCase().equals("link")))
				return By.linkText(locatorValue);
			else if ((locatorType.toLowerCase().equals("partiallinktext"))
					|| (locatorType.toLowerCase().equals("partiallink")))
				return By.partialLinkText(locatorValue);
			else if ((locatorType.toLowerCase().equals("cssselector")) || (locatorType.toLowerCase().equals("css")))
				return By.cssSelector(locatorValue);
			else if (locatorType.toLowerCase().equals("xpath"))
				return By.xpath(locatorValue);
			else
				throw new Exception("Unknown locator type '" + locatorType + "'");
		}
		catch (Exception e) {
			logger.error("Unable to fetch the element " + strElement + " from the object repository", e);
			return null;
		}
	}

	/**
	 * To get the browser console error
	 *
	 * @param driver
	 *            Browser driver object
	 * @return consoleError Returns NIL if there is no error
	 */
	public String getConsoleError(WebDriver driver) {
		int i = 0;
		StringBuilder consoleError = new StringBuilder("");
		LogEntries logs = driver.manage().logs().get("browser");
		for (LogEntry log : logs) {
			consoleError.append(log + "\n");
			if ((i++) > 1)
				break;
		}
		if (consoleError.toString().equals(""))
			consoleError.append("NIL");
		else
			logger.warn("Console Message: " + consoleError.toString());
		return consoleError.toString();
	}
}