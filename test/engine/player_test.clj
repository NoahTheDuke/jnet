(ns engine.player-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.player :as sut]))

(deftest new-player-test
  (is (sut/new-player nil))
  (let [deck [:card1 :card2]
        id {:id "Corp"}
        user {:username "Noah"}
        opts {:user user :identity id :deck deck}]
    (is (= (:username user) (:name (sut/new-player opts))))
    (is (= id (:identity (sut/new-player opts))))
    (is (= deck (:deck (sut/new-player opts))))
    (is (= deck (:deck-list (sut/new-player opts))))))
