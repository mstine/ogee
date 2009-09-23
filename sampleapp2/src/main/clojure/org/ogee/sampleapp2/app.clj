(ns org.ogee.sampleapp2.app
	(:use ogee))

(defn start [ctx]
  ((:hello (service-import ctx :app1service))))

(defn stop [_])


