(ns org.ogee.sampleapp2.app)

(def context (atom nil))
 
(defn start [_]
  (println "starting clojure bundle2")
  ;((:hello (org.ogee.osgi/getService c "java.util.Map")))
  )

(defn stop [_]
  (println "stopping clojure bundle2"))


