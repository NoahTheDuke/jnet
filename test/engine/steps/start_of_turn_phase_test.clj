(ns engine.steps.start-of-turn-phase-test
  (:require
    [clojure.test :refer [deftest is]]
    [engine.game :refer [new-game start-new-game]]
    [engine.player :refer [new-corp new-runner]]
    [engine.pipeline :refer [continue-game queue-step]]
    [engine.steps.start-of-turn-phase :as sut]
    [engine.test-helper :refer [click-prompt]]))

(deftest active-player-test
  (is (= :corp
         (-> (start-new-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}
                              :runner {:deck [:a :b :c :d :e :f :g :h :i]}})
             (click-prompt :corp "Keep")
             (click-prompt :runner "Keep")
             (:active-player)))))

(deftest correct-phase-test
  (is (= :phase/start-of-turn
         (-> (new-game nil)
             (queue-step (sut/start-of-turn-phase))
             (continue-game)
             (second)
             (:current-phase)))))

(deftest gain-allotted-clicks-test
  (is (= (:clicks-per-turn (new-corp nil))
         (-> (new-game nil)
             (assoc :active-player :corp)
             (queue-step (sut/gain-allotted-clicks))
             (continue-game)
             (second)
             (get-in [:corp :clicks]))))
  (is (= (:clicks-per-turn (new-runner nil))
         (-> (new-game nil)
             (assoc :active-player :runner)
             (queue-step (sut/gain-allotted-clicks))
             (continue-game)
             (second)
             (get-in [:runner :clicks])))))
