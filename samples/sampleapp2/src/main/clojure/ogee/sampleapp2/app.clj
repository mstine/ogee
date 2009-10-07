(ns ogee.sampleapp2.app
  (:use [compojure :only (defroutes GET ANY page-not-found servlet)]) 
  (:require
     [ogee.sampleapp2.website :as website]
     ogee.osgi))


(def servlet-root "/app2")
(defn url [path] (str servlet-root path))

(defroutes my-app
           (GET (url "/start")
                (website/root params))
           (ANY "*"
                (page-not-found)))

(defn start []
  (println "app2 started")
  (ogee.osgi/register-servlet (servlet my-app) servlet-root)
  )

(defn stop []
  (println "app2 stopped"))

