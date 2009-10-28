
(ns ogee.cop
  (:use
    ogee.utils)
  (:require
    [clojure.contrib.str-utils :as str-utils]
    [clojure.contrib.logging :as logging]))

(def *active-layers* [])

(defn create-merged-name
  "Create a string that represents the merge of the specified layers.
   E.g. layer1->layer2->layer3"
  [layers]
  (str-utils/str-join "->" (map :name layers)))

(defn create-bindings
  "Merge the layers and return a map of all variations. The values in
   the map will be the actual values of the expressions specified when
   the layer was created."
  [layers]
  (let [merged (reduce merge (map :variations layers))
        applied (mapcat (fn [[k v]] [k (v)]) merged)]
    (apply hash-map applied)))

(defmacro deflayer
  "Create the layer layer-name. This macro defines a var with the same name.
   variations must be vector that contains the vars and their values. Same
   syntax as in let and binding."
  [layer-name variations]
  (let [layer-name-str (str layer-name)
        grouped (mapcat
                  (fn [p] [`(var ~(first p)) `(fn [] ~(second p))])
                  (partition 2 variations))]
    `(def ~layer-name {:name ~layer-name-str
                       :variations (hash-map ~@grouped)})))

(defmacro with-layers
  "Activate the layers listed in the vector layers and execute body. The scope
   of the active layers is limited to body."
  [layers & body]
  `(let [merged-name# (create-merged-name (concat *active-layers* ~layers))
         merged-variations# (create-bindings ~layers)]
     (binding [*active-layers* (concat *active-layers* ~layers)]
       (push-thread-bindings merged-variations#)
       (try
         ~@body
         (catch Throwable t#
           (throw (exception-add-text t# "Exception thrown. Active layers: " merged-name#)))
         (finally
           (pop-thread-bindings))))))

(defmacro impart
  "Partially apply function f with initargs and bind all currently active
   layers and bindings. Useful if the function will run in another thread
   but belongs to the current context."
  [f & initargs]
  `(let [current-thread-bindings# (get-thread-bindings)
         merged-name# (create-merged-name *active-layers*)]
     (fn [& args#]
       (push-thread-bindings current-thread-bindings#)
       (try
         (apply ~f (concat ~(vec initargs) args#))
         (catch Throwable t#
           (throw (exception-add-text t#
                    "Error while executing imparted function. Linked layers:" merged-name#)))
         (finally
           (pop-thread-bindings))))))

