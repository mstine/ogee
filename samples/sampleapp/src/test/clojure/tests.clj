
(ns tests
  (:require 
    ogee.sampleapp.app)
  (:use
    clojure.test))


(deftest simple-test
  (is (= 1 1))
  (is (= 2 2)))


(run-tests)
