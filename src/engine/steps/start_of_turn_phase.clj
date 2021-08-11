(ns engine.steps.start-of-turn-phase
  (:require
   [engine.steps.base-step :as base-step]
   [engine.steps.phase-step :refer [make-phase-step]]))

(defn start-of-turn-phase
  "* click allotment
  * PAW
  * recurring credits
  * “when your turn begins”
  * checkpoint"
  [game]
  (make-phase-step
    {:phase :phase/start-of-turn
     :steps [(base-step/make-base-step
               {:continue-step (fn [_ game] [false game])})]}))
