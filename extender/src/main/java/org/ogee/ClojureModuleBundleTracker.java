package org.ogee;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public class ClojureModuleBundleTracker implements SynchronousBundleListener {

	private final BundleContext context;
	private final Map<Bundle, ClojureModule> trackedBundles = new HashMap<Bundle, ClojureModule>();
	private final ClojureRuntime clojureRuntime;

	public ClojureModuleBundleTracker(ClojureRuntime clojureRuntime, BundleContext context) {
		this.clojureRuntime = clojureRuntime;
		this.context = context;
	}

	public void checkAllInstalledBundles() {
		for (Bundle b : context.getBundles()) {
			onNewBundle(b);
			if (b.getState() == Bundle.ACTIVE)
				onStartedBundle(b);
		}
	}

	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.RESOLVED)
			onNewBundle(event.getBundle());
		else if (event.getType() == BundleEvent.STARTED)
			onStartedBundle(event.getBundle());
		else if (event.getType() == BundleEvent.STOPPED)
			onStoppedBundle(event.getBundle());
		else if (event.getType() == BundleEvent.UPDATED)
			onUpdatedBundle(event.getBundle());
		else if (event.getType() == BundleEvent.UNINSTALLED)
			onUninstalledBundle(event.getBundle());
	}

	private void onNewBundle(Bundle b) {
		synchronized (trackedBundles) {
			if (trackedBundles.containsKey(b))
				return;
			String cmHeader = (String) b.getHeaders().get("Clojure-Module");
			if (cmHeader == null && !b.getLocation().endsWith("clj.jar"))
				return;

			clojureRuntime.addModuleToClasspath(b);
			ClojureModule module = null;
			try {
				module = new ClojureModule(clojureRuntime, b, cmHeader);
			} catch (Exception e) {
				e.printStackTrace();
			}
			trackedBundles.put(b, module);
		}
	}

	private void onStartedBundle(Bundle b) {
		synchronized (trackedBundles) {
			if (!trackedBundles.containsKey(b))
				return;
			if (b.getBundleContext() == null)
				return;

			try {
				trackedBundles.get(b).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void onStoppedBundle(Bundle b) {
		synchronized (trackedBundles) {
			if (!trackedBundles.containsKey(b))
				return;

			trackedBundles.get(b).stop();
		}
	}

	private void onUpdatedBundle(Bundle b) {
		synchronized (trackedBundles) {
			if (!trackedBundles.containsKey(b))
				return;

			clojureRuntime.bundleUpdated(b);
		}
	}

	private void onUninstalledBundle(Bundle b) {
		synchronized (trackedBundles) {
			if (!trackedBundles.containsKey(b))
				return;

			clojureRuntime.removeModuleFromClasspath(b);
			trackedBundles.remove(b);
		}
	}

}
