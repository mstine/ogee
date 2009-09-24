(ns org.ogee.sampleapp2.app
	(:use ogee)
	(:require org.ogee.sampleapp.app))

(defn start [ctx]
	(org.ogee.sampleapp.app/hello)
  ((:hello (service-import ctx :app1service)))
  )

(defn stop [_])


