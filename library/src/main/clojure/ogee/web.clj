
(ns ogee.web
  (:require
    compojure))

(def register-servlet-method (ref nil))

(defn register-routes
  [routes url]
  (let [decorated (compojure/with-context routes url)
        servlet (compojure/servlet decorated)]
    (register-servlet-method servlet url)))
