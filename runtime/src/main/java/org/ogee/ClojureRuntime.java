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

public class ClojureRuntime {

	private final Logger logger = LoggerFactory.getLogger(ClojureRuntime.class);

	private final BundleContext context;

	private final URL[] initCljs;

	private ClassLoader classLoader;

	public ClojureRuntime(BundleContext context, URL[] initCljs) throws Exception {
		this.context = context;
		this.initCljs = initCljs;
		ClassLoader bundle = new BundleClassLoader(context.getBundle());
		ClassLoader libs = classLoader = new URLClassLoader(initCljs, bundle);
//		classLoader = new ClojureClassLoader(bundle, libs);
		classLoader = libs;
	}

	public void init() throws Exception {
		setThreadContextClassLoader();
		logger.info("Starting Ogee...");
		loadModule("ogee");
		RT.var("ogee", "ogee-start").invoke();

		for (URL clj : initCljs) {
			JarFile jar = new JarFile(new File(clj.toURI()));
			String cm = jar.getManifest().getMainAttributes().getValue("Clojure-Module");
			if (cm == null)
				continue;
			loadModule(cm);
			moduleStarted(context.getBundle(), cm);
		}
	}

	public void destroy() throws Exception {
		RT.var("ogee", "ogee-stop").invoke();
	}

	public void setThreadContextClassLoader() {
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	public void loadModule(String name) throws Exception {
		RT.load(name.replace('.', '/'));
	}

	public void moduleStarted(Bundle bundle, String mainModule) throws Exception {
		RT.var("ogee", "module-added").invoke(mainModule, "context", bundle.getBundleContext());
		RT.var(mainModule, "start").invoke(context);
	}

	public void moduleStopped(Bundle bundle) throws Exception {
		RT.var("ogee", "module-removed").invoke(bundle.getBundleContext());
	}

}
