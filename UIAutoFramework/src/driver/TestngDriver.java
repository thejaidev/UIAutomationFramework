package driver;

import framework.utilities.ConfigurationLib;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestngDriver {

	public static boolean suiteExec = false;

	/**
	 * To trigger the suite execution without depending on the testng.xml
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Logger.getRootLogger().setLevel(Level.OFF);
		ConfigurationLib configLib = new ConfigurationLib();
		String binPath = configLib.getTestSuitePath();
		TestNG testng = new TestNG();
		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		XmlSuite mySuite = new XmlSuite();
		List<XmlClass> xmlClasses = new ArrayList<XmlClass>();
		XmlTest test = null;
		String className = null;
		binPath = binPath.replace("null", "");

		File dir = new File(binPath);
		List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File f : files) {
			test = new XmlTest(mySuite);
			className = ((f.getCanonicalPath().replace(configLib.getBinaryPath(), "")).replace("\\", "."));
			className = StringUtils.removeEnd(className, ".class");
			test.setName(className);
			xmlClasses.add(new XmlClass(Class.forName(className)));
		}

		test.setXmlClasses(xmlClasses);
		AnnotationTransformerImpl myTransformer = new AnnotationTransformerImpl();
		testng.addListener(myTransformer);
		AlterSuiteImpl alterSuite = new AlterSuiteImpl();
		testng.addListener(alterSuite);
		suites.add(mySuite);
		testng.setXmlSuites(suites);
		suiteExec = true;
		testng.run();
	}
}
