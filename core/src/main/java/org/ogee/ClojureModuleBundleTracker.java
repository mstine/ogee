package org.ogee;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public class ClojureModuleBundleTracker implements SynchronousBundleListener {

	private final BundleContext context;
	private final Map<Bundle, ClojureModule> trackedBundles = Collections
			.synchronizedMap(new HashMap<Bundle, ClojureModule>());
	private final ClojureRuntime clojureRuntime;

	public ClojureModuleBundleTracker(ClojureRuntime clojureRuntime, BundleContext context) {
		this.clojureRuntime = clojureRuntime;
		this.context = context;
	}

	public void checkAllInstalledBundles() {
		for (Bundle b : context.getBundles())
			analyseNewBundle(b);
	}

	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.STOPPING)
			analyseRemovedBundle(event.getBundle());
		else if (event.getType() == BundleEvent.STARTED)
			analyseNewBundle(event.getBundle());
	}

	private void analyseNewBundle(Bundle b) {
		if (b.getState() != Bundle.ACTIVE)
			return;

		if (trackedBundles.containsKey(b))
			return;

		if (b.getBundleContext() == null)
			return;

		Object cmHeader = b.getHeaders().get("Clojure-Module");
		if (cmHeader != null)
			createClojureModule(b, (String) cmHeader);
	}

	private void analyseRemovedBundle(Bundle b) {
		if (trackedBundles.containsKey(b)) {
			trackedBundles.get(b).stop();
			trackedBundles.remove(b);
		}
	}

	private void createClojureModule(final Bundle bundle, final String mainModule) {
		try {
			ClojureModule module = new ClojureModule(clojureRuntime, bundle, mainModule);
			module.start();
			trackedBundles.put(bundle, module);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
