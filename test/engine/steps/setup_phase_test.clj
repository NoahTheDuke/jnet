(ns engine.steps.setup-phase-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.setup-phase :as sut]
   [engine.test-utils :refer :all]))

(deftest setup-test
  (let [deck-list (map #(do {:name (str %)}) "abcdefghij")]
    (is (= :phase/setup
           (-> (game/make-game nil)
               (pipeline/queue-step (sut/make-setup-phase))
               (pipeline/continue-game)
               (:current-phase))))
    (testing "both players shuffle their decks"
      (with-redefs [clojure.core/shuffle (comp vec reverse)]
        (is (= [{:name "e"
                 :zone :deck}
                {:name "d"
                 :zone :deck}
                {:name "c"
                 :zone :deck}
                {:name "b"
                 :zone :deck}
                {:name "a"
                 :zone :deck}]
               (-> (game/make-game {:corp {:deck-list deck-list}})
                   (pipeline/queue-step (sut/make-setup-phase))
                   (pipeline/continue-game)
                   (get-in [:corp :deck]))))))
    (testing "both players draw 5 cards"
      (is (= 5
             (-> (game/make-game {:corp {:deck-list deck-list}})
                 (pipeline/queue-step (sut/make-setup-phase))
                 (pipeline/continue-game)
                 (get-in [:corp :hand])
                 (count))))
      (is (= 5
             (-> (game/make-game {:runner {:deck-list deck-list}})
                 (pipeline/queue-step (sut/make-setup-phase))
                 (pipeline/continue-game)
                 (get-in [:runner :hand])
                 (count)))))))

(deftest mulligan-tests
  (testing "mulligan prompts display correctly"
    (let [game (-> (game/make-game {:corp {:deck-list (a-deck :corp)}})
                   (pipeline/queue-step (sut/make-setup-phase))
                   (pipeline/continue-game))]
      (is (= {:header "Mulligan"
              :text "Keep or mulligan this hand?"
              :buttons [{:text "Keep"
                         :arg "keep"}
                        {:text "Mulligan"
                         :arg "mulligan"}]}
             (-> game
                 (get-in [:corp :prompt-state])
                 (select-keys [:header :text :buttons]))))
      (is (= {:header ""
              :text "Waiting for opponent"
              :buttons []}
             (-> game
                 (get-in [:runner :prompt-state])
                 (select-keys [:header :text :buttons]))))
      (is (= {:header "Mulligan"
              :text "Keep or mulligan this hand?"
              :buttons [{:text "Keep"
                         :arg "keep"}
                        {:text "Mulligan"
                         :arg "mulligan"}]}
             (-> game
                 (click-prompt :corp "Keep")
                 (get-in [:runner :prompt-state])
                 (select-keys [:header :text :buttons]))))
      (is (= {:header ""
              :text "Waiting for opponent"
              :buttons []}
             (-> game
                 (click-prompt :corp "Keep")
                 (get-in [:corp :prompt-state])
                 (select-keys [:header :text :buttons])))))))
