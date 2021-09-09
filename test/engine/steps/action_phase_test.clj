(ns engine.steps.action-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.prompt-state :as prompt-state]
   [engine.steps.action-phase :as sut]
   [engine.test-utils :refer [click-prompt]]))

(deftest action-window-prompt-test
  (is (= "You have 3 clicks. Choose an action."
         (-> (game/make-game nil)
             (assoc-in [:corp :clicks] 3)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (prompt-state/prompt-text :corp))))
  (is (= ""
         (-> (game/make-game nil)
             (assoc-in [:corp :clicks] 3)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (click-prompt :corp "[click]: Gain 1[c].")
             (prompt-state/prompt-text :corp)))))

(deftest action-window-buttons-test
  (is (= 1
         (-> (game/make-game nil)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (click-prompt :corp "[click]: Gain 1[c].")
             (:corp)
             (:credits))))
  (is (= -1
         (-> (game/make-game nil)
             (pipeline/queue-step (sut/action-window))
             (pipeline/continue-game)
             (click-prompt :corp "[click]: Gain 1[c].")
             (:corp)
             (:clicks)))))

(deftest action-phase-repeats
  (let [game (-> (game/make-game nil)
                 (assoc-in [:corp :clicks] 2)
                 (pipeline/queue-step (sut/make-action-phase))
                 (pipeline/continue-game)
                 (click-prompt :corp "[click]: Gain 1[c]."))]
    (is (= "You have 1 clicks. Choose an action." (prompt-state/prompt-text game :corp)))
    (is (= :phase/action (:current-phase game)))))
