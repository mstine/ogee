package org.ogee;

import java.lang.reflect.Method;

import org.osgi.framework.Bundle;

public class ClojureModule {

	private final ClojureRuntime clojureRuntime;
	private final Bundle bundle;
	private final String mainModule;

	private Object mainModuleStart;
	private Method mainModuleStartInvoke;

	private Object mainModuleStop;
	private Method mainModuleStopInvoke;

	public ClojureModule(ClojureRuntime clojureRuntime, Bundle bundle, String mainModule) throws Exception {
		this.clojureRuntime = clojureRuntime;
		this.bundle = bundle;
		this.mainModule = mainModule;
		init();
	}

	private void init() throws Exception {
		ClojureModuleClassLoader cmcl = new ClojureModuleClassLoader(clojureRuntime, bundle);
		Thread.currentThread().setContextClassLoader(cmcl);
		clojureRuntime.loadModule(mainModule);
		clojureRuntime.initClojureModule(bundle, mainModule);

		mainModuleStart = clojureRuntime.getRT_var().invoke(null, mainModule, "start");
		mainModuleStartInvoke = mainModuleStart.getClass().getMethod("invoke", Object.class);

		mainModuleStop = clojureRuntime.getRT_var().invoke(null, mainModule, "stop");
		mainModuleStopInvoke = mainModuleStop.getClass().getMethod("invoke", Object.class);
	}

	public void start() {
		try {
			mainModuleStartInvoke.invoke(mainModuleStart, bundle.getBundleContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			mainModuleStopInvoke.invoke(mainModuleStop, bundle.getBundleContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
