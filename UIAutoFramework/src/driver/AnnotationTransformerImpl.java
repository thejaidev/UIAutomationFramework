package driver;

import framework.utilities.ConfigurationLib;
import framework.utilities.ExcelLib;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.ClassHelper;
import org.testng.internal.annotations.AnnotationHelper;
import org.testng.internal.annotations.JDK15AnnotationFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

public class AnnotationTransformerImpl implements IAnnotationTransformer {

	/**
	 * To set the invocation count of each script
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		try {
			String className, scriptName;
			int methodCount = 0, invocationCount = 0;

			ConfigurationLib configLib = new ConfigurationLib();

			className = testMethod.toString().replace("public void ", "");
			className = className.split(" throws")[0];
			className = className.substring(0, className.lastIndexOf("."));
			scriptName = className.substring(className.lastIndexOf(".") + 1, className.length());

			Set<Method> allMethods = ClassHelper.getAvailableMethods(Class.forName(className));
			for (Method eachMethod : allMethods) {
				ITestAnnotation value = AnnotationHelper.findTest(new JDK15AnnotationFinder(new DummyTransformer()),
						eachMethod);
				if (value != null) {
					methodCount++;
				}
			}

			ExcelLib testData = new ExcelLib();
			testData.connectToExcel(configLib.getTestDataPath());
			if (methodCount != 0)
				invocationCount = (int) Math.ceil((float) testData.getNumberOfOccurences(scriptName) / methodCount);

			annotation.setInvocationCount(invocationCount);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class DummyTransformer implements IAnnotationTransformer {

		@SuppressWarnings("rawtypes")
		@Override
		public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor,
				Method testMethod) {
		}

	}
}