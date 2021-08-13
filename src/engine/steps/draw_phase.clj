(ns engine.steps.draw-phase
  (:require
   [engine.draw :as draw]
   [engine.messages :as msg]
   [engine.steps.phase :as phase]
   [engine.pipeline :refer [queue-step]]
   [engine.macros :refer [queue-simple-step]]))

(defn mandatory-draw [game]
  (queue-simple-step game
      (-> game
          (msg/add-message "{0} draws 1 card for their mandatory draw." [(:corp game)])
          (draw/draw :corp 1))))

(defn draw-phase [game]
  (queue-simple-step game
    (if (= :corp (:active-player game))
        (phase/phase-step game
          {:phase :draw
           :steps [mandatory-draw]})
        game)))
