(ns ogee.osgi
	(:require 
		[clojure.contrib.logging :as logging]
		ogee.utils)
	(:import 
		(org.osgi.util.tracker ServiceTracker)
		(org.osgi.framework BundleContext)))

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
    (ogee.utils/aop-proxy java.util.Map (fn [obj mth args] (.invoke mth (.getService tracker) args)))))
		
(defn smap-export
  "Exports service map 'service' with name 'sname'."
  [context sname service]
  (.registerService context "java.util.Map" service (ogee.utils/map-to-hashtable {"ogee.service.name" (str sname)})))

(defn register-servlet
	[context servlet url]
	(.registerService context "javax.servlet.Servlet" servlet (ogee.utils/map-to-hashtable {"alias" (str url)})))
	
	
