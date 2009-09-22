(ns org.clojuredm.sampleapp.app)
 
(defn start [c]
  (println "starting clojure bundle2" c)
  (println (:hello (org.clojuredm.osgi/getService c "java.util.Map")))
;  (println ((get (org.clojuredm.osgi/getService c "java.util.Map") "hello")))
  )

(defn stop [c]
  (println "stopping clojure bundle2" c))


