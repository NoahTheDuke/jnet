(ns engine.steps.mulligan-step-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.prompt-state :as prompt-state]
   [engine.test-utils :refer :all]))

(deftest mulligan-messages-test
  (is (= [{:text [{:name "Corp player"} "has kept their hand"]}
          {:text [{:name "Runner player"} "has kept their hand"]}
          {:text [{:name "Corp player"} "draws 1 card for their mandatory draw."]}]
         (-> (new-game)
             (click-prompt :corp "Keep")
             (click-prompt :runner "Keep")
             (get-messages)))))

(deftest mulligan-prompt-test
  (let [game (new-game)]
    (is (= "Mulligan"
           (prompt-state/prompt-header game :corp)))
    (is (= "Keep or mulligan this hand?"
           (prompt-state/prompt-text game :corp)))))
