(ns ogee.sampleapp.app
 (:use [ogee.osgi :as osgi]))

(defn config-callback
  [props]
  (println "got config:" props))

(defn start []
  (println "app1 started")
  (osgi/add-configuration-handler "abc" config-callback))

(defn stop []
  (println "app1 stopped"))

