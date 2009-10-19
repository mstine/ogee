
(ns ogee.web
  (:require
    [clojure.contrib.def :as cdef]
    compojure))

(cdef/defvar- *register-servlet-method*)

(defn set-servlet-method
  [m]
  ; TODO If already defined, throw exception
  (cdef/defvar- *register-servlet-method* m))

(defn register-routes
  [routes url]
  (let [decorated (compojure/with-context routes url)
        servlet (compojure/servlet decorated)]
    (*register-servlet-method* servlet url)))
