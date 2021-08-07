(ns engine.game-test
  (:require [clojure.test :refer [deftest is testing]]
            [engine.game :as sut]
            [engine.player :as player]))

(deftest new-game-test
  (with-redefs
    [player/new-corp (fn [c] c)
     player/new-runner (fn [r] r)]
    (testing "corp and runner are straight assignment"
      (let [corp {:name "corp"}
            runner {:name "runner"}]
        (is (= {:corp corp
                :runner runner}
               (select-keys (sut/new-game {:corp corp :runner runner})
                            [:corp :runner]))))))
  (testing "pipeline exists"
    (is (= {:pipeline [] :queue []} (:gp (sut/new-game {})))))
  (testing "initial turns start at 0"
    (is (= 0 (:turns (sut/new-game {}))))))

(deftest initialize-game-test
  (is (= :setup (->> nil
                     (sut/new-game)
                     (sut/initialize-game)
                     (:phase)))))
