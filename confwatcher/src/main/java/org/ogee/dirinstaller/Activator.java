package org.ogee.dirinstaller;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String DIRINSTALLER_POLL = "dirinstaller.poll";
	private static final String DIRINSTALLER_DIR = "dirinstaller.dir";
	
	private DirWatcher watcher;
	
	public void start(BundleContext context) throws Exception {
		String bundlesDir = context.getProperty(DIRINSTALLER_DIR);
		bundlesDir = bundlesDir == null ? "conf" : bundlesDir;
		
		String intervalStr = context.getProperty(DIRINSTALLER_POLL);
		Integer interval = intervalStr == null ? 2000 : Integer.valueOf(intervalStr);
		
		watcher = new DirWatcher(context, bundlesDir, interval);
		watcher.startWatching();
	}

	public void stop(BundleContext context) throws Exception {
		watcher.stopWatching();
	}

}
