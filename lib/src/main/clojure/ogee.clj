(ns ogee
	(:require [clojure.contrib.logging :as logging]))
					 
(defn ogee-start []
	(logging/info "Ogee started."))

(defn ogee-stop []
	"Shutdown the Ogee runtime."
	(logging/info "Stopping Ogee..."))

(defn- inject-bundle-context
	"Initializes a module. The atom name-ns/name-var will be set to the BundleContext, if present."
	[name-ns name-var context]
	(let [context-atom ((ns-interns (symbol name-ns)) (symbol name-var))]
		(if (not= context-atom nil) (reset! @context-atom context))))
	
(defn module-added
	"Activate the clojure module. name-ns/name-var specify the var where the BundleContext should be injected."
	[name-ns name-var context]
	(inject-bundle-context name-ns name-var context))

