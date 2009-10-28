
(ns ogee.war.sample.app
  (:use cascade)
  (:import (java.util Date)))

(defview itworks
  {:path "working"}
  [env]
  :html [
         :head [ :title [ "It Works!"]]
         :body [
                :h1 [ "It Works!"]

                :p [
                    "Current date and time: "
                    :span { :id "time"} [ (.toString (Date.)) ]
                    ]

                :p [
                    "Let's count:"
                    :ul [
                         (for [x (range 10 0 -1)]
                           (template :li [ (str x) " ..." ] )
                           )
                         ]
                    ]

                :hr

                (render-link env itworks ["extra"] { :class "nav" } "refresh")
                ]
         ])

(declare show-counter)

(defaction increment-count
  {:path "count/increment"}
  [env]
  [count :int]
  (send-redirect env (link env show-counter [(inc count)])))

(defaction adjust-count
  {:path "count/adjust"}
  [env]
  [op [:operation :str]
   count [:count :int]]
  (let [new-value (condp = op
                    "reset" 1
                    "dec" (dec count))]
    (send-redirect env (link env show-counter [new-value]))))

(defn page-template
  [env title body-block]
  (template
    :html [
           :head [ :title title ]
           :body [
                  :h1 [ title ]
                  (body-block env)
                  ]
           ]))


(defview show-counter
  {:path "count/current"}
  [env]
  [count :int]
  (page-template env "Current Count"
    (block [env]
      :p [
          "The current count is: "
          :strong {:id "current"} [ count ]
          "."
          ]
      :p [
          "Click "
          (render-link env increment-count [ count ] "here to increment")
          "."
          ]
      :p [
          "Click "
          (render-link env adjust-count {:operation :reset} "here to reset")
          "."
          ]
      :p [
          "Click "
          (render-link env adjust-count {:operation :dec :count count} "here to decrement")
          "."
          ])))

(defview fail-view
  {:path "force-failure"}
  [env]
  (.setAttribute (-> env :servlet-api :request) "an-attribute" "a value")
  (.. (-> env :servlet-api :request) (getSession true) (setAttribute "my-session-attribute" "my-session-value"))
  (page-template env "Force Failure"
    (block [env]
      :p [
          "Five / 0 ="
          :strong [ (/ 5 0) ]
          ])))

(defview index-view
  {:path ""}
  [env]
  (page-template env "Test Application"
    (block [env]
      :ul [
           :li [ (render-link env itworks "Simple Output") ]
           :li [ (render-link env fail-view "Forced Exception") ]
           :li [ (render-link env show-counter [0] "Simple Actions") ]
           ])))


