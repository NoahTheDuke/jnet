(ns engine.steps.setup-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.test-helper :refer [click-prompt get-messages]]))

(deftest mulligan-messages
  (is (= {:text ["Corp" "has" "kept" "their" "hand"]}
         (-> (game/start-new-game nil)
             (second)
             (click-prompt :corp "Keep")
             (get-messages)))))
