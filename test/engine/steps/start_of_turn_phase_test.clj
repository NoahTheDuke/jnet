(ns engine.steps.start-of-turn-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :refer [new-game start-new-game]]
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
  (let [game (new-game nil)]
    (is (= :phase/start-of-turn
           (-> game
               (queue-step (sut/start-of-turn-phase game))
               (continue-game)
               (second)
               (:current-phase))))))
