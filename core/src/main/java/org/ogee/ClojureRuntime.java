package org.ogee;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ClojureRuntime {

	private final BundleContext context;
	private URLClassLoader classLoader;
	private Class<?> RT;
	private Method RT_load;
	private Method RT_var;

	public ClojureRuntime(BundleContext context) throws Exception {
		this.context = context;
		init();
	}

	public void init() throws Exception {
		classLoader = new URLClassLoader(new URL[] {
				(URL) context.getBundle().findEntries("ogee-lib", "*.jar", false).nextElement(),
				context.getBundle().getEntry("clojure-contrib.jar"),
				context.getBundle().getEntry("clojure.jar"), }, new BundleClassLoader(context.getBundle()));

		setCurrentThreadClassLoader();
		RT = classLoader.loadClass("clojure.lang.RT");
		RT_var = RT.getMethod("var", String.class, String.class);
		RT_load = RT.getMethod("load", String.class);
		loadModule("ogee");
	}

	public void setCurrentThreadClassLoader() {
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	public void loadModule(String name) throws Exception {
		RT_load.invoke(null, name);
	}

	public ClassLoader getClojureClassLoader() {
		return classLoader;
	}

	public Class<?> getRT() {
		return RT;
	}

	public Method getRT_var() {
		return RT_var;
	}

	public void initClojureModule(Bundle bundle, String mainModule) throws Exception {
		Object initModule = RT_var.invoke(null, "ogee", "init-module");
		Method initModuleInvoke = initModule.getClass().getMethod("invoke", Object.class, Object.class,
				Object.class);
		initModuleInvoke.invoke(initModule, mainModule, "context", bundle.getBundleContext());
	}

}
