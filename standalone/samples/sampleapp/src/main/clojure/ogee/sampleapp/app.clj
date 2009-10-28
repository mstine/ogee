
(ns ogee.sampleapp.app
 (:require
   [ogee.osgi :as osgi]))

(defn config-callback
  [props]
  (println "got config:" props))

(defn init []
  (println "app1 started")
  (osgi/add-configuration-handler "abc" config-callback))
