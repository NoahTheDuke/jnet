(ns engine.steps.base-step-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.steps.base-step :as sut]
   [engine.steps.step-protocol :refer [complete? continue-step validate]]))

(deftest base-step-test
  (is (thrown? clojure.lang.ExceptionInfo (validate (sut/map->BaseStep {}))))
  (let [continue-fn (constantly [true false])
        step (sut/make-base-step {:continue-step continue-fn})]
    (is (= [true false] (continue-step step {}))))
  (let [continue-fn (fn [step game] [step game])
        game {:a 1}
        step (sut/make-base-step {:continue-step continue-fn})]
    (is (= [step game] (continue-step step game))))
  (is (not (complete? (sut/make-base-step nil)))))

(deftest simple-step-test
  (is (= [true :foo]
         (continue-step
           (sut/simple-step (fn [game] game))
           :foo))))
