(ns engine.game-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as sut]))

(deftest new-game-test
  (testing "pipeline exists"
    (is (:gp (sut/new-game {}))))
  (testing "initial turns set"
    (is (:turns (sut/new-game {})))))

(deftest start-new-game
  (is (= [:a :b :c]
         (-> (sut/new-game {:corp {:deck [:a :b :c]}})
             (get-in [:corp :deck])))))
