(ns engine.steps.action-phase-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.action-phase :as sut]
   [engine.test-helper :refer [click-prompt get-messages]]))

(deftest action-phase-prompt-test
  (is (= "You have 3 clicks. What will you spend them on, douche?"
         (-> (game/start-new-game nil)
             (second)
             (click-prompt :corp "Keep")
             (click-prompt :runner "Keep")
             (:corp))))
  )
