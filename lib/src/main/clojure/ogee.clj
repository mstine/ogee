(ns ogee
	(:import (java.lang.reflect InvocationHandler Proxy)
					 (org.osgi.util.tracker ServiceTracker)
					 (org.osgi.framework BundleContext)))
	
(defn init-module
	[name-ns name-var context]
	(let [atm ((ns-interns (symbol name-ns)) (symbol name-var))]
		(if (not= atm nil)
			(reset! (deref atm) context))))
	
(defn map-to-dictionary
	[inmap]
	(let [table (java.util.Hashtable.)]
		(doseq [k (keys inmap)] (.put table k (get inmap k)))
		table))
		
(defn aop-proxy
	[forclass callback]
	(let [handler (proxy [InvocationHandler] [] (invoke [obj mth args] (callback obj mth args)))]
		(Proxy/newProxyInstance (ClassLoader/getSystemClassLoader) (into-array [forclass]) handler)))

(defn service-tracker
	[context clazz ldap]
	(let [ldapfilter (str "(&(objectClass=" (.getName clazz) ")" ldap ")")
				tracker (ServiceTracker. context (.createFilter context ldapfilter) nil)]
		(.open tracker)
		tracker))
		
(defn service-import
	[context ldap]
	(let [tracker (service-tracker context java.util.Map ldap)]
		(aop-proxy java.util.Map (fn [obj mth args] (.invoke mth (.getService tracker) args)))))
		
(defn service-export 
	([context service] (service-export context service {}))
	([context service property-map]
		(.registerService context (.getName java.util.Map) service (map-to-dictionary property-map))))
	

