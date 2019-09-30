package testsuite;

import framework.core.SetUpTearDownScript;
import org.testng.annotations.Test;

/**
 * Sample test script template
 */
public class Login extends SetUpTearDownScript {

	/**
	 * This is the actual script method. This is where you code. Call all the
	 * required methods in the try block
	 */
	@Test(priority = 1)
	public void login() {
		try {
			// Call the setUp() before you perform any action in the script
			setUp(true);

			// Call all your methods here using bs.*

		}
		catch (Exception e) {
			bs.report.fail("An exception occurred at the script level.", e, true);
		}
	}

	/**
	 * Multiple @Test methods can be used in the same script so that all the
	 * relatable scenarios can grouped together in the same file. Different
	 * iterations will be considered, even though they are in the same file In this
	 * case, login() will be the first iteration and loginLogout() will be the
	 * second
	 *
	 * @note In testng.xml give the test iteration as 1 itself in case if there are
	 *       multiple
	 * @Test methods. E.g. If invocation count is set to 2 for this script then both
	 *       login and loginLogout will run twice Itr 1 - login with iteration 1
	 *       data set. Itr 2 - login with iteration 2 data set. Itr 3 - loginLogout
	 *       with iteration 3 data set. Itr 4 - loginLogout with iteration 4 data
	 *       set
	 */
	@Test(priority = 2)
	public void loginLogout() {
		try {
			// Call the setUp() before you perform any action in the script
			setUp(true);

			// Call all your methods in this try block using bs.*

			
		}
		catch (Exception e) {
			bs.report.fail("An exception occurred at the script level.", e, true);
		}
	}
}
