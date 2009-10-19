(ns ogee.osgi
 (:require
   [clojure.contrib.logging :as logging]
   [clojure.contrib.java-utils :as java-utils]
   ogee.web)
 (:import
   (org.osgi.util.tracker ServiceTracker)
   (org.osgi.framework BundleContext)))

(def bundle-context (ref nil))
(def managed-services (ref {}))
(def configuration-handlers (ref {}))

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
    (java-utils/as-properties {"alias" (str url)})))

(defn- register-managed-service
  [pid]
  (let [ms (proxy [org.osgi.service.cm.ManagedService] []
             (updated [props] (doseq [handler (@configuration-handlers pid)] (handler props))))]
    (.registerService
      @bundle-context
      "org.osgi.service.cm.ManagedService"
      ms
      (java-utils/as-properties {"service.pid" pid}))))

(defn add-configuration-handler
  [pid handler]
  (dosync
    (when
      (nil? (@managed-services pid))
      (alter managed-services assoc pid (register-managed-service pid)) ; yuck, side effect in tx. need to fix this...
      ))
  (dosync
    (alter configuration-handlers update-in [pid] conj handler)))

(defn init [context]
  (dosync
    (ref-set bundle-context context)
    (ref-set ogee.web/register-servlet-method register-servlet)))
