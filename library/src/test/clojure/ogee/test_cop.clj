
(ns ogee.test-cop
  (:require 
    [ogee.cop :as cop])
  (:use
    clojure.test))

(def *var1*) ; var used in layer 1
(def *var2*) ; var used in layer 1
(cop/deflayer layer1 [*var1* 1
                      *var2* 2])
(cop/deflayer layer1-override [*var1* 11])

(def *var3*) ; var used in layer 2
(cop/deflayer layer2 [*var3* 3])

(def *var-global*) ; var used for e.g. bindings

(deftest create-layer
  (is (map? layer1))
  (is (= (:name layer1) "layer1"))
  (is (map? (:variations layer1)))
  (is (fn? ((:variations layer1) #'*var1*)))
  (is (fn? ((:variations layer1) #'*var2*))))

(deftest with-single-layer
  (cop/with-layers [layer1]
    (is (= *var1* 1))
    (is (= *var2* 2))))

(deftest with-multiple-layers
  (cop/with-layers [layer1 layer2]
    (is (= *var1* 1))
    (is (= *var2* 2))
    (is (= *var3* 3))))

(deftest with-nested-layers
  (cop/with-layers [layer1]
    (cop/with-layers [layer2]
      (is (= *var1* 1))
      (is (= *var2* 2))
      (is (= *var3* 3)))))

(deftest with-multiple-layers-validate-overrides
  (cop/with-layers [layer1 layer1-override]
    (is (= *var1* 11))))

(deftest impart-function
  (cop/with-layers [layer1]
    (is (thrown? RuntimeException (first (pmap (fn [_] *var1*) [0]))))
    (is (= 1 (first (pmap (cop/impart (fn [_] *var1*)) [0]))))))

(deftest impart-function-should-capture-bindings
  (binding [*var-global* "X"]
    (cop/with-layers [layer1]
      (is (thrown? RuntimeException (first (pmap (fn [_] *var-global*) [0]))))
      (is (= "X" (first (pmap (cop/impart (fn [_] *var-global*)) [0])))))))

(deftest nested-layers-are-stacked
  (cop/with-layers [layer1]
    (cop/with-layers [layer2]
      (is (= (count cop/*active-layers*) 2)))))

(run-tests)






