package org.ogee;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clojure.lang.RT;
import clojure.lang.Var;

public class ClojureRuntime {

	private final Logger logger = LoggerFactory.getLogger(ClojureRuntime.class);

	private final BundleContext context;

	private final URL[] initCljs;

	private ClassLoader classLoader;

	public ClojureRuntime(BundleContext context, URL[] initCljs) throws Exception {
		this.context = context;
		this.initCljs = initCljs;
		ClassLoader bundle = new BundleClassLoader(context.getBundle());
		ClassLoader withLib = new URLClassLoader(new URL[] {(URL) context.getBundle().findEntries("ogee-lib", "*.jar", false)
				.nextElement()}, bundle);
		classLoader = new URLClassLoader(initCljs, withLib);
	}

	public void init() throws Exception {
		setThreadContextClassLoader();

		loadModule("ogee");
		RT.var("ogee", "ogee-start").invoke();

		for (URL clj : initCljs) {
			JarFile jar = new JarFile(new File(clj.toURI()));
			String cm = jar.getManifest().getMainAttributes().getValue("Clojure-Module");
			if (cm == null)
				continue;
			System.out.println("Loading module: " + cm);
			loadModule(cm);
			moduleStarted(context.getBundle(), cm);
			System.out.println("###");
		}
	}

	public void destroy() throws Exception {
		RT.var("ogee", "ogee-stop").invoke();
	}

	public void setThreadContextClassLoader() {
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	public void loadModule(String name) throws Exception {
		RT.load(name);
	}

	public void moduleStarted(Bundle bundle, String mainModule) throws Exception {
		Var fn = RT.var("ogee", "module-added");
		fn.invoke(mainModule, "context", bundle.getBundleContext());
	}

	public void moduleStopped(Bundle bundle) throws Exception {
		Var fn = RT.var("ogee", "module-removed");
		fn.invoke(bundle.getBundleContext());
	}

	public void bundleUpdated(Bundle bundle) {
		logger.info("Bundle [" + bundle + "] updated. Refresh Ogee bundle [" + context.getBundle()
				+ "] to apply changes.");
	}

}
