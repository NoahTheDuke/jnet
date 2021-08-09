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

(defn draw
  [game player amount]
  (let [[drawn deck] (split-at amount (get-in game [player :deck]))]
    (-> game
        (update-in [player :hand] into drawn)
        (assoc-in [player :deck] (into [] deck)))))

(defn draw-initial-hands []
  (simple-step
    (fn [_this game]
      [true (-> game
                (draw :corp 5)
                (draw :runner 5))])))

(defn setup-phase
  [game]
  (-> game
      (assoc :current-phase :phase/setup)
      (queue-step (setup-begin))
      (queue-step (draw-initial-hands))))
