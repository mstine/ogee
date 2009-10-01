(ns ogee.sampleapp2.app
	(:use ogee)
	(:use [compojure :only (defroutes
												   GET
												   html
												   ANY
												   page-not-found
												   servlet)]) 
  (:require ogee.sampleapp2.util)
  )

(defroutes my-app
  (ANY "*"
    (html [:h1 "Hello World"])))

(defn start [ctx]
  (println "app2 started")
  (ogee.sampleapp2.util/abc)
  (register-servlet ctx (servlet my-app) "/myservlet") 
  ((:hello (smap-import ctx :app1service)))
  )

(defn stop [_]
	(println "app2 stopped"))
