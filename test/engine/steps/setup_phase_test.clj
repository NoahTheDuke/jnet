(ns engine.steps.setup-phase-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.setup-phase :as sut]
   [engine.test-utils :refer [click-prompt]]))

(deftest setup-test
  (is (= :phase/setup
         (-> (game/make-game nil)
             (pipeline/queue-step (sut/setup-phase))
             (pipeline/continue-game)
             (:current-phase))))
  (testing "both players shuffle their decks"
    (with-redefs [clojure.core/shuffle (comp vec reverse)]
      (is (= [:d :c :b :a]
             (-> (game/make-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}})
                 (pipeline/queue-step (sut/setup-phase))
                 (pipeline/continue-game)
                 (get-in [:corp :deck]))))))
  (testing "both players draw 5 cards"
    (is (= 5
           (-> (game/make-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}})
               (pipeline/queue-step (sut/setup-phase))
               (pipeline/continue-game)
               (get-in [:corp :hand])
               (count))))
    (is (= 5
           (-> (game/make-game {:runner {:deck [:a :b :c :d :e :f :g :h :i]}})
               (pipeline/queue-step (sut/setup-phase))
               (pipeline/continue-game)
               (get-in [:runner :hand])
               (count))))))

(deftest mulligan-tests
  (testing "mulligan prompts display correctly"
    (let [game (-> (game/make-game {:corp {:deck [:a :b :c :d :e :f :g :h :i]}})
                   (pipeline/queue-step (sut/setup-phase))
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
