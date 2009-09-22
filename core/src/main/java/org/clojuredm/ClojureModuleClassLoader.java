package org.clojuredm;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ClojureModuleClassLoader extends ClassLoader {

	private final Bundle bundle;
	
	private List<String> bypass = new LinkedList<String>();

	public static ClassLoader SHARED_CLOJURE_CL = null;
	public ClassLoader userClojureCl = null;

	public ClojureModuleClassLoader(BundleContext context, Bundle bundle) throws MalformedURLException {
		synchronized (ClojureModuleClassLoader.class) {
			if (SHARED_CLOJURE_CL == null) {
				SHARED_CLOJURE_CL = new ParentLastClassLoader(new URLClassLoader(new URL[] {
						context.getBundle().getEntry("clojure.jar"),
						context.getBundle().getEntry("clojure-contrib.jar") }), new BundleClassLoader(context
						.getBundle()));
			}
		}

		bypass.add("clojure.lang.Keyword");
		bypass.add("clojure.lang.IFn");
		bypass.add("clojure.lang.Named");
		bypass.add("clojure.lang.Symbol");
		bypass.add("clojure.lang.AFn");
		bypass.add("clojure.lang.ISeq");
		bypass.add("clojure.lang.IPersistentMap");

		userClojureCl = new ParentLastClassLoader(new URLClassLoader(new URL[] {
				context.getBundle().getEntry("clojure.jar"),
				context.getBundle().getEntry("clojure-contrib.jar") }), SHARED_CLOJURE_CL, bypass);
		
		this.bundle = bundle;
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		return userClojureCl.loadClass(name);
	}

	@Override
	public URL findResource(String name) {
		String path = name.substring(0, name.lastIndexOf(".")).replace('.', '/');
		String ext = name.substring(name.lastIndexOf("."));
		URL entry = bundle.getResource(path + ext);
		if (entry != null)
			return entry;
		else
			return userClojureCl.getResource(name);
	}

}
