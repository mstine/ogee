package org.clojuredm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		context.addBundleListener(new ClojureModuleBundleTracker(context));
	}

	public void stop(BundleContext context) throws Exception {
	}

}
