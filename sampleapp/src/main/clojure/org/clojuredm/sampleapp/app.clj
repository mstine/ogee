(ns org.clojuredm.sampleapp.app
	(:use clojuredm.osgi))
 
(def context (atom nil))
 
(defn hello []
	(println "HELLO"))
 
(defn start [_]
  (println "starting clojure bundle")
;  (service-export context {:hello hello})
  )

(defn stop [_]
  (println "stopping clojure bundle"))

