(ns clojuredm.osgi
	(:import (org.clojuredm osgi)))
	
(defn init-module
	[name-ns name-var context]
	(let [atm ((ns-interns (symbol name-ns)) (symbol name-var))]
		(if (not= atm nil)
			(reset! (deref atm) context))))
	
(defn service-export
	[context service]
	(org.clojuredm.osgi/registerService context "java.util.Map" service))
