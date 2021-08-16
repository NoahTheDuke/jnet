(ns engine.steps.start-of-turn-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.player :as player]
   [engine.steps.start-of-turn-phase :as sut]
   [engine.test-utils :refer [click-prompt]]))

(deftest active-player-test
  (is (= :corp
         (-> (game/start-new-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}
                                   :runner {:deck [:a :b :c :d :e :f :g :h :i]}})
             (click-prompt :corp "Keep")
             (click-prompt :runner "Keep")
             (:active-player)))))

(deftest gain-allotted-clicks-test
  (is (= (:clicks-per-turn (player/new-corp nil))
         (-> (game/make-game nil)
             (pipeline/queue-step (sut/gain-allotted-clicks))
             (pipeline/continue-game)
             (get-in [:corp :clicks]))))
  (is (= (:clicks-per-turn (player/new-runner nil))
         (-> (game/make-game nil)
             (assoc :active-player :runner)
             (pipeline/queue-step (sut/gain-allotted-clicks))
             (pipeline/continue-game)
             (get-in [:runner :clicks])))))
