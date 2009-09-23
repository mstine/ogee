(ns org.ogee.sampleapp2.app
	(:use ogee))

(def context (atom nil))
 
(defn start [_]
  ((:hello (service-import @context :app1service))))

(defn stop [_])


