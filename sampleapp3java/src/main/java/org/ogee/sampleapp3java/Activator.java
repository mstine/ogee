package org.ogee.sampleapp3java;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		// Filter f =
		// context.createFilter("(&(objectClass=java.util.Map)(ogee.service.name=*))");
		// new ServiceTracker(context, f, null) {
		// @SuppressWarnings("unchecked")
		// @Override
		// public Object addingService(ServiceReference reference) {
		// Map<Keyword, IFn> sm = (Map<Keyword, IFn>)
		// context.getService(reference);
		// IFn ifn = sm.get(Keyword.intern("hello"));
		// try {
		// ifn.invoke();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return super.addingService(reference);
		// }
		// }.open();
	}

	public void stop(BundleContext context) throws Exception {
	}

}
