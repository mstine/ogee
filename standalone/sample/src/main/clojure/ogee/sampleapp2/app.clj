
(ns ogee.sampleapp2.app
 (:require
   [ogee.web :as web]
   [ogee.osgi :as osgi]
   [ogee.sampleapp2.website :as website]))

(defn config-callback
  [props]
  (println "got config:" props))

(defn init []
  (osgi/add-configuration-handler "abc" config-callback)
  (let [servlet (web/servlet-from-routes website/app-routes "/app2")]
    (osgi/register-servlet servlet "/app2")
    (println "app2 started")))
