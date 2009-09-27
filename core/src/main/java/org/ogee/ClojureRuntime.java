package org.ogee;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import clojure.lang.DynamicClassLoader;
import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureRuntime {

	private final BundleContext context;
	private ModuleChainClassLoader chain;

	public ClojureRuntime(BundleContext context) throws Exception {
		this.context = context;
	}

	public void init() throws Exception {
		ClassLoader withLib = new URLClassLoader(new URL[] { (URL) context.getBundle().findEntries(
				"ogee-lib", "*.jar", false).nextElement() }, new BundleClassLoader(context.getBundle()));

		chain = new ModuleChainClassLoader(withLib);

		clojure.lang.Compiler.LOADER.bindRoot(new DynamicClassLoader(chain));
		setThreadContextClassLoader();
		
		loadModule("ogee");
		RT.var("ogee", "ogee-start").invoke();
	}

	public void destroy() throws Exception {
		RT.var("ogee", "ogee-stop").invoke();
	}
	
	public void setThreadContextClassLoader() {
		Thread.currentThread().setContextClassLoader(chain);
	}

	public void loadModule(String name) throws Exception {
		Method baseLoader = RT.class.getMethod("baseLoader");
		baseLoader.setAccessible(true);
		System.out.println("BASELOADER:" + baseLoader.invoke(null));
		
		RT.load(name);
	}

	public void addBundleToClassLoader(Bundle bundle) {
		chain.addBundle(bundle);
	}

	public void removeBundleFromClassLoader(Bundle bundle) {
		chain.removeBundle(bundle);
	}

	public void initClojureModule(Bundle bundle, String mainModule) throws Exception {
		Var initModule = RT.var("ogee", "module-start");
		initModule.invoke(mainModule, "context", bundle.getBundleContext());
	}

}
