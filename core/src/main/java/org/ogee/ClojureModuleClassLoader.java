package org.ogee;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ClojureModuleClassLoader extends ClassLoader {

	private final Bundle bundle;

	public static ClassLoader SHARED_CLOJURE_CL = null;

	public ClojureModuleClassLoader(BundleContext context, Bundle bundle) throws MalformedURLException {
		synchronized (ClojureModuleClassLoader.class) {
			if (SHARED_CLOJURE_CL == null) {
				SHARED_CLOJURE_CL = new URLClassLoader(new URL[] {
						(URL) context.getBundle().findEntries("ogee-lib", "*.jar", false).nextElement(),
						context.getBundle().getEntry("clojure-contrib.jar"),
						context.getBundle().getEntry("clojure.jar"), }, new BundleClassLoader(context
						.getBundle()));
			}
		}
		this.bundle = bundle;
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			Class<?> loadClass = bundle.loadClass(name);
			return loadClass;
		} catch (Exception e) {
			return SHARED_CLOJURE_CL.loadClass(name);
		}
	}

	@Override
	public URL findResource(String name) {
		String path = name.substring(0, name.lastIndexOf(".")).replace('.', '/');
		String ext = name.substring(name.lastIndexOf("."));
		URL entry = bundle.getResource(path + ext);
		if (entry != null)
			return entry;
		else
			return SHARED_CLOJURE_CL.getResource(name);
	}

}
