(ns ogee.sampleapp.app
	;(:use ogee)
	)
 
(def context (atom nil))
 
(defn start [_]
	(println "app1 started")
  ;(smap-export @context :app1service {:hello #(println "Hello from sample1!")})
  )

(defn stop [_]
	(println "app1 stopped"))

