(ns engine.steps.handler-prompt-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.draw :as draw]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.prompt-state :as prompt-state]
   [engine.steps.handler-prompt :as sut]
   [engine.test-utils :refer :all]))

(deftest prompt-with-handlers-test
  (let [step (sut/handler-prompt
               {:active-condition :corp
                :active-text "How many to draw?"
                :waiting-text "Corp to draw"
                :choices {"Draw 1" (fn [game]
                                     (draw/draw game :corp 1))
                          "Draw 2" (fn [game]
                                     (draw/draw game :corp 2))}})]
    (is (= "How many to draw?"
           (-> (game/make-game nil)
               (pipeline/queue-step step)
               (pipeline/continue-game)
               (prompt-state/prompt-text :corp))))
    (is (= "Corp to draw"
           (-> (game/make-game nil)
               (pipeline/queue-step step)
               (pipeline/continue-game)
               (prompt-state/prompt-text :runner))))
    (is (= 2
           (-> (game/make-game {:corp {:deck-list (a-deck :corp)}})
               (pipeline/queue-step step)
               (pipeline/continue-game)
               (click-prompt :corp "Draw 2")
               (get-in [:corp :hand])
               (count))))))
