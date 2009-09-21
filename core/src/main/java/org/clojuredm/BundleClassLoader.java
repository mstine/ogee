package org.clojuredm;

import org.osgi.framework.Bundle;

public class BundleClassLoader extends ClassLoader {

	private final Bundle bundle;

	public BundleClassLoader(Bundle bundle, ClassLoader parent) {
		super(parent);
		this.bundle = bundle;
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		try {
			return bundle.loadClass(name);
		} catch (ClassNotFoundException e) {
		}
		return super.loadClass(name, resolve);
	}

}
