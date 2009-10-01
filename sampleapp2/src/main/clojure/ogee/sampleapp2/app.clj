(ns ogee.sampleapp2.app
	(:use ogee ogee.sampleapp2.util)
	;compojure
  ;(:require ogee.sampleapp2.util)
  )

;(defroutes my-app
  ;(GET "/"
    ;(html [:h1 "Hello World"]))
  ;(ANY "*"
    ;(page-not-found)))



(defn start [ctx]
  (println "app2 started")
  (abc)
  ;(ogee.sampleapp2.util/abc)
  ;(println "XXX" (servlet my-app))
  ((:hello (smap-import ctx :app1service)))
  )

(defn stop [_]
	(println "app2 stopped"))
