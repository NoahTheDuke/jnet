(ns engine.pipeline-test
  (:require [clojure.test :refer [deftest is]]
            [engine.steps.base-step :refer [make-base-step]]
            [engine.game :refer [new-game]]
            [engine.pipeline :as sut]))

(deftest queue-steps-test
  (let [game (new-game nil)
        step (make-base-step)]
    (is (= {:pipeline [] :queue [step]} (:gp (sut/queue-step game step)))
        "Step is correctly queued")
    (is (thrown? clojure.lang.ExceptionInfo (:gp (sut/queue-step game {})))
        "Step must be valid")
    ))
