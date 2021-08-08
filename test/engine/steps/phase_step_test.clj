(ns engine.steps.phase-step-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.steps.phase-step :as sut]
   [engine.steps.step-protocol :refer [complete? continue-step validate]])
  (:import
   [engine.steps.phase_step PhaseStep]))

(deftest phase-step-test
  (is (instance? PhaseStep (sut/make-phase-step nil)))
  (is (thrown? clojure.lang.ExceptionInfo (validate (sut/map->PhaseStep {}))))
  (let [continue-fn (constantly [true false])
        step (sut/make-phase-step {:continue-step continue-fn})]
    (is (= [true false] (continue-step step {}))))
  (let [continue-fn (fn [step game] [step game])
        game {:a 1}
        step (sut/make-phase-step {:continue-step continue-fn})]
    (is (= [step game] (continue-step step game))))
  (is (not (complete? (sut/make-phase-step nil)))))
