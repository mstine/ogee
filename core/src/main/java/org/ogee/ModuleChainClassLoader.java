package org.ogee;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.Bundle;

public class ModuleChainClassLoader extends ClassLoader {

	private final List<Bundle> bundles = new LinkedList<Bundle>();
	
	public ModuleChainClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@Override
	public URL findResource(String name) {
//		System.out.println("BChainCL: getResource " + name);
		for (Bundle bundle : bundles) {
			URL resource = bundle.getResource(convertPath(name));
			if (resource != null) {
//				System.out.println("    found in bundlelist " + bundle);
				return resource;
			} else {
//				System.out.println("    not found in bundlelist " + bundle);
			}
		}
		return super.findResource(name);
	}

	private String convertPath(String fqcn) {
		String path = fqcn.substring(0, fqcn.lastIndexOf(".")).replace('.', '/');
		String ext = fqcn.substring(fqcn.lastIndexOf("."));
		return path + ext;
	}

	public void addBundle(Bundle bundle) {
		bundles.add(bundle);
	}
	
	public void removeBundle(Bundle bundle) {
		bundles.remove(bundle);
	}

}
