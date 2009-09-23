package org.ogee;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class osgi {
	
	public static void registerService(BundleContext bc, String name, Object service) {
		bc.registerService(name, service, null);
	}
	
	public static Object getService(BundleContext bc, String name) {
		ServiceReference ref = bc.getServiceReference(name);
		return bc.getService(ref);
	}
	
}
