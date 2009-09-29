/*
 * Copyright (C) 2008 ProSyst Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modulefusion.dirinstaller;

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

public class ConfigurationManager {

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
			System.out.println("new configuration: " + filename);
			ConfigurationAdmin cm = (ConfigurationAdmin) tracker.getService();
			if (cm != null) {
				System.out.println("Activating configuration");
				setConfig(cm, filename);
			} else {
				System.out.println("ConfigurationAdmin not available. Defer activation.");
				queue.add(filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setConfig(ConfigurationAdmin ca, String filename) throws Exception {
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
