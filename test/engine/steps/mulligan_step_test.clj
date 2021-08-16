(ns engine.steps.mulligan-step-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.prompt-state :as prompt-state]
   [engine.test-utils :refer [click-prompt get-messages]]))

(deftest mulligan-messages-test
  (is (= [{:text [{:name "Corp player"} "has kept their hand"]}
          {:text [{:name "Runner player"} "has kept their hand"]}
          {:text [{:name "Corp player"} "draws 1 card for their mandatory draw."]}]
         (-> (game/start-new-game {:corp {:user {:username "Corp player"}}
                                   :runner {:user {:username "Runner player"}}})
             (click-prompt :corp "Keep")
             (click-prompt :runner "Keep")
             (get-messages)))))

(deftest mulligan-prompt-test
  (let [game (game/start-new-game nil)]
    (is (= "Mulligan"
           (prompt-state/prompt-header game :corp)))
    (is (= "Keep or mulligan this hand?"
           (prompt-state/prompt-text game :corp)))))
