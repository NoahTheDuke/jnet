(ns engine.data-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.data :as sut]
   [engine.test-utils :refer :all]))

(deftest make-cards-in-deck-test
  (let [deck {:identity "custom-biotics-engineered-for-success"
              :cards [{:name "hedge-fund"
                       :qty 2}]}]
    (is (= [(sut/make-card "hedge-fund") (sut/make-card "hedge-fund")]
           (:deck-list (sut/make-cards-in-deck deck))))
    (is (= "Custom Biotics: Engineered for Success"
           (get-in (sut/make-cards-in-deck deck) [:identity :name])))))
