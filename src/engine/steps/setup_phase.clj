(ns engine.steps.setup-phase
  (:require
   [engine.draw :as draw]
   [engine.steps.mulligan-step :as mulligan]
   [engine.steps.phase :as phase]
   [engine.pipeline :refer [queue-step]]
   [engine.macros :refer [queue-simple-step]]
   [engine.steps.phase :as phase]))

(defn setup-begin [game]
  (-> game (queue-simple-step
             (-> game (assoc-in [:corp :credits] 5)
                      (assoc-in [:runner :credits] 5)))))

(defn draw-initial-hands [game]
  (-> game
      (update-in [:corp :deck] shuffle)
      (update-in [:runner :deck] shuffle)
      (draw/draw :corp 5)
      (draw/draw :runner 5)))

(defn setup-phase [game]
  (phase/phase-step game
    {:phase :setup
     :steps [setup-begin
             draw-initial-hands
             #(mulligan/mulligan-prompt % :corp)
             #(mulligan/mulligan-prompt % :runner)]}))

