(ns org.ogee.sampleapp2.app
  (:use ogee)
  (:require org.ogee.sampleapp2.util))

(defn start [ctx]
  (println "a")
  (org.ogee.sampleapp2.util/abc)
  ((:hello (smap-import ctx :app1service))))

(defn stop [_]
	(println "app2 stopped"))



