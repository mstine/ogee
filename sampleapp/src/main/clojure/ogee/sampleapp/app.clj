(ns ogee.sampleapp.app
	(:require ogee.osgi))
 
(def context (atom nil))
 
(defn start [_]
	(println "app1 started")
  (ogee.osgi/smap-export @context :app1service {:hello #(println "Hello from sample1!")}))

(defn stop [_]
	(println "app1 stopped"))
