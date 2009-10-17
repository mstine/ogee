
(ns ogee.sampleapp2.website
  (:use compojure))

(defn default-page [title & body]
  (html
    [:html
     [:head [:title title]]
     [:body
      body
      ]]))

(defn bookmark-form [params]
  (form-to
    [:get "start"]
    (label :url   "URL:")   (text-field :url (:url params)) [:br]
    (label :title "Title:") (text-field :title (:title params)) [:br]
    (submit-button "Send") [:br]
    ))

(defn root [params]
  (default-page
    "Start Page"
    [:h1 "Hello World"]
    (bookmark-form params)
    ))

(defroutes app-routes
  (GET "/start"
    (root params))
  (ANY "*"
    (page-not-found)))
