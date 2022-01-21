(ns engine.moving-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.moving :as sut]
   [engine.test-utils :refer :all]
   [medley.core :refer [find-first]]))

(deftest remove-card-from-zone-test
  (testing "removes the correct card"
    (let [game (new-game)
          card (first (get-in game [:corp :deck]))
          sec-card (second (get-in game [:corp :deck]))]
      (is (not= game (sut/remove-card-from-zone game card)))
      (is (nil? (find-first
                  #(= (:uuid card) (:uuid %))
                  (-> game
                      (sut/remove-card-from-zone card)
                      (get-in [:corp :deck])))))
      (is (= sec-card
             (-> game
                 (sut/remove-card-from-zone card)
                 (get-in [:corp :deck])
                 (first))))
      )))
