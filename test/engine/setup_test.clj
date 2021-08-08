(ns engine.setup-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :refer [initialize-game new-game]]
   [engine.pipeline :refer [continue-game]]
   [engine.setup :as sut]))

(deftest setup-test
  (is (= :phase/setup
         (-> (new-game nil)
             (initialize-game)
             (sut/setup-phase)
             (continue-game)
             (second)
             (:current-phase))))
  (testing "both players shuffle their decks"
    (with-redefs [clojure.core/shuffle reverse]
      (is (= [:c :b :a]
             (-> (new-game {:corp {:deck [:a :b :c]}})
                 (initialize-game)
                 (sut/setup-phase)
                 (continue-game)
                 (second)
                 (get-in [:corp :deck]))))))
  )
