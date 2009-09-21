package org.clojuredm;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ClojureModuleClassLoader extends URLClassLoader {

	private final Bundle bundle;

	public ClojureModuleClassLoader(BundleContext context, Bundle bundle) throws MalformedURLException {
		super(new URL[] { context.getBundle().getEntry("clojure.jar"),
				context.getBundle().getEntry("clojure-contrib.jar") }, new BundleClassLoader(context
				.getBundle()));

		this.bundle = bundle;
	}

	@Override
	public URL findResource(String name) {
		String path = name.substring(0, name.lastIndexOf(".")).replace('.', '/');
		String ext = name.substring(name.lastIndexOf("."));
		URL entry = bundle.getResource(path + ext);
		if (entry != null)
			return entry;
		else
			return super.findResource(name);
	}

}
