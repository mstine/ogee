package org.ogee;

import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureRuntime {

	private final BundleContext context;
	private URLClassLoader classLoader;

	public ClojureRuntime(BundleContext context) throws Exception {
		this.context = context;
		init();
	}

	public void init() throws Exception {
		classLoader = new URLClassLoader(new URL[] { (URL) context.getBundle().findEntries("ogee-lib",
				"*.jar", false).nextElement() }, new BundleClassLoader(context.getBundle()));

		Thread.currentThread().setContextClassLoader(classLoader);
		loadModule("ogee");
	}

	public void loadModule(String name) throws Exception {
		clojure.lang.RT.load(name);
	}

	public ClassLoader getClojureClassLoader() {
		return classLoader;
	}

	public void initClojureModule(Bundle bundle, String mainModule) throws Exception {
		Var initModule = RT.var("ogee", "init-module");
		initModule.invoke(mainModule, "context", bundle.getBundleContext());
	}

}
