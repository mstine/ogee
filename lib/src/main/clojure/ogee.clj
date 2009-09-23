(ns ogee
	(:import (java.lang.reflect InvocationHandler Proxy)
					 (org.osgi.util.tracker ServiceTracker)
					 (org.osgi.framework BundleContext)))
	
(defn init-module
	"Initializes a module. The atom name-ns/name-var will be set to the BundleContext, if present."
	[name-ns name-var context]
	(let [context-atom ((ns-interns (symbol name-ns)) (symbol name-var))]
		(if (not= context-atom nil) (reset! @context-atom context))))
	
(defn map-to-hashtable
	"Convert a map to a hashtable."
	[inmap]
	(let [table (java.util.Hashtable.)]
		(doseq [k (keys inmap)] (.put table k (get inmap k)))
		table))

(defn aop-proxy
	"Create a proxy for the type forclass. All method calls are delegated to callback.
	The callback function must be compatible to java.lang.reflect.InvocationHandler.invoke(...)."
	[forclass callback]
	(let [handler (proxy [InvocationHandler] [] (invoke [obj mth args] (callback obj mth args)))]
		(Proxy/newProxyInstance (ClassLoader/getSystemClassLoader) (into-array [forclass]) handler)))

(defn service-tracker
	"Create an OSGi ServiceTracker. It will track all services of type clazz + the ldap filter."
	[context clazz ldap]
	(let [ldap-with-type (str "(&(objectClass=" (.getName clazz) ")" ldap ")")
				tracker (ServiceTracker. context (.createFilter context ldap-with-type) nil)]
		(.open tracker)
		tracker))
		
(defn service-import
	"Returns a proxy to the service with matching id."
	[context id]
	(let [ldap (str "(ogee.service.id=" (str id) ")")
				tracker (service-tracker context java.util.Map ldap)]
		(aop-proxy java.util.Map (fn [obj mth args] (.invoke mth (.getService tracker) args)))))
		
(defn service-export
	"Exports service with id. Service is supposed to be a map."
	[context id service]
		(.registerService context (.getName java.util.Map) service (map-to-hashtable {"ogee.service.id" (str id)})))
	

