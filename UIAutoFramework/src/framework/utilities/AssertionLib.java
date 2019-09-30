/* AssertionLib Library contains the most commonly used methods for validation
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import framework.libraryinit.PageFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.it.modular.hamcrest.date.DateMatchers;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * This class contains all the methods / actions to perform assertion
 */
public class AssertionLib {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private PageFactory bs;

	public AssertionLib() {

	}

	public AssertionLib(PageFactory bs) {
		this.bs = bs;
	}

	/**
	 * To validate whether 2 strings are equal
	 *
	 * @param actual
	 *            Actual value that should be compared
	 * @param expected
	 *            Expected value that should be compared
	 * @param context
	 *            The content which is being validated. E.g. Release name
	 */
	public boolean assertEquals(String actual, String expected, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertEquals method execution");
		try {
			if (actual == null && expected == null)
				bs.report.pass("Both actual and expected are null");
			else if (actual == null && expected != null)
				throw new Exception("Actual value is null and expected value is " + expected);
			else if (actual != null && expected == null)
				throw new Exception("Expected value is null and actual value is " + actual);
			else if (actual.equals(expected)) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass("Successfully validated the " + context + ": " + expected);
				validationSuccessful = true;
			}
			else
				throw new Exception("Actual and Expected values are different");
		}
		catch (Exception e) {
			bs.report.fail("Actual " + context + " is " + actual + ". Expected " + context + " is " + expected, e,
					false);
		}
		finally {
			logger.info("assertEquals execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether 2 string[] arrays are equal
	 *
	 * @param actual
	 *            Actual array that should be compared
	 * @param expected
	 *            Expected array value that should be compared
	 * @param context
	 *            The content which is being validated. E.g. Release name
	 */
	public boolean assertEquals(String[] actual, String[] expected, String context, boolean validateOrder) {
		boolean validationSuccessful = false;
		logger.info("Started assertEquals method execution");
		try {
			if (actual == null && expected == null)
				bs.report.pass("Both actual and expected values are null");
			else if (actual == null && expected != null)
				throw new Exception("Actual and Expected values are different");
			else if (actual != null && expected == null)
				throw new Exception("Actual and Expected values are different");
			else {
				if (validateOrder) {
					if (Arrays.equals(actual, expected)) {
						logger.info("Assert " + context + ": Passed");
						bs.report.pass("Successfully validated the " + context + ": " + Arrays.toString(expected));
						validationSuccessful = true;
					}
					else
						throw new Exception("Actual and Expected values are different");
				}
				else {
					CommonUtilLib utilLib = new CommonUtilLib();
					actual = utilLib.trimStringsInArray(actual);
					expected = utilLib.trimStringsInArray(expected);
					ArrayList<String> actList = new ArrayList<String>(Arrays.asList(actual));
					ArrayList<String> expList = new ArrayList<String>(Arrays.asList(expected));
					if (actList.equals(expList)) {
						logger.info("Assert " + context + ": Passed");
						bs.report.pass("Successfully validated the " + context + ": " + Arrays.toString(expected));
						validationSuccessful = true;
					}
					else
						throw new Exception("Actual and Expected values are different");
				}
			}

		}
		catch (Exception e) {
			bs.report.fail("Actual " + context + " is " + Arrays.toString(actual) + ". Expected " + context + " is "
					+ Arrays.toString(expected), e, false);
		}
		finally {
			logger.info("assertEquals execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether 2 integers are equal or not
	 *
	 * @param actual
	 *            actual int which should be compared
	 * @param expected
	 *            expected int which should be compared
	 * @param context
	 *            The content which is being validated. E.g. Build Number
	 * @return True if its equal else false
	 */
	public boolean assertEquals(int actual, int expected, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertEquals method execution");
		try {
			if (actual == expected) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass("Successfully validated the " + context + ": " + expected);
				validationSuccessful = true;
			}
			else
				throw new Exception("Actual and Expected values are different");
		}
		catch (Exception e) {
			bs.report.fail("Actual " + context + " is " + actual + ". Expected " + context + " is " + expected, e,
					false);
		}
		finally {
			logger.info("assertEquals execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To compare table data store in List<LinkedHashMap<String, String>> format
	 *
	 * @param actTData
	 *            Actual table data
	 * @param expTData
	 *            Expected table data
	 * @param colsToIgnore
	 *            Columns to be ignored in validation
	 */
	public boolean assertEquals(List<LinkedHashMap<String, String>> actTData,
			List<LinkedHashMap<String, String>> expTData, String[] colsToIgnore, String context) {
		logger.info("Started assertEquals method execution");
		boolean status = true;
		try {
			int actRows = actTData.size();
			int expRows = expTData.size();
			if (actRows == expRows) {
				int actCols = expTData.get(0).keySet().size();
				int expCols = actTData.get(0).keySet().size();
				if (expCols == actCols) {
					Set<String> keySet = expTData.get(0).keySet();
					for (int i = 0; i < expTData.size(); i++) {
						for (String key : keySet) {
							if (!ArrayUtils.contains(colsToIgnore, key)) {
								String actVal = actTData.get(i).get(key);
								String expVal = expTData.get(i).get(key);
								if (!expVal.equalsIgnoreCase(actVal)) {
									status = false;
									throw new Exception(
											"Actual value " + actVal + " is not equal to expected value " + expVal);
								}
							}
						}
					}
				}
				else {
					status = false;
					new Exception("Expected number of columns are " + expTData.get(0).keySet().size()
							+ " and actual number of columns are " + actTData.get(0).keySet().size());
				}
			}
			else {
				status = false;
				throw new Exception(
						"Expected number of rows are " + expRows + " and actual number of rows are " + actRows);
			}
			if (status)
				bs.report.pass("Acutal and expected table contents are same for " + context + ". "
						+ Arrays.toString(colsToIgnore) + " columns were ignored during comparision");
		}
		catch (Exception e) {
			bs.report.fail("Table contents doesn't match", e, false);
		}
		finally {
			logger.info("assertEquals execution completed");
		}
		return status;
	}

	/**
	 * To validate whether 2 strings are NOT equal
	 *
	 * @param actual
	 *            Actual value that should be compared
	 * @param expected
	 *            Expected value that should be compared
	 * @param context
	 *            The content which is being validated. E.g. Release name
	 */
	public boolean assertNotEquals(String actual, String expected, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertNotEquals method execution");
		try {
			if (!actual.equals(expected)) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass("Actual " + context + " is " + actual + ". Expected " + context + " is " + expected);
				validationSuccessful = true;
			}
			else
				throw new Exception("Actual and Expected values are same");
		}
		catch (Exception e) {
			bs.report.fail("Both actual and expected " + context + " is " + expected, e, false);
		}
		finally {
			logger.info("assertNotEquals execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether 2 integers are not equal
	 *
	 * @param actual
	 *            actual int which should be compared
	 * @param expected
	 *            expected int which should be compared
	 * @param context
	 *            The content which is being validated. E.g. Build Number
	 * @return True if its not equal else false
	 */
	public boolean assertNotEquals(int actual, int expected, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertNotEquals method execution");
		try {
			if (actual != expected) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass("Actual " + context + " is " + actual + ". Expected " + context + " is " + expected);
				validationSuccessful = true;
			}
			else
				throw new Exception("Actual and Expected values are same");
		}
		catch (Exception e) {
			bs.report.fail("Both actual and expected " + context + " is " + expected, e, false);
		}
		finally {
			logger.info("assertNotEquals execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether completeString contains subString
	 *
	 * @param completeString
	 *            Complete string which should be searched
	 * @param subString
	 *            String which should be searched for
	 * @param context
	 *            The content which is being validated. E.g. Release name
	 * @return validationSuccessful Returns true if the validation is successful.
	 *         Returns false if validation is failed
	 */
	public boolean assertContains(String completeString, String subString, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertContains method execution");
		try {
			if (completeString == null && subString == null)
				bs.report.pass("Both complete string and sub string are null");
			else if (completeString == null && subString != null)
				throw new Exception("Complete string is null and sub string is " + subString);
			else if (completeString != null && subString == null)
				throw new Exception("Sub string is null and Complete string is " + completeString);
			else if (completeString.contains(subString)) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass(
						"Successfully validated the " + context + " - " + completeString + " contains " + subString);
				validationSuccessful = true;
			}
			else
				throw new Exception(completeString + " doesn't contain " + subString);
		}
		catch (Exception e) {
			bs.report.fail("Complete string is " + completeString + " and sub string is " + subString, e, false);
		}
		finally {
			logger.info("assertContains execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether completeString does NOT contain subString
	 *
	 * @param completeString
	 *            Complete string which should be searched
	 * @param subString
	 *            String which should be searched for
	 * @param context
	 *            The content which is being validated. E.g. Release name
	 * @return validationSuccessful Returns true if the validation is successful.
	 *         Returns false if validation is failed
	 */
	public boolean assertNotContains(String completeString, String subString, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertNotEquals method execution");
		try {
			if (!completeString.contains(subString)) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass(completeString + " doesn't contain " + subString);
				validationSuccessful = true;
			}
			else
				throw new Exception(completeString + " contains " + subString);
		}
		catch (Exception e) {
			bs.report.fail("Complete string is " + completeString + " and sub string is " + subString, e, false);
		}
		finally {
			logger.info("assertNotEquals execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether the result is true
	 *
	 * @param condition
	 *            That should be validated
	 * @param context
	 *            The content which is being validated. E.g. Is present
	 */
	public boolean assertTrue(boolean condition, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertTrue method execution");
		try {
			if (condition == true) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass(context + " - True");
				validationSuccessful = true;
			}
			else
				throw new Exception("Condition is false");
		}
		catch (Exception e) {
			bs.report.fail(context + " - False", e, false);
		}
		finally {
			logger.info("assertTrue execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether the result is false
	 *
	 * @param condition
	 *            That should be validated
	 * @param context
	 *            The content which is being validated. E.g. Is present
	 */
	public boolean assertFalse(boolean condition, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertTrue method execution");
		try {
			if (condition == false) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass(context + " - False");
				validationSuccessful = true;
			}
			else
				throw new Exception("Condition is true");
		}
		catch (Exception e) {
			bs.report.fail(context + " - True", e, false);
		}
		finally {
			logger.info("assertTrue execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether an object reference is null
	 *
	 * @param obj
	 *            Object reference that should be validated
	 * @param context
	 *            Is what the object reference is pointing to. E.g. testDataFile
	 */
	public boolean assertNull(Object obj, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertNull method execution");
		try {
			if (obj == null) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass(context + " is null");
				validationSuccessful = true;
			}
			else
				throw new Exception(context + " is not null");
		}
		catch (Exception e) {
			bs.report.fail(context + " is not null", e, false);
		}
		finally {
			logger.info("assertNull execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * To validate whether an object is NOT null
	 *
	 * @param obj
	 *            Object reference that should be validated
	 * @param context
	 *            Is what the object reference is pointing to. E.g. testDataFile
	 */
	public boolean assertNotNull(Object obj, String context) {
		boolean validationSuccessful = false;
		logger.info("Started assertNotNull method execution");
		try {
			if (obj != null) {
				logger.info("Assert " + context + ": Passed");
				bs.report.pass(context + " is not null");
				validationSuccessful = true;
			}
			else
				throw new Exception(context + " is null");
		}
		catch (Exception e) {
			bs.report.fail(context + " is null", e, false);
		}
		finally {
			logger.info("assertNotNull execution completed");
		}
		return validationSuccessful;
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertEquals(Object actual, Object expected) {
		try {
			assertThat(actual, Matchers.equalTo(expected));
			bs.report.pass("Data fetched from Response/Database " + actual + " is equal to expected value " + expected);
		}
		catch (AssertionError e) {
			bs.report.fail(
					"Data fetched from Response/Database " + actual + " is NOT equal to expected value " + expected, e,
					true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertEqualsIgnoreCase(String actual, String expected) {
		try {
			assertThat(actual, equalToIgnoringCase(expected));
			bs.report.pass("Actual value \"" + actual + "\" is equal to expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value \"" + actual + "\" is NOT equal to expected value \"" + expected, e, true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertContains(String actual, String expected) {
		try {
			assertThat(actual, containsString(expected));
			bs.report.pass("Actual value \"" + actual + "\"contains expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value \"" + actual + "\"Does NOT Contain expected value \"" + expected, e, true);
		}
	}

	/**
	 * Overloaded function for assertContains
	 *
	 * @param actual
	 * @param expected
	 */
	public void assertContains(List<String> actual, String expected) {
		try {
			actual.contains(expected);
			bs.report.pass("Actual value \"" + actual + "\"contains expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value \"" + actual + "\"contains expected value \"" + expected, e, true);
		}
	}

	/**
	 * @param actual
	 */
	public void assertNotEmpty(Object actual) {
		try {
			assertEquals(actual, not(Matchers.isEmptyString()));
			bs.report.pass("Actual value \"" + actual + "\" is empty");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value expected to be empty--> ", e, true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertGreaterThan(int actual, int expected) {
		try {
			assertThat(actual, greaterThan(expected));
			bs.report.pass("Actual value \"" + actual + "\" is greater than expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value is not greater than expected--> ", e, true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertLessThan(int actual, int expected) {
		try {
			assertThat(actual, lessThan(expected));
			bs.report.pass("Actual value \"" + actual + "\" is less than expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value is not less than expected--> ", e, true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertGreaterThanOrEqualTo(int actual, int expected) {
		try {
			assertThat(actual, greaterThanOrEqualTo(expected));
			bs.report.pass(
					"Actual value \"" + actual + "\" is greater than or equal to expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value is not greater than or equal to expected--> ", e, true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertLessThanOrEqualTo(int actual, int expected) {
		try {
			assertThat(actual, lessThanOrEqualTo(expected));
			bs.report.pass(
					"Actual value \"" + actual + "\" is less than or equal to expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value is not less than or equal to expected--> ", e, true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertIsListContainValue(List<Object> actual, Object expected) {
		try {
			assertThat(expected, isIn(actual));
			bs.report.pass("Actual list of values \"" + actual + "\"contain expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value is less than or equal to expected--> ", e, true);
		}
	}

	/**
	 * @param afterDate
	 * @param beforeDate
	 */
	public void assertDateIsAfter(Date afterDate, Date beforeDate) {
		try {
			assertThat(afterDate, DateMatchers.after(beforeDate));
			bs.report.pass("Actual date \"" + afterDate + "\" is after the expected date \"" + beforeDate + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual date \"" + afterDate + "\" is not after the expected date \"" + beforeDate, e, true);
		}
	}

	/**
	 * @param actual
	 * @param expected
	 */
	public void assertnotEquals(Object actual, Object expected) {
		try {
			assertThat(actual, not(Matchers.equalTo(expected)));
			bs.report.pass("Actual value \"" + actual + "\" is not equal to expected value \"" + expected + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value is equal to expected--> ", e, true);
		}
	}

	/**
	 * @param text
	 * @param pattern
	 * @return
	 */
	public boolean isStringMatch(String text, String pattern) {
		return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
	}

	/**
	 * Asserts 2 Expected Values
	 *
	 * @param actual
	 * @param expected1
	 * @param expected2
	 */
	public void assertTwoExtractedValues(String actual, String expected1, String expected2) {
		try {
			assert (actual.equalsIgnoreCase(expected1) || actual.equalsIgnoreCase(expected2));
			bs.report.pass(
					"Actual value " + actual + " is not equal to expected value " + expected1 + " or " + expected2);
		}
		catch (AssertionError e) {
			bs.report.fail("Actual value is equal to expected--> ", e, true);
		}
	}

	/**
	 * @param actualData
	 * @param expectedDate
	 *            Assert Data in Same Hour
	 */
	public void assertDateSameHour(Date actualData, Date expectedDate) {
		try {
			assertThat(actualData, DateMatchers.sameHour(expectedDate));
			bs.report.pass("Actual date \"" + actualData + "\" is after the expected date \"" + expectedDate + "\"");
		}
		catch (AssertionError e) {
			bs.report.fail("Actual date \"" + actualData + "\" is not after the expected date \"" + expectedDate, e,
					true);
		}
	}
}