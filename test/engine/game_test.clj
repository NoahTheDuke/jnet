(ns engine.game-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as sut]
   [engine.test-utils :refer :all]))

(deftest new-game-test
  (testing "pipeline exists"
    (is (:gp (sut/make-game {}))))
  (testing "initial turns set"
    (is (:turns (sut/make-game {})))))

(deftest corp-turn-test
  (is (= 6 (-> (sut/start-new-game {:corp {:user {:username "Corp player"}
                                           :deck [:a :b :c :d :e :f :g :h :i :j]}})
               (click-prompt :corp "Keep")
               (click-prompt :runner "Keep")
               (get-in [:corp :hand])
               (count)))))

(deftest switch-active-player-test
  (is (= :runner
         (-> (new-game)
             (click-prompt :corp "Keep")
             (click-prompt :runner "Keep")
             (click-prompt :corp "[click]: Gain 1[c].")
             (click-prompt :corp "[click]: Gain 1[c].")
             (click-prompt :corp "[click]: Gain 1[c].")
             (click-prompt :corp "Card 1")
             (prompt-fmt :corp)
             (:active-player)))))
