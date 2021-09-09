(ns engine.steps.draw-phase
  (:require
   [engine.draw :as draw]
   [engine.messages :as msg]
   [engine.pipeline :as pipeline]
   [engine.steps.phase :as phase]
   [engine.steps.step :as step :refer [defstep]]))

(defstep mandatory-draw []
  (-> game
      (msg/add-message "{0} draws 1 card for their mandatory draw." [(:corp game)])
      (draw/draw :corp 1)))

(defn make-draw-phase []
  (phase/make-phase
    {:phase :draw
     :condition (fn [game] (= :corp (:active-player game)))
     :steps [(mandatory-draw)]}))

(defn draw-phase
  [game]
  (pipeline/queue-step game (make-draw-phase)))
