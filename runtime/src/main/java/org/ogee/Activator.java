package org.ogee;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String CLJS_DIR = "cljs.dir";
	private ClojureRuntime clojureRuntime;
	private String cljsDir;
	private boolean shutdown = false;
	private BundleContext context;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		
		cljsDir = context.getProperty(CLJS_DIR);
		cljsDir = cljsDir == null ? "cljs" : cljsDir;

		clojureRuntime = new ClojureRuntime(context, getAllCljs());
		clojureRuntime.init();

		startDirWatcher();
	}

	public void stop(BundleContext context) throws Exception {
		this.shutdown  = true;
		clojureRuntime.destroy();
	}

	private URL[] getAllCljs() {
		File[] allFiles = new File(cljsDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(".jar"))
					return true;
				else
					return false;
			}
		});
		URL[] fileUrls = new URL[allFiles.length];
		for (int i = 0; i < allFiles.length; i++) {
			try {
				fileUrls[i] = allFiles[i].toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		return fileUrls;
	}

	private void startDirWatcher() {
		final long started = System.currentTimeMillis();
		new Thread() {
			public void run() {
				try {
				while (!Activator.this.shutdown) {
						Thread.sleep(500);
					URL[] allCljs = getAllCljs();
					for (URL url : allCljs) {
						File f = new File(url.toURI());
						if (f.lastModified() > started) {
							Activator.this.shutdown = true;
							restartOgee();
						}
					}
				}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}.start();
	}
	
	private void restartOgee() {
		logger.info("Restarting Ogee...");
		ServiceTracker st = new ServiceTracker(context, PackageAdmin.class.getName(), null);
		st.open();
		((PackageAdmin) st.getService()).refreshPackages(new Bundle[] {context.getBundle()});
	}
}
