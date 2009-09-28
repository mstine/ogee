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
//		System.out.println("BCL: loadClass " + name);
//		try {
			Class<?> clazz = bundle.loadClass(name);
//			System.out.println("    found in bundle");
			return clazz;
//		} catch (ClassNotFoundException e) {
//			System.out.println("    not found in bundle");
//		}
//		throw new ClassNotFoundException(name);
//		return findClass(name);
//		return super.loadClass(name, resolve);
	}

	@Override
	public URL getResource(String name) {
//		System.out.println("BCL: getResource " + convertPath(name));
		URL resource = bundle.getResource(convertPath(name));
		if (resource != null) {
//			System.out.println("    found in bundle " + bundle);
			return resource;
		} else {
//			System.out.println("    not found in bundle " + bundle);
		}
		return null;
//		return findResource(name);
//		return super.findResource(name);
	}

	private String convertPath(String fqcn) {
		String path = fqcn.substring(0, fqcn.lastIndexOf(".")).replace('.', '/');
		String ext = fqcn.substring(fqcn.lastIndexOf("."));
		return path + ext;
	}

}
