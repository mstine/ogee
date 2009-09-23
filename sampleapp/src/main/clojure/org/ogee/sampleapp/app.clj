(ns org.ogee.sampleapp.app
	(:use ogee))
 
(def context (atom nil))
 
(defn start [_]
  (service-export @context {:hello #(println "Hello from sample1!")} {:vendor "Roman"}))

(defn stop [_])

