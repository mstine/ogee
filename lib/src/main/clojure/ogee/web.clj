
(ns ogee.web
  (:require
    compojure
    [ogee.osgi :as osgi]))

(defn register-routes
  [routes url]
  (let [decorated (compojure/with-context routes url)
        servlet (compojure/servlet decorated)]
    (osgi/register-servlet servlet url)))



