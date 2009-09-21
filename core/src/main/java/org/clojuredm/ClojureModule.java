package org.clojuredm;

import java.lang.reflect.Method;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ClojureModule {

	private final BundleContext context;
	private final Bundle bundle;
	private final String mainModule;

	private ClojureModuleClassLoader classLoader;

	public ClojureModule(BundleContext context, Bundle bundle, String mainModule) {
		this.context = context;
		this.bundle = bundle;
		this.mainModule = mainModule;
	}

	public void start() throws Exception {
		classLoader = new ClojureModuleClassLoader(context, bundle);
		Thread.currentThread().setContextClassLoader(classLoader);

		Class<?> RT = classLoader.loadClass("clojure.lang.RT");
		Method RT_load = RT.getMethod("load", String.class);
		RT_load.invoke(null, mainModule);

		Method RT_var = RT.getMethod("var", String.class, String.class);
		Object mainModuleStart = RT_var.invoke(null, mainModule, "start");
		Method mainModuleStart_invoke = mainModuleStart.getClass().getMethod("invoke", Object.class);
		mainModuleStart_invoke.invoke(mainModuleStart, "YEAHHHHHH");

	}

}
