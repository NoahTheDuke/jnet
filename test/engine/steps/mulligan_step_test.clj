(ns engine.steps.setup-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :refer [start-new-game]]
   [engine.test-helper :refer [click-prompt get-messages]]))

(deftest mulligan-messages
  (is (= {:text ["Corp" "has" "kept" "their" "hand"]}
         (-> (start-new-game nil)
             (click-prompt :corp "Keep")
             (get-messages))
         )))
