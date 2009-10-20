
(ns ogee.cop
  (:require
    [clojure.contrib.str-utils :as str-utils]))

(defmacro deflayer
  [layer-name variations]
  (let [layer-name-str# (str layer-name)
        grouped# (mapcat
                  (fn [p] [`(var ~(first p)) (second p)])
                  (partition 2 variations))]
    `(def ~layer-name {:name ~layer-name-str#
                       :variations (hash-map ~@grouped#)})))

(defmacro with-layers
  [layers & body]
  (let [layers# (eval layers)
        merged-name# (str-utils/str-join "->" (map :name layers#))
        merged-variations# (reduce merge (map :variations layers#))
        ]
    `(let []
       (push-thread-bindings ~merged-variations#)
       (try
         ~@body
         (finally
           (pop-thread-bindings))))))



; TESTS
(def *a*)
(def *b*)
(def *c*)
(deflayer l1 [*a* 111])
(deflayer l2 [*b* 222])
(deflayer l3 [*c* 333])
(println "------------------------------")
(with-layers [l1 l2 l3]
  (println *a*)
  )
