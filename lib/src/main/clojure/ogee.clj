(ns ogee
	(:use clojure.contrib.seq-utils)
	(:import (java.lang.reflect InvocationHandler Proxy)
					 (org.osgi.util.tracker ServiceTracker)
					 (org.osgi.framework BundleContext)))
					 
(def modules (ref {}))

(defn ogee-start [] (println "Ogee started."))

(defn ogee-stop []
	(let [allregs (flatten (map deref (map :exported-services (vals @modules))))]
		(doseq [reg allregs] (.unregister reg))))

(defn- init-module
	"Initializes a module. The atom name-ns/name-var will be set to the BundleContext, if present."
	[name-ns name-var context]
	(let [context-atom ((ns-interns (symbol name-ns)) (symbol name-var))]
		(if (not= context-atom nil) (reset! @context-atom context))))
	
(defn module-start
	[name-ns name-var context]
	(init-module name-ns name-var context)
	(dosync (ref-set modules (assoc @modules context {:exported-services (ref [])}))))
	
(defn- map-to-hashtable
	"Convert a map to a hashtable."
	[inmap]
	(let [table (java.util.Hashtable.)]
		(doseq [k (keys inmap)] (.put table k (get inmap k)))
		table))

(defn- aop-proxy
	"Create a proxy for the type forclass. All method calls are delegated to callback.
	The callback function must be compatible to java.lang.reflect.InvocationHandler.invoke(...)."
	[forclass callback]
	(let [handler (proxy [InvocationHandler] [] (invoke [obj mth args] (callback obj mth args)))]
		(Proxy/newProxyInstance (ClassLoader/getSystemClassLoader) (into-array [forclass]) handler)))

(defn- service-tracker
	"Create an OSGi ServiceTracker. It will track all services of type clazz + the ldap filter."
	[context clazz ldap]
	(let [ldap-with-type (str "(&(objectClass=" (.getName clazz) ")" ldap ")")
				tracker (ServiceTracker. context (.createFilter context ldap-with-type) nil)]
		(.open tracker)
		tracker))
		
(defn smap-import
	"Returns a proxy to the service map with a matching name."
	[context sname]
	(let [ldap (str "(ogee.service.name=" (str sname) ")")
				tracker (service-tracker context java.util.Map ldap)]
		(aop-proxy java.util.Map (fn [obj mth args] (.invoke mth (.getService tracker) args)))))
		
(defn smap-export
	"Exports service map 'service' with name 'sname'."
	[context sname service]
	(let [reference (.registerService context (.getName java.util.Map)
	 																					 service
																						 (map-to-hashtable {"ogee.service.name" (str sname)}))]
		(dosync
			(let [ref-es (:exported-services (get @modules context))]
				(ref-set ref-es (conj @ref-es reference))))))
	
