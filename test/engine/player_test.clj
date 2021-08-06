(ns engine.player-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.player :as sut]))

(deftest new-player-test
  (is (sut/new-player nil nil nil))
  (let [user {:username "Noah"}
        deck {:cards [:card1 :card2]}
        id {:id "Corp"}]
    (is (= user (:user (sut/new-player user id deck))))
    (is (= id (:identity (sut/new-player user id deck))))
    (is (= deck (:deck (sut/new-player user id deck))))
    (is (= deck (:deck-list (sut/new-player user id deck))))))
