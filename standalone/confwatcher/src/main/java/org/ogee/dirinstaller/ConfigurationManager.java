package org.ogee.dirinstaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	final static String ALIAS_KEY = "_alias_factory_pid";

	private ServiceTracker tracker;

	private Queue<String> queue = new ConcurrentLinkedQueue<String>();

	public ConfigurationManager(BundleContext context) {
		this.tracker = new ServiceTracker(context, ConfigurationAdmin.class.getName(), null) {
			@Override
			public Object addingService(ServiceReference ref) {
				onConfigurationAdminAvailable((ConfigurationAdmin) context.getService(ref));
				return super.addingService(ref);
			}
		};
		this.tracker.open();
	}

	private void onConfigurationAdminAvailable(ConfigurationAdmin ca) {
		Queue<String> failed = new ConcurrentLinkedQueue<String>();

		while (!queue.isEmpty()) {
			String filename = queue.remove();
			try {
				setConfig(ca, filename);
			} catch (Exception e) {
				e.printStackTrace();
				failed.add(filename);
			}
		}

		for (String filename : failed) {
			queue.add(filename);
		}
	}

	public void activateConfiguration(String filename) {
		try {
			ConfigurationAdmin cm = (ConfigurationAdmin) tracker.getService();
			if (cm != null) {
				System.out.println();
				setConfig(cm, filename);
			} else {
				queue.add(filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setConfig(ConfigurationAdmin ca, String filename) throws Exception {
		logger.info("Processing configuration: "
				+ filename.substring(filename.lastIndexOf(File.separatorChar) + 1));
		File file = new File(filename);
		Properties p = new Properties();
		InputStream in = new FileInputStream(file);
		p.load(in);
		in.close();

		String pid[] = parsePid(file.getName());
		if (pid[1] != null)
			p.put(ALIAS_KEY, pid[1]);
		Configuration config = getConfiguration(ca, pid[0], pid[1]);
		if (config.getBundleLocation() != null)
			config.setBundleLocation(null);
		config.update(p);
		return;
	}

	private String[] parsePid(String path) {
		String pid = path.substring(0, path.length() - 4);
		int n = pid.indexOf('-');
		if (n > 0) {
			String factoryPid = pid.substring(n + 1);
			pid = pid.substring(0, n);
			return new String[] { pid, factoryPid };
		} else
			return new String[] { pid, null };
	}

	private Configuration getConfiguration(ConfigurationAdmin cm, String pid, String factoryPid)
			throws Exception {
		if (factoryPid != null) {
			Configuration configs[] = cm.listConfigurations("(|(" + ALIAS_KEY + "=" + factoryPid
					+ ")(.alias_factory_pid=" + factoryPid + "))");
			if (configs == null || configs.length == 0)
				return cm.createFactoryConfiguration(pid, null);
			else
				return configs[0];
		} else
			return cm.getConfiguration(pid, null);
	}

}
