(ns org.ogee.sampleapp2.app
	(:use ogee))

(def context (atom nil))
 
(defn start [_]
  ((:hello (service-import @context "(:vendor=Roman)"))))

(defn stop [_])


