(ns engine.steps.action-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.player :as player]
   [engine.prompt-state :as prompt-state]
   [engine.steps.action-phase :as sut]
   [engine.test-helper :refer [click-prompt]]))

(deftest action-phase-prompt-test
  (is (= "You have 3 clicks. Choose an action."
         (-> (game/new-game nil)
             (pipeline/queue-step (sut/action-phase :corp))
             (pipeline/continue-game)
             (second)
             (prompt-state/prompt-text :corp)))))

(deftest action-phase-buttons-test
  (is (= 1
         (-> (game/new-game nil)
             (pipeline/queue-step (sut/action-phase :corp))
             (pipeline/continue-game)
             (second)
             (click-prompt :corp "[click] Gain 1[c].")
             (:corp)
             (player/credits)))))
