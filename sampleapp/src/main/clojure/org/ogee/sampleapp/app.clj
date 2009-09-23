(ns org.ogee.sampleapp.app
	(:use ogee))
 
(def context (atom nil))
 
(defn start [_]
  (service-export @context :app1service {:hello #(println "Hello from sample1!")}))

(defn stop [_])

