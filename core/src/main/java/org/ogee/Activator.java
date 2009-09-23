package org.ogee;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		ClojureRuntime clojureRuntime = new ClojureRuntime(context);
		ClojureModuleBundleTracker tracker = new ClojureModuleBundleTracker(clojureRuntime, context);
		context.addBundleListener(tracker);
		tracker.checkAllInstalledBundles();
	}

	public void stop(BundleContext context) throws Exception {
	}

}
