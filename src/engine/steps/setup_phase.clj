(ns engine.steps.setup-phase
  (:require
   [engine.draw :refer [draw]]
   [engine.pipeline :refer [queue-step]]
   [engine.steps.base-step :refer [simple-step]]
   [engine.steps.mulligan-step :refer [mulligan-prompt]]))

(defn setup-begin []
  (simple-step
    (fn [game]
      (-> game
          (update-in [:corp :deck] shuffle)
          (update-in [:runner :deck] shuffle)))))

(defn draw-initial-hands []
  (simple-step
    (fn [game]
      (-> game
          (draw :corp 5)
          (draw :runner 5)))))

(defn setup-phase
  [game]
  (-> game
      (assoc :current-phase :phase/setup)
      (queue-step (setup-begin))
      (queue-step (draw-initial-hands))
      (queue-step (mulligan-prompt :corp))
      (queue-step (mulligan-prompt :runner))))
