package org.ogee.sampleapp3java;

import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import clojure.lang.IFn;
import clojure.lang.Keyword;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		Filter f = context.createFilter("(&(objectClass=java.util.Map)(ogee.service.name=*))");
		new ServiceTracker(context, f, null) {
			@SuppressWarnings("unchecked")
			@Override
			public Object addingService(ServiceReference reference) {
				Map<Keyword, IFn> sm = (Map<Keyword, IFn>) context.getService(reference);
				IFn ifn = sm.get(Keyword.intern("hello"));
				try {
					ifn.invoke();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return super.addingService(reference);
			}
		}.open();
	}

	public void stop(BundleContext context) throws Exception {
	}

}
