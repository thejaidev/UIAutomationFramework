/* CCustomException contains the mostly throws exceptions
 *
 */
package framework.utilities;

@SuppressWarnings("serial")
public class CustomException {

	/**
	 * To create an exception of type element not found in the UI
	 *
	 * @param message
	 *            Message to be passed
	 */
	public static class UIElementNotFound extends Exception {
		public UIElementNotFound(String message) {
			super(message);
		}
	}

	/**
	 * To create an exception of type DB validation failed
	 *
	 * @param message
	 *            Message to be passed
	 */
	public static class DBValidationFailed extends Exception {
		public DBValidationFailed(String message) {
			super(message);
		}
	}
}
