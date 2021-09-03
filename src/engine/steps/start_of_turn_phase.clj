(ns engine.steps.start-of-turn-phase
  (:require
   [engine.steps.phase :as phase]
   [engine.steps.step :as step :refer [defstep]]))

(defstep gain-allotted-clicks []
  (let [active-player (:active-player game)
        default (get-in game [active-player :clicks-per-turn])]
    (assoc-in game [active-player :clicks] default)))

(defstep start-of-turn-paw
  "Stub until PAWs are developed"
  []
  game)

(defstep refill-recurring-credits
  "Stub until recurring credits are developed"
  []
  game)

(defstep trigger-start-of-turn-abilities
  "Stub until abilities and conditional abilities are developed"
  []
  game)

(defstep checkpoint
  "Stub until checkpoints are developed"
  []
  game)

(defn start-of-turn-phase
  "* click allotment
  * PAW
  * recurring credits
  * “when your turn begins”
  * checkpoint"
  []
  (phase/make-phase
    {:phase :start-of-turn
     :steps [(gain-allotted-clicks)
             ; (start-of-turn-paw)
             ; (refill-recurring-credits)
             ; (trigger-start-of-turn-abilities)
             ; (checkpoint)
             ]}))
