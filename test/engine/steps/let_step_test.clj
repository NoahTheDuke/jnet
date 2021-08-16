(ns engine.steps.let-step-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.macros :as sut]))

;This should also check things are ordered correctly and nil defaults
;I'm not thrilled with this style of testing though, putting is inside of steps seems wrong. What if the step doesn't run at all? but it's hard to test var binding otherwise, maybe atoms?

(deftest let-step-test
  (let [game (game/new-game {})]
    (testing "Basic test"
      (let [game (-> game
                     (sut/let-step [test-var (sut/set-result game :--test-flag)]
                                   (is (= test-var :--test-flag))
                                   (assoc-in game [:--test-loc2] :--test-flag2)
                     (pipeline/continue-game)))]
        (is "Test steps were executed" (= :--test-flag2 (get-in game [:--test-loc2])))))
    (testing "Multiple bindings test"
      (let [game (-> game
                     (sut/let-step [test-var1 (sut/set-result game :--test-flag1)
                                    test-var2 (sut/set-result game :--test-flag2)]
                                   (is (= test-var1 :--test-flag1))
                                   (is (= test-var2 :--test-flag2))
                                   (assoc-in game [:--test-loc3] :--test-flag3))
                     (pipeline/continue-game))]
        (is "Test steps were executed" (= :--test-flag3 (get-in game [:--test-loc3])))))))
