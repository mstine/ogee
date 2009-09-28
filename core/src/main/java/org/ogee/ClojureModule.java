package org.ogee;

import org.osgi.framework.Bundle;

import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureModule {

	private final ClojureRuntime clojureRuntime;
	private final Bundle bundle;
	private final String mainModule;

	private Var mainModuleStart;
	private Var mainModuleStop;

	public ClojureModule(ClojureRuntime clojureRuntime, Bundle bundle, String mainModule) throws Exception {
		this.clojureRuntime = clojureRuntime;
		this.bundle = bundle;
		this.mainModule = mainModule;
		init();
	}

	private void init() throws Exception {
		clojureRuntime.setThreadContextClassLoader();

		clojureRuntime.loadModule(bundle, mainModule);
		clojureRuntime.initClojureModule(bundle, mainModule);
		mainModuleStart = RT.var(mainModule, "start");
		mainModuleStop = RT.var(mainModule, "stop");
	}

	public void start() {
		try {
			mainModuleStart.invoke(bundle.getBundleContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			mainModuleStop.invoke(bundle.getBundleContext());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clojureRuntime.removeBundle(bundle);
		}
	}

}
