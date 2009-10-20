
(ns ogee.cop)

(defmacro create-layer
  [layer-name bhs]
  (let [grouped (mapcat
                  (fn [p] [`(var ~(first p)) (second p)])
                  (partition 2 bhs))]
    `(def ~layer-name (hash-map ~@grouped))))
