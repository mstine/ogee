package org.ogee;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureModule {

	private final ClojureRuntime clojureRuntime;
	private final Bundle bundle;
	private final String mainModule;

	public ClojureModule(ClojureRuntime clojureRuntime, Bundle bundle, String mainModule) throws Exception {
		this.clojureRuntime = clojureRuntime;
		this.bundle = bundle;
		this.mainModule = mainModule;
		init();
	}

	private void init() throws Exception {
		clojureRuntime.setThreadContextClassLoader();
	}

	public void start() {
		clojureRuntime.setThreadContextClassLoader();
		if (mainModule == null)
			return;
		try {
			clojureRuntime.loadModule(mainModule);
			clojureRuntime.moduleStarted(bundle, mainModule);
			Var mainModuleStart = RT.var(mainModule, "start");
			
			BundleContext bundleContext = bundle.getBundleContext();
			mainModuleStart.invoke(bundleContext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		clojureRuntime.setThreadContextClassLoader();
		if (mainModule == null)
			return;
		try {
			Var mainModuleStop = RT.var(mainModule, "stop");
			mainModuleStop.invoke(bundle.getBundleContext());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				clojureRuntime.moduleStopped(bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
