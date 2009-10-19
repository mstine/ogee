(ns ogee.osgi
 (:require
   [clojure.contrib.logging :as logging]
   ogee.utils)
 (:import
   (org.osgi.util.tracker ServiceTracker)
   (org.osgi.framework BundleContext)))

(def bundle-context (ref nil))

(def managed-services (ref {}))
(def configuration-handlers (ref {}))

(defn set-bundle-context [context]
  (dosync (ref-set bundle-context context)))

(defn- service-tracker
  "Create an OSGi ServiceTracker. It will track all services of type clazz + the ldap filter."
  [clazz ldap]
  (let [ldap-with-type (str "(&(objectClass=" (.getName clazz) ")" ldap ")")
        tracker (ServiceTracker. @bundle-context (.createFilter @bundle-context ldap-with-type) nil)]
    (.open tracker)
    tracker))

(defn register-servlet
  [servlet url]
  (.registerService 
    @bundle-context
    (.getName javax.servlet.Servlet)
    servlet
    (ogee.utils/map-to-hashtable {"alias" (str url)})))

(defn- register-managed-service
  [pid]
  (let [ms (proxy [org.osgi.service.cm.ManagedService] []
             (updated [props] (doseq [handler (@configuration-handlers pid)] (handler props))))]
    (.registerService
      @bundle-context
      "org.osgi.service.cm.ManagedService"
      ms
      (ogee.utils/map-to-hashtable {"service.pid" pid}))))

(defn add-configuration-handler
  [pid handler]
  (dosync
    (when
      (nil? (@managed-services pid))
      (alter managed-services assoc pid (register-managed-service pid)) ; yuck, side effect in tx. need to fix this...
      ))
  (dosync
    (alter configuration-handlers update-in [pid] conj handler)))
