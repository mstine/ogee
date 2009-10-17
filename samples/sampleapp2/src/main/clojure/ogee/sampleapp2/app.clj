(ns ogee.sampleapp2.app
 (:use [compojure :only (defroutes GET ANY page-not-found servlet)])
 (:require
   [ogee.sampleapp2.website :as website]
   [ogee.web :as web]))


(defn start []
  (web/register-routes website/app-routes "/app2")
  (println "app2 started"))

(defn stop []
  (println "app2 stopped"))

