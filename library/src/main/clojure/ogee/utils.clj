(ns ogee.utils
	(:require [clojure.contrib.logging :as logging])
	(:import (java.lang.reflect InvocationHandler Proxy)))
					 
(defn map-to-hashtable
	"Convert a map to a hashtable."
	[inmap]
	(let [table (java.util.Hashtable.)]
		(doseq [k (keys inmap)] (.put table k (get inmap k)))
		table))

(defn aop-proxy
	"Create a proxy for the type forclass. All method calls are delegated to callback.
	The callback function must be compatible to java.lang.reflect.InvocationHandler.invoke(...)."
	[forclass callback]
	(let [handler (proxy [InvocationHandler] [] (invoke [obj mth args] (callback obj mth args)))]
		(Proxy/newProxyInstance (ClassLoader/getSystemClassLoader) (into-array [forclass]) handler)))

