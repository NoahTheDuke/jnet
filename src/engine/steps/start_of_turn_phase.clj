(ns engine.steps.start-of-turn-phase
  (:require
   [engine.macros :refer [queue-simple-step]]
   [engine.pipeline :refer [queue-step]]
   [engine.steps.phase :as phase]))

(defn gain-allotted-clicks [game]
  (queue-simple-step game
    (let [active-player (:active-player game)
          default (get-in game [active-player :clicks-per-turn])]
      (assoc-in game [active-player :clicks] default))))

(defn start-of-turn-paw
  "Stub until PAWs are developed"
  [game]
  (queue-simple-step game game))

(defn refill-recurring-credits
  "Stub until recurring credits are developed"
  [game]
  (queue-simple-step game game))

(defn trigger-start-of-turn-abilities
  "Stub until abilities and conditional abilities are developed"
  [game]
  (queue-simple-step game game))

(defn checkpoint
  "Stub until checkpoints are developed"
  [game]
  (queue-simple-step game game))

(defn start-of-turn-phase
  "* click allotment
  * PAW
  * recurring credits
  * “when your turn begins”
  * checkpoint"
  [game]
  (phase/phase-step game
    {:phase :start-of-turn
     :steps [gain-allotted-clicks
             start-of-turn-paw
             refill-recurring-credits
             trigger-start-of-turn-abilities
             checkpoint]}))
