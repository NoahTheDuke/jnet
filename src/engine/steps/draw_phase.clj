(ns engine.steps.draw-phase
  (:require
   [engine.draw :as draw]
   [engine.messages :as msg]
   [engine.steps.phase :as phase]
   [engine.macros :refer [defstep]]))

(defstep mandatory-draw [game]
  (-> game
      (msg/add-message "{0} draws 1 card for their mandatory draw." [(:corp game)])
      (draw/draw :corp 1)))

(defstep draw-phase [game]
  (if (= :corp (:active-player game))
      (phase/phase game
        {:phase :draw
         :steps mandatory-draw})
      game))
