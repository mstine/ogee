(ns org.ogee.sampleapp2.app
	(:use ogee))

(defn start [ctx]
	(println "Hello Todor")
  ((:hello (smap-import ctx :app1service))))

(defn stop [_])


