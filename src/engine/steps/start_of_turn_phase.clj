(ns engine.steps.start-of-turn-phase
  (:require
   [engine.steps.step :as step :refer [simple-step]]
   [engine.steps.phase-step :as phase]))

(defn gain-allotted-clicks []
  (simple-step
    (fn [game]
      (let [active-player (:active-player game)
            default (get-in game [active-player :clicks-per-turn])]
        (assoc-in game [active-player :clicks] default)))))

(defn start-of-turn-paw
  "Stub until PAWs are developed"
  []
  (simple-step (fn [game] game)))

(defn refill-recurring-credits
  "Stub until recurring credits are developed"
  []
  (simple-step (fn [game] game)))

(defn trigger-start-of-turn-abilities
  "Stub until abilities and conditional abilities are developed"
  []
  (simple-step (fn [game] game)))

(defn checkpoint
  "Stub until checkpoints are developed"
  []
  (simple-step (fn [game] game)))

(defn start-of-turn-phase
  "* click allotment
  * PAW
  * recurring credits
  * “when your turn begins”
  * checkpoint"
  []
  (phase/make-phase-step
    {:phase :phase/start-of-turn
     :steps [(gain-allotted-clicks)
             (start-of-turn-paw)
             (refill-recurring-credits)
             (trigger-start-of-turn-abilities)
             (checkpoint)
             (step/make-base-step
               {:continue-step (fn [_ game] [false game])})]}))
