(ns org.ogee.sampleapp2.app
	(:use ogee)
	(:require org.ogee.sampleapp.app))

(defn start [ctx]
	(org.ogee.sampleapp.app/hello)
  ((:hello (smap-import ctx :app1service))))

(defn stop [_])


