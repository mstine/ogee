package org.ogee;

import java.net.URL;

import org.osgi.framework.Bundle;

public class BundleClassLoader extends ClassLoader {

	private final Bundle bundle;

	public BundleClassLoader(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		// System.out.println("BCL: loadClass " + name);
		try {
			Class<?> clazz = bundle.loadClass(name);
			// System.out.println("    found in bundle");
			return clazz;
		} catch (ClassNotFoundException e) {
			// System.out.println("    not found in bundle");
		}
		return super.loadClass(name, resolve);
	}

	@Override
	public URL getResource(String name) {
		// System.out.println("BCL: getResource " + name);
		URL resource = bundle.getResource(name);
		if (resource != null) {
			// System.out.println("    found in bundle");
			return resource;
		} else {
			// System.out.println("    not found in bundle");
			return super.getResource(name);
		}
	}

}
