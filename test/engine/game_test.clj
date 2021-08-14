(ns engine.game-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as sut]
   [engine.helper-test :refer [click-prompt prompt-fmt]]))

(deftest new-game-test
  (testing "pipeline exists"
    (is (:gp (sut/new-game {}))))
  (testing "initial turns set"
    (is (:turns (sut/new-game {})))))

(deftest start-new-game
  (is (= [:a :b :c]
         (-> (sut/new-game {:corp {:deck [:a :b :c]}})
             (get-in [:corp :deck])))))

(deftest corp-turn-test
  (is (= 6 (-> (sut/start-new-game {:corp {:user {:username "Corp player"}
                                           :deck [:a :b :c :d :e :f :g :h :i :j]}})
               (click-prompt :corp "Keep")
               (click-prompt :runner "Keep")
               (get-in [:corp :hand])
               (count))))
  (is (= :runner
         (-> (sut/start-new-game {:corp {:user {:username "Corp player"}
                                         :deck [:a :b :c :d :e]}})
             (click-prompt :corp "Keep")
             (click-prompt :runner "Keep")
             (click-prompt :corp "[click]: Gain 1[c].")
             (click-prompt :corp "[click]: Gain 1[c].")
             (click-prompt :corp "[click]: Gain 1[c].")
             (:active-player)))))
