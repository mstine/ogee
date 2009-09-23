package org.ogee;

import org.osgi.framework.Bundle;

public class BundleClassLoader extends ClassLoader {

	private final Bundle bundle;

	public BundleClassLoader(Bundle bundle) {
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
