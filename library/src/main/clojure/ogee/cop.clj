
(ns ogee.cop
  (:require
    [clojure.contrib.str-utils :as str-utils]
    [clojure.contrib.logging :as logging]))

(def *active-layers* [])

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
     (binding [*active-layers* (concat *active-layers* ~layers)]
       (try
         ~@body
         (catch Throwable t#
           (logging/error (str "Exception thrown. Active layers: " merged-name#) t#)
           (throw t#))
         (finally
           (pop-thread-bindings))))))

(defmacro impart
  [f & initargs]
  `(let [current-thread-bindings# (get-thread-bindings)
         current-layers# *active-layers*]
     (fn [& args#]
       (push-thread-bindings current-thread-bindings#)
       (try
         (with-layers current-layers#
           (apply ~f (concat ~(vec initargs) args#)))
         (finally (pop-thread-bindings))))))
