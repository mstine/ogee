
(ns ogee.sampleapp2.app
 (:require
   [ogee.web :as web]
   [ogee.sampleapp2.website :as website]))

(defn init []
  (web/register-routes website/app-routes "/app2")
  (println "app2 started"))

