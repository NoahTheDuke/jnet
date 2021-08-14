(ns engine.steps.start-of-turn-phase
  (:require
   [engine.macros :refer [defstep]]
   [engine.pipeline :refer [queue-step]]
   [engine.steps.phase :as phase]))

(defstep gain-allotted-clicks [game]
  (let [active-player (:active-player game)
        default (get-in game [active-player :clicks-per-turn])]
    (assoc-in game [active-player :clicks] default)))

(defstep start-of-turn-paw
  "Stub until PAWs are developed"
  [game]
  game)

(defstep refill-recurring-credits
  "Stub until recurring credits are developed"
  [game]
  game)

(defstep trigger-start-of-turn-abilities
  "Stub until abilities and conditional abilities are developed"
  [game]
  game)

(defstep checkpoint
  "Stub until checkpoints are developed"
  [game]
  game)

(defn start-of-turn-phase
  "* click allotment
  * PAW
  * recurring credits
  * “when your turn begins”
  * checkpoint"
  [game]
  (phase/phase game
    {:phase :start-of-turn
     :steps #(-> % (gain-allotted-clicks)
                   (start-of-turn-paw)
                   (refill-recurring-credits)
                   (trigger-start-of-turn-abilities)
                   (checkpoint))}))
