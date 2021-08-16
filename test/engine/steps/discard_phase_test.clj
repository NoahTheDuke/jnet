(ns engine.steps.discard-phase-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.draw :as draw]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.prompt-state :as prompt-state]
   [engine.steps.discard-phase :as sut]
   [engine.test-utils :refer :all]))

(deftest discard-phase-test
  (let [game (-> (game/make-game {:corp {:user {:username "Corp player"}
                                         :deck-list (a-deck :corp)}})
                 (draw/draw :corp 5)
                 (pipeline/queue-step (sut/discard-phase)))]
    (testing "Only display discard prompt if over hand size"
      (is (= "" (-> game
                    (pipeline/continue-game)
                    (prompt-state/prompt-text :corp))))
      (is (= "Select cards to discard"
             (-> game
                 (draw/draw :corp 1)
                 (pipeline/continue-game)
                 (prompt-state/prompt-text :corp)))))
    ; (testing "Selecting cards discards them"
    ;   (is (= [:a]
    ;          (-> game
    ;              (draw/draw :corp 1)
    ;              (pipeline/continue-game)
    ;              (prompt-state/prompt-text :corp)))))
    (testing "Player loses reamining clicks"
      (is (zero?
            (-> game
                (assoc-in [:corp :clicks] 2)
                (pipeline/continue-game)
                (get-in [:corp :clicks]))))
      (is (= [{:text [{:name "Corp player"} "loses their" 2 "remaining clicks."]}]
             (-> game
                 (assoc-in [:corp :clicks] 2)
                 (pipeline/continue-game)
                 (get-messages)))))))
