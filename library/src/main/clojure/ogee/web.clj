
(ns ogee.web
  (:require
    [clojure.contrib.def :as cdef]
    compojure))

(defn servlet-from-routes
  [routes url]
  (let [decorated (compojure/with-context routes url)
        servlet (compojure/servlet decorated)]
    servlet))
