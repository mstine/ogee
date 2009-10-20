
(ns ogee.test.cop
  (:require 
    [ogee.cop :as cop])
  (:use
    clojure.test))

(def *var1*)
(def *var2*)

(deftest layer-creation
  (cop/create-layer layer1 [*var1* 1
                            *var2* 2])
  (is (var? #'layer1))
  (is (map? layer1))
  (is (= (layer1 #'*var1*) 1))
  (is (= (layer1 #'*var2*) 2)))

(run-tests)


