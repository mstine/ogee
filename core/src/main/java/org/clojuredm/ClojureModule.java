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

	public ClojureModule(BundleContext context, Bundle bundle, String mainModule) throws Exception {
		this.context = context;
		this.bundle = bundle;
		this.mainModule = mainModule;
		init();
	}
	
	private void init() throws Exception {
		classLoader = new ClojureModuleClassLoader(context, bundle);
		Thread.currentThread().setContextClassLoader(classLoader);
		
		Class<?> RT = classLoader.loadClass("clojure.lang.RT");
		Method RT_load = RT.getMethod("load", String.class);
		RT_load.invoke(null, mainModule);
		
		Method RT_var = RT.getMethod("var", String.class, String.class);
		
		mainModuleStart = RT_var.invoke(null, mainModule, "start");
		mainModuleStartInvoke = mainModuleStart.getClass().getMethod("invoke", Object.class);
		
		mainModuleStop = RT_var.invoke(null, mainModule, "stop");
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
			throw new RuntimeException(e);
		}
	}

}
