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

(defn draw-initial-hands []
  (simple-step
    (fn [_this game]
      (let [[starting-corp corp-deck] (split-at 5 (get-in game [:corp :deck]))
            [starting-runner runner-deck] (split-at 5 (get-in game [:runner :deck]))]
        [true
         (-> game
             (update-in [:corp :hand] #(into [] (apply conj % starting-corp)))
             (assoc-in [:corp :deck] (into [] corp-deck))
             (update-in [:runner :hand] #(into [] (apply conj % starting-runner)))
             (assoc-in [:runner :deck] (into [] runner-deck)))]))))

(defn setup-phase
  [game]
  (-> game
      (assoc :current-phase :phase/setup)
      (queue-step (setup-begin))
      (queue-step (draw-initial-hands))))
