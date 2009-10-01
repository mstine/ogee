package org.ogee;

import java.net.URL;

import org.osgi.framework.Bundle;

public class BundleClassLoader extends ClassLoader {

	private final Bundle bundle;

	public BundleClassLoader(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
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
	public URL findResource(String name) {
//		System.out.println("BCL: getResource " + convertPath(name));
		URL resource = bundle.getResource(ClassLoaderUtils.convertPath(name));
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

}
