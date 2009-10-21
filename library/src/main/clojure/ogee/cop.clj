
(ns ogee.cop
  (:require
    [clojure.contrib.str-utils :as str-utils]
    [clojure.contrib.logging :as logging]))

(def *active-layers*)

(defmacro deflayer
  [layer-name variations]
  (let [layer-name-str (str layer-name)
        grouped (mapcat
                  (fn [p] [`(var ~(first p)) (second p)])
                  (partition 2 variations))]
    `(def ~layer-name {:name ~layer-name-str
                       :variations (hash-map ~@grouped)})))

(defmacro with-layers
  [layers & body]
  `(let [merged-name# (str-utils/str-join "->" (map :name ~layers))
         merged-variations# (reduce merge (map :variations ~layers))]
     (push-thread-bindings merged-variations#)
     (binding [*active-layers* ~layers] ; TODO merge with already active layers
       (try
         ~@body
         (catch Throwable t#
           (logging/error (str "Exception thrown. Active layers: " merged-name#) t#)
           (throw t#))
         (finally
           (pop-thread-bindings))))))

(defmacro impart
  [f & initargs]
  `(let [current-layers# *active-layers*]
     (fn [& args#]
       (with-layers current-layers#
         (apply ~f (concat ~(vec initargs) args#)))))
  )

(def *a*)
(deflayer l1 [*a* 1])
(defn pi [a b] (println a "and" b ":" *a*))
(with-layers [l1]
  (doall (map (partial pi "a") ["b"]))
  (doall (pmap (impart pi "x") ["y"]))
  ;(impart pi)
  )


