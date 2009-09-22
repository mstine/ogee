(ns org.clojuredm.sampleapp.app)
 
 
(defn hello []
	(println "HELLO"))
 
 
(defn start [c]
  (println "starting clojure bundle" c)
  (org.clojuredm.osgi/registerService c "java.util.Map" {:hello hello})
  )

(defn stop [c]
  (println "stopping clojure bundle" c))


