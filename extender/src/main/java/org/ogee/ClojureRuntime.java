package org.ogee;

import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureRuntime {

	private final Logger logger = LoggerFactory.getLogger(ClojureRuntime.class);

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

		loadModule(null, "ogee");
		RT.var("ogee", "ogee-start").invoke();
	}

	public void destroy() throws Exception {
		RT.var("ogee", "ogee-stop").invoke();
	}

	public void setThreadContextClassLoader() {
		Thread.currentThread().setContextClassLoader(chain);
	}

	public void loadModule(Bundle bundle, String name) throws Exception {
		if (bundle != null)
			chain.addBundle(bundle);
		RT.load(name);
	}

	public void moduleAdded(Bundle bundle, String mainModule) throws Exception {
		Var fn = RT.var("ogee", "module-added");
		fn.invoke(mainModule, "context", bundle.getBundleContext());
	}

	public void moduleRemoved(Bundle bundle) throws Exception {
		chain.removeBundle(bundle);
		Var fn = RT.var("ogee", "module-removed");
		fn.invoke(bundle.getBundleContext());
	}
	
	public void bundleUpdated(Bundle bundle) {
		logger.info("Bundle [" + bundle + "] updated. Refresh Ogee bundle [" + context.getBundle()
				+ "] to apply changes.");
	}

}
