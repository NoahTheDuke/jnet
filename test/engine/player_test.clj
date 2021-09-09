(ns engine.player-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.data :as data]
   [engine.player :as sut]))

(deftest new-player-test
  (let [user {:username "Noah"}
        deck-list {:identity "custom-biotics-engineered-for-success"
                   :cards [{:name "hedge-fund"
                            :qty 2}]}
        opts (conj {:user user}
                   (data/make-cards-in-deck deck-list))]
    (is (= (:username user) (:name (sut/new-player opts))))))
