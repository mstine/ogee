(ns ogee.sampleapp2.app
  (:use 
  			ogee
  			compojure
  			)
  (:require ogee.sampleapp2.util))



;(defroutes my-app
  ;(GET "/"
    ;(html [:h1 "Hello World"]))
  ;(ANY "*"
    ;(page-not-found)))



(defn start [ctx]
  (ogee.sampleapp2.util/abc)
  (println "app2 started")
  ;(println "XXX" (servlet my-app))
  ((:hello (smap-import ctx :app1service)))
  )

(defn stop [_]
	(println "app2 stopped"))



