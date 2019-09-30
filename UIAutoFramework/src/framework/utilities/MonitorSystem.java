package framework.utilities;

import com.sun.management.OperatingSystemMXBean;
import framework.core.SetUpTearDownScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

public class MonitorSystem implements Runnable {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConfigurationLib configLib = new ConfigurationLib();

	/**
	 * To keep checking the system load at runtime
	 *
	 */
	@Override
	public void run() {
		int interval = -1;
		try {
			while (SetUpTearDownScript.sysMonitor) {
				isSystemOverloaded();
				if (configLib.getMonitorInterval() != null)
					interval = Integer.parseInt(configLib.getMonitorInterval());
				else
					interval = 60;
				Thread.sleep(interval * 1000);
			}
		}
		catch (Exception e) {
			logger.error("Unable to monitor the system under test", e);
		}

	}

	/**
	 * To get what % CPU load this current JVM is taking and what % load the overall
	 * system is at and refresh the browser if
	 *
	 */

	private void isSystemOverloaded() {
		int loadLimit = -1;
		try {
			if (configLib.getLoadAlertLimit() != null)
				loadLimit = Integer.parseInt(configLib.getLoadAlertLimit());
			else
				loadLimit = 95;
			OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
			if ((osBean.getProcessCpuLoad() * 100 > loadLimit) || (osBean.getSystemCpuLoad() * 100 > loadLimit)) {
				logger.warn("System is overloaded. CPU load is " + osBean.getSystemCpuLoad() * 100
						+ " and Java usage is " + osBean.getProcessCpuLoad() * 100);
			}
		}
		catch (Exception e) {
			logger.error("Unable to get the CPU usage", e);
		}
	}

}
