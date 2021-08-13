(ns engine.player-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.player :as sut]))

(deftest new-player-test
  (let [user {:username "Noah"}
        opts {:user user
              :deck {:identity "custom-biotics-engineered-for-success"
                     :cards [{:name "hedge-fund"
                              :qty 2}]}}]
    (is (= (:username user) (:name (sut/new-player opts))))))
