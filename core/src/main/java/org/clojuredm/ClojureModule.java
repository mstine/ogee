package org.clojuredm;

import java.lang.reflect.Method;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ClojureModule {

	private final BundleContext context;
	private final Bundle bundle;
	private final String mainModule;

	private ClojureModuleClassLoader classLoader;

	private Object mainModuleStart;
	private Method mainModuleStartInvoke;

	private Object mainModuleStop;
	private Method mainModuleStopInvoke;
	private Class<?> RT;
	private Method RT_var;

	public ClojureModule(BundleContext context, Bundle bundle, String mainModule) throws Exception {
		this.context = context;
		this.bundle = bundle;
		this.mainModule = mainModule;
		init();
	}

	private void init() throws Exception {
		classLoader = new ClojureModuleClassLoader(context, bundle);
		Thread.currentThread().setContextClassLoader(classLoader);

		RT = classLoader.loadClass("clojure.lang.RT");
		Method RT_load = RT.getMethod("load", String.class);
		RT_load.invoke(null, "clojuredm.osgi");
		RT_load.invoke(null, mainModule);

		RT_var = RT.getMethod("var", String.class, String.class);

		Object initModule = RT_var.invoke(null, "clojuredm.osgi", "init-module");
		Method initModuleInvoke = initModule.getClass().getMethod("invoke", Object.class, Object.class,
				Object.class);
		initModuleInvoke.invoke(initModule, mainModule, "context", bundle.getBundleContext());

		mainModuleStart = RT_var.invoke(null, mainModule, "start");
		mainModuleStartInvoke = mainModuleStart.getClass().getMethod("invoke", Object.class);

		mainModuleStop = RT_var.invoke(null, mainModule, "stop");
		mainModuleStopInvoke = mainModuleStop.getClass().getMethod("invoke", Object.class);
	}

	// private Object symbol(String name) throws Exception {
	// Method Symbol_intern = Symbol.getMethod("intern", String.class);
	// return Symbol_intern.invoke(null, name);
	// }

	// private void run(String ns, String callable, Object... params) throws
	// Exception {
	// Object intern = RT_var.invoke(null, ns, callable);
	// Class<?>[] dummyTypes = new Class[params.length];
	// for (int i = 0; i < params.length; i++) {
	// dummyTypes[i] = Object.class;
	// }
	// Method internInvoke = intern.getClass().getMethod("invoke", dummyTypes);
	// internInvoke.invoke(intern, params);
	// }

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
