package org.ogee;

import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

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
