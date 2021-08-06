(ns engine.steps.base-step-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.steps.base-step :refer [make-base-step map->BaseStep]]
   [engine.steps.step-protocol :refer [complete? continue validate]])
  (:import [engine.steps.base_step BaseStep]))

(deftest base-step-tests
  (is (instance? BaseStep (make-base-step nil)))
  (is (thrown? clojure.lang.ExceptionInfo (validate (map->BaseStep {}))))
  (let [continue-fn (constantly [true false])
        step (make-base-step {:continue continue-fn})]
    (is (= [true false] (continue step {}))))
  (let [continue-fn (fn [step game] [step game])
        game {:a 1}
        step (make-base-step {:continue continue-fn})]
    (is (= [step game] (continue step game))))
  (is (not (complete? (make-base-step nil)))))
