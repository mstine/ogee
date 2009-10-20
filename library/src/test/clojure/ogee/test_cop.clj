
(ns ogee.test-cop
  (:require 
    [ogee.cop :as cop])
  (:use
    clojure.test
    clojure.contrib.def))

(defvar *var1* "Var used in tests")
(defvar *var2* "Var used in tests")
(cop/deflayer layer1 [*var1* 1
                          *var2* 2])

(deftest create-layer
  (is (var? #'layer1))
  (is (map? layer1))
  (is (= (:name layer1) "layer1"))
  (is (map? (:variations layer1)))
  (is (= ((:variations layer1) #'*var1*) 1))
  (is (= ((:variations layer1) #'*var2*) 2)))




;(cop/with-layers [layer1]
;  (println "XXX"))



(run-tests)


