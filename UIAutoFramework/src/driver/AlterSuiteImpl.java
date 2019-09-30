package driver;

import framework.utilities.ConfigurationLib;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.ParallelMode;

import java.util.List;

public class AlterSuiteImpl implements IAlterSuiteListener {

	/**
	 * To set the thread count at run time
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void alter(List<XmlSuite> suites) {
		ConfigurationLib configLib = new ConfigurationLib();
		XmlSuite suite = suites.get(0);
		suite.setParallel(ParallelMode.TRUE);
		suite.setThreadCount(Integer.parseInt(configLib.getThreadCountConfig()));
	}
}
