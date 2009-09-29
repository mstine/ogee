(ns ogee.sampleapp.app
	(:use ogee))
 
(def context (atom nil))
 
(defn start [_]
  (smap-export @context :app1service {:hello #(println "Hello from sample1!")}))

(defn stop [_])

(defn hello [] (println "Hello"))
