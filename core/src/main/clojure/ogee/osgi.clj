(ns ogee.osgi
	(:import (org.ogee osgi)))
	
(defn init-module
	[name-ns name-var context]
	(let [atm ((ns-interns (symbol name-ns)) (symbol name-var))]
		(if (not= atm nil)
			(reset! (deref atm) context))))
	
(defn service-export
	[context service]
	(org.ogee.osgi/registerService context "java.util.Map" service))
