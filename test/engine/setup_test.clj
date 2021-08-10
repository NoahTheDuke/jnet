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
    (with-redefs [clojure.core/shuffle (comp #(into [] %) reverse)]
      (is (= [:d :c :b :a]
             (-> (new-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}})
                 (initialize-game)
                 (sut/setup-phase)
                 (continue-game)
                 (second)
                 (get-in [:corp :deck]))))))
  (testing "both players draw 5 cards"
    (is (= 5
           (-> (new-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}})
               (initialize-game)
               (sut/setup-phase)
               (continue-game)
               (second)
               (get-in [:corp :hand])
               (count))))
    (is (= 5
           (-> (new-game {:runner {:deck [:a :b :c :d :e :f :g :h :i]}})
               (initialize-game)
               (sut/setup-phase)
               (continue-game)
               (second)
               (get-in [:runner :hand])
               (count))))))

(deftest mulligan-tests
  (testing "mulligan prompts display correctly"
    (let [game (-> (new-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}})
                   (initialize-game)
                   (sut/setup-phase)
                   (continue-game)
                   (second))]
      (is (= {:header "Mulligan"
              :text "Keep or mulligan this hand?"}
             (-> game
                 (get-in [:corp :prompt-state])
                 (select-keys [:header :text]))))
      (is (= {:header ""
              :text "Waiting for opponent"}
             (-> game
                 (get-in [:runner :prompt-state])
                 (select-keys [:header :text]))))
      ; (is (= {:header ""
      ;         :text "Waiting for opponent"}
      ;        (-> game
      ;            (click-prompt :corp "Keep")
      ;            )))
      ))
  )
