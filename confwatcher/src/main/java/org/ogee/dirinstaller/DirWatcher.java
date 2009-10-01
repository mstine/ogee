package org.ogee.dirinstaller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

public class DirWatcher {

	private Thread thread;

	private final String bundlesDir;
	private final int interval;

	private final Map<String, Long> timestamps = new HashMap<String, Long>();

	private ConfigurationManager configManager;

	private boolean warningMissingLoadDirPresented = false;

	public DirWatcher(BundleContext context, String bundlesDir, int interval) {
		this.bundlesDir = bundlesDir;
		this.interval = interval;
		this.configManager = new ConfigurationManager(context);
	}

	public void startWatching() {
		thread = new Thread() {
			@Override
			public void run() {
				try {
					while (!Thread.interrupted()) {
						List<File> found = new ArrayList<File>();
						getAllFiles(found, bundlesDir);
						analyseNewState(found);
						Thread.sleep(interval);
					}
				} catch (InterruptedException e) {
				}
			}
		};
		thread.start();
	}

	public void stopWatching() {
		thread.interrupt();
	}

	private void getAllFiles(List<File> found, String dirName) {
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		if (files == null) {
			if (!warningMissingLoadDirPresented) {
				System.out.println("DirInstaller WARNING: Directory '" + dirName + "' does not exist!");
				warningMissingLoadDirPresented = true;
			}
			return;
		}

		for (File f : files) {
			try {
				if (f.isFile()) {
					if (f.getName().endsWith(".cfg")) {
						found.add(f);
					}
				} else {
					getAllFiles(found, f.getCanonicalPath());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void analyseNewState(List<File> found) {
		// check for new or updated bundles
		for (File file : found) {
			try {
				String string = file.getCanonicalPath();
				Long time = timestamps.get(string);

				// time == null: system startup
				// time < lastModified: updated file
				if (time == null || time < file.lastModified()) {
					timestamps.put(string, file.lastModified());
					configManager.activateConfiguration(string);
				}
			} catch (IOException e) {
				System.out.println("DirInstaller: Problems accessing file " + file.getName());
				e.printStackTrace();
			}
		}
	}
}
