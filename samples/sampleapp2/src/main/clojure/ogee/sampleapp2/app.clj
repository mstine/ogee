
(ns ogee.sampleapp2.app
 (:require
   [ogee.web :as web]
   [ogee.sampleapp2.website :as website]))

(defn start []
  (web/register-routes website/app-routes "/app2")
  (println "app2 started"))

(defn stop []
  (println "app2 stopped"))

