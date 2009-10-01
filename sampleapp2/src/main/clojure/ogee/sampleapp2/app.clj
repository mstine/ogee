(ns ogee.sampleapp2.app
	(:use [compojure :only (defroutes GET html ANY page-not-found servlet)]) 
  (:require
  	ogee.sampleapp2.util
  	ogee.osgi))

(defroutes my-app
  (ANY "*"
    (html [:h1 "Hello World"])))

(defn start [ctx]
  (println "app2 started")
  (ogee.sampleapp2.util/abc)
  (ogee.osgi/register-servlet ctx (servlet my-app) "/myservlet") 
  ((:hello (ogee.osgi/smap-import ctx :app1service)))
  )

(defn stop [_]
	(println "app2 stopped"))
