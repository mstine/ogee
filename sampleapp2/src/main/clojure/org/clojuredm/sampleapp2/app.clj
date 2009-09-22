(ns org.clojuredm.sampleapp2.app)

(def context (atom nil))
 
(defn start [_]
  (println "starting clojure bundle2")
  ;((:hello (org.clojuredm.osgi/getService c "java.util.Map")))
  )

(defn stop [_]
  (println "stopping clojure bundle2"))


