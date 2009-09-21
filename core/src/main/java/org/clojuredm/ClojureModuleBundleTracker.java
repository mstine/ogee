package org.clojuredm;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class ClojureModuleBundleTracker implements BundleListener {

	private final BundleContext context;

	public ClojureModuleBundleTracker(BundleContext context) {
		this.context = context;
	}

	public void bundleChanged(BundleEvent event) {
		if (event.getType() != BundleEvent.STARTED)
			return;
		
		Object cmHeader = event.getBundle().getHeaders().get("Clojure-Module");
		if (cmHeader != null)
			createClojureModule(event.getBundle(), (String) cmHeader);
	}

	private void createClojureModule(Bundle bundle, String mainModule) {
		ClojureModule module = new ClojureModule(context, bundle, mainModule);
		try {
			module.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
