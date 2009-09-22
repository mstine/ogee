package org.clojuredm;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ParentLastClassLoader extends ClassLoader {

	private final ClassLoader delegate;
	private final ClassLoader parent;
	private final List<String> bypass;

	public ParentLastClassLoader(ClassLoader delegate, ClassLoader parent) {
		this(delegate, parent, new LinkedList<String>());
	}

	public ParentLastClassLoader(ClassLoader delegate, ClassLoader parent, List<String> bypass) {
		this.delegate = delegate;
		this.parent = parent;
		this.bypass = bypass;
	}

	@Override
	public URL getResource(String name) {
		ClassLoader first = delegate;
		ClassLoader second = parent;
		if (bypass.contains(name)) {
			first = parent;
			second = delegate;
		}

		URL url = first.getResource(name);
		if (url != null)
			return url;
		else
			return second.getResource(name);
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		ClassLoader first = delegate;
		ClassLoader second = parent;
		if (bypass.contains(name)) {
			first = parent;
			second = delegate;
		}

		try {
			Class<?> load = first.loadClass(name);
			// TODO: Resolve class
			return load;
		} catch (Exception e) {
			return second.loadClass(name);
		}
	}

}
