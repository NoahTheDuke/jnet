(ns engine.setup
  (:require
   [engine.pipeline :refer [queue-step]]
   [engine.steps.base-step :refer [simple-step]]))

(defn setup-begin []
  (simple-step
    (fn [_this game]
      [true (-> game
                (update-in [:corp :deck] shuffle)
                (update-in [:runner :deck] shuffle))])))

(defn setup-phase
  [game]
  (-> game
      (assoc :current-phase :phase/setup)
      (queue-step (setup-begin))))
