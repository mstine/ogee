package org.clojuredm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		ClojureModuleBundleTracker tracker = new ClojureModuleBundleTracker(context);
		context.addBundleListener(tracker);
		tracker.checkAllInstalledBundles();
	}

	public void stop(BundleContext context) throws Exception {
	}

}
