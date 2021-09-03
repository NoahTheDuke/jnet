(ns engine.steps.setup-phase
  (:require
   [engine.deck :as deck]
   [engine.draw :as draw]
   [engine.steps.mulligan-step :as mulligan]
   [engine.steps.phase :as phase]
   [engine.steps.step :refer [defstep]]))

(defstep draw-initial-hands []
  (-> game
      (assoc-in [:corp :credits] 5)
      (assoc-in [:runner :credits] 5)
      (update-in [:corp :deck] deck/shuffle-deck)
      (update-in [:runner :deck] deck/shuffle-deck)
      (draw/draw :corp 5)
      (draw/draw :runner 5)))

(defn setup-phase []
  (phase/make-phase
    {:phase :setup
     :steps [(draw-initial-hands)
             (mulligan/mulligan-prompt :corp)
             (mulligan/mulligan-prompt :runner)]}))
