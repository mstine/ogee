package org.ogee;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String CLJS_DIR = "cljs.dir";
	private ClojureRuntime clojureRuntime;

	public void start(BundleContext context) throws Exception {
		String cljsDir = context.getProperty(CLJS_DIR);
		cljsDir = cljsDir == null ? "cljs" : cljsDir;

		clojureRuntime = new ClojureRuntime(context, getAllCljs(cljsDir));
		clojureRuntime.init();
	}

	public void stop(BundleContext context) throws Exception {
		clojureRuntime.destroy();
	}

	private URL[] getAllCljs(String cljsDir) {
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

}
