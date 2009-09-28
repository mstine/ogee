package org.ogee;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private ClojureRuntime clojureRuntime;

	public void start(BundleContext context) throws Exception {
		clojureRuntime = new ClojureRuntime(context);
		clojureRuntime.init();
		ClojureModuleBundleTracker tracker = new ClojureModuleBundleTracker(clojureRuntime, context);
		context.addBundleListener(tracker);
		tracker.checkAllInstalledBundles();
	}

	public void stop(BundleContext context) throws Exception {
		clojureRuntime.destroy();
	}

}
