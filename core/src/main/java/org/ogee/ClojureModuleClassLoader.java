package org.ogee;

import java.net.MalformedURLException;
import java.net.URL;

import org.osgi.framework.Bundle;

public class ClojureModuleClassLoader extends ClassLoader {

	private final Bundle bundle;
	private final ClojureRuntime runtime;

	public ClojureModuleClassLoader(ClojureRuntime runtime, Bundle bundle) throws MalformedURLException {
		this.runtime = runtime;
		this.bundle = bundle;
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		return runtime.getClojureClassLoader().loadClass(name);
	}

	@Override
	public URL findResource(String name) {
		String path = name.substring(0, name.lastIndexOf(".")).replace('.', '/');
		String ext = name.substring(name.lastIndexOf("."));
		URL entry = bundle.getResource(path + ext);
		if (entry != null)
			return entry;
		else
			return runtime.getClojureClassLoader().getResource(name);
	}

}
