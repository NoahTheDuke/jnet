(ns engine.steps.setup-phase
  (:require
   [engine.draw :refer [draw]]
   [engine.steps.base-step :refer [simple-step]]
   [engine.steps.mulligan-step :refer [mulligan-prompt]]
   [engine.steps.phase-step :refer [make-phase-step]]))

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

(defn setup-phase []
  (make-phase-step
    {:phase :phase/setup
     :steps [(setup-begin)
             (draw-initial-hands)
             (mulligan-prompt :corp)
             (mulligan-prompt :runner)]}))
