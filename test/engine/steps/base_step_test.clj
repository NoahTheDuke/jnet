(ns engine.steps.base-step-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.steps.step :as sut]))

(deftest base-step-test
  (is (thrown? clojure.lang.ExceptionInfo (sut/validate (sut/map->BaseStep {}))))
  (let [continue-fn (constantly false)
        step (sut/make-base-step {:continue-step continue-fn})]
    (is (= false (sut/continue-step step {}))))
  (let [continue-fn (fn [step game] [step game])
        game {:a 1}
        step (sut/make-base-step {:continue-step continue-fn})]
    (is (= [step game] (sut/continue-step step game)))
    (is (not (sut/blocking (sut/make-base-step nil) game)))))

(deftest simple-step-test
  (is (= :foo
         (sut/continue-step
           (sut/simple-step (fn [game] game))
           :foo))))
