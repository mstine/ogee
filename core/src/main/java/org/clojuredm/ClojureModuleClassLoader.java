package org.clojuredm;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ClojureModuleClassLoader extends URLClassLoader {

	public ClojureModuleClassLoader(BundleContext context, Bundle bundle) throws MalformedURLException {
		super(new URL[] { context.getBundle().getEntry("clojure.jar"),
				context.getBundle().getEntry("clojure-contrib.jar"), new URL(bundle.getLocation()) },
				new BundleClassLoader(context.getBundle()));
	}

	// @Override
	// public URL findResource(String name) {
	// String path = name.substring(0, name.lastIndexOf(".")).replace('.', '/');
	// String ext = name.substring(name.lastIndexOf("."));
	// URL entry = bundle.getEntry(path + ext);
	// return entry;
	// }

}
