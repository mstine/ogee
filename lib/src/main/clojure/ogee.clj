(ns ogee
	(:use clojure.contrib.seq-utils)
	(:require [clojure.contrib.logging :as logging])
	(:import (java.lang.reflect InvocationHandler Proxy)
					 (org.osgi.util.tracker ServiceTracker)
					 (org.osgi.framework BundleContext)))
					 
(defn ogee-start []
	(logging/info "Ogee started."))

(defn ogee-stop []
	"Shutdown the Ogee runtime."
	(logging/info "Stopping Ogee..."))

(defn- inject-bundle-context
	"Initializes a module. The atom name-ns/name-var will be set to the BundleContext, if present."
	[name-ns name-var context]
	(let [context-atom ((ns-interns (symbol name-ns)) (symbol name-var))]
		(if (not= context-atom nil) (reset! @context-atom context))))
	
(defn module-added
	"Activate the clojure module. name-ns/name-var specify the var where the BundleContext should be injected."
	[name-ns name-var context]
	(inject-bundle-context name-ns name-var context))
	
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
  (.registerService context "java.util.Map" service (map-to-hashtable {"ogee.service.name" (str sname)})))

