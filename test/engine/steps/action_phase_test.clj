(ns engine.steps.action-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.prompt-state :as prompt-state]
   [engine.steps.action-phase :as sut]
   [engine.helper-test :refer [click-prompt]]))

(deftest action-window-prompt-test
  (is (= "You have 3 clicks. Choose an action."
         (-> (game/new-game nil)
             (assoc-in [:corp :clicks] 3)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (prompt-state/prompt-text :corp))))
  (is (= ""
         (-> (game/new-game nil)
             (assoc-in [:corp :clicks] 3)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (click-prompt :corp "[click]: Gain 1[c].")
             (prompt-state/prompt-text :corp)))))

(deftest action-window-buttons-test
  (is (= 1
         (-> (game/new-game nil)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (click-prompt :corp "[click]: Gain 1[c].")
             (:corp)
             (:credits))))
  (is (= -1
         (-> (game/new-game nil)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (click-prompt :corp "[click]: Gain 1[c].")
             (:corp)
             (:clicks)))))

(deftest action-phase-repeats
  (let [game (-> (game/new-game nil)
                 (assoc-in [:corp :clicks] 2)
                 (pipeline/queue-step (sut/action-phase))
                 (pipeline/continue-game)
                 (click-prompt :corp "[click]: Gain 1[c]."))]
    (is (= "You have 1 clicks. Choose an action." (prompt-state/prompt-text game :corp)))
    (is (= :phase/action (:current-phase game)))))
