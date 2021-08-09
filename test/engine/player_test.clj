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
    (is (= user (:user (sut/new-player opts))))
    (is (= id (:identity (sut/new-player opts))))
    (is (= [] (:deck (sut/new-player opts))))
    (is (= deck (:deck-list (sut/new-player opts))))))

(deftest player-state-is-set-test
  (is (= :corp (-> (sut/new-corp {})
                   (get-in [:prompt-state :player]))))
  (is (= :runner (-> (sut/new-runner {})
                     (get-in [:prompt-state :player])))))
