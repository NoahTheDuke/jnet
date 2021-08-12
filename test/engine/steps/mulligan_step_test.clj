(ns engine.steps.mulligan-step-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.test-helper :refer [click-prompt get-messages]]))

(deftest mulligan-messages-test
  (is (= [{:text [{:name "Corp player"} "has kept their hand"]}]
         (-> (game/start-new-game {:corp {:user {:username "Corp player"}}})
             (second)
             (click-prompt :corp "Keep")
             (get-messages)))))
