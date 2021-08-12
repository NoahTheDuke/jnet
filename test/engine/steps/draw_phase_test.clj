(ns engine.steps.draw-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.draw-phase :as sut]
   [engine.test-helper :refer [get-messages]]))

(deftest draw-if-corp-test
  (let [game (-> (game/new-game {:corp {:user {:username "Corp player"}
                                        :deck ["Hedge Fund"]}})
                 (pipeline/queue-step (sut/draw-phase))
                 (pipeline/continue-game)
                 (second))]
    (is (= 1 (-> game
                 (get-in [:corp :hand])
                 (count))))
    (is (= [{:text [{:name "Corp player"} "draws 1 card for their mandatory draw."]}]
           (-> game
               (get-messages)))))
  (is (= 0 (-> (game/new-game {:runner {:deck ["Sure Gamble"]}})
               (pipeline/queue-step (sut/draw-phase))
               (pipeline/continue-game)
               (second)
               (get-in [:corp :hand])
               (count)))))
