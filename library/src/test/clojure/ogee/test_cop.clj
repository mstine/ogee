
(ns ogee.test-cop
  (:require 
    [ogee.cop :as cop])
  (:use
    clojure.test))

(def *var1*) ; var used in layer 1
(def *var2*) ; var used in layer 1
(cop/deflayer layer1 [*var1* 1 *var2* 2])
(cop/deflayer layer1-override [*var1* 11])

(def *var3*) ; var used in layer 2
(cop/deflayer layer2 [*var3* 3])

(deftest create-layer
  (is (map? layer1))
  (is (= (:name layer1) "layer1"))
  (is (map? (:variations layer1)))
  (is (= ((:variations layer1) #'*var1*) 1))
  (is (= ((:variations layer1) #'*var2*) 2)))

(deftest with-single-layer
  (cop/with-layers [layer1]
    (is (= *var1* 1))
    (is (= *var2* 2))))

(deftest with-multiple-layers
  (cop/with-layers [layer1 layer2]
    (is (= *var1* 1))
    (is (= *var2* 2))
    (is (= *var3* 3))))

(deftest with-multiple-layers-validate-overrides
  (cop/with-layers [layer1 layer1-override]
    (is (= *var1* 11))
    (is (= *var2* 2))))

(deftest method-bind
  (cop/with-layers [layer1]
    (let [test-fn #(first (map (fn [_] *var1*) [1]))]
      (println (test-fn))
      ;(is (= @(test-fn) 1))

      )))




(run-tests)


