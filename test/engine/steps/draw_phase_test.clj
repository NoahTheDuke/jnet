(ns engine.steps.draw-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.draw-phase :as sut]
   [engine.test-utils :refer :all]))

(deftest draw-if-corp-test
  (let [game (-> (game/make-game
                   {:corp {:user {:username "Corp player"}
                           :deck-list [{:name "hedge-fund"
                                        :qty 1}]}})
                 (pipeline/queue-step (sut/make-draw-phase))
                 (pipeline/continue-game))]
    (is (= 1 (-> game
                 (get-in [:corp :hand])
                 (count))))
    (is (= [{:text [{:name "Corp player"} "draws 1 card for their mandatory draw."]}]
           (-> game
               (get-messages)))))
  (is (zero? (-> (game/make-game
                   {:runner {:deck-list [{:name "sure-gamble"
                                          :qty 1}]}})
                 (pipeline/queue-step (sut/make-draw-phase))
                 (pipeline/continue-game)
                 (get-in [:corp :hand])
                 (count)))))
