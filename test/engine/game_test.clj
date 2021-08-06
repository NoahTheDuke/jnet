(ns engine.game-test
  (:require [clojure.test :refer [deftest is testing]]
            [engine.game :as sut]))

(deftest new-game-test
  (testing "default to empty maps"
    (is (= {:corp {} :runner {}} (select-keys (sut/new-game {}) [:corp :runner]))))
  (testing "corp and runner are straight assignment"
    (let [corp {:name "corp"}
          runner {:name "runner"}]
      (is (= {:corp corp
              :runner runner}
             (select-keys (sut/new-game {:corp corp :runner runner})
                          [:corp :runner])))))
  (testing "pipeline exists"
    (is (= {:pipeline [] :queue []} (:gp (sut/new-game {})))))
  (testing "initial turns start at 0"
    (is (= 0 (:turns (sut/new-game {}))))))
