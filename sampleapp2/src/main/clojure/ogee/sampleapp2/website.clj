(ns ogee.sampleapp2.website
  (:use compojure))

(defn template [body]
  (html 
    [:html
     [:head [:title "Start Page"]]
     [:body
      body
      ]]))

(defn root []
  (html
    [:h1 "Hello World"]
    "bla"
    ))

