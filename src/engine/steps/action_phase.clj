(ns engine.steps.action-phase
  (:require
   [engine.draw :as draw]
   [engine.messages :as msg]
   [engine.steps.phase-step :as phase]
   [engine.steps.step :as step :refer [simple-step]]))

(defn mandatory-draw []
  (simple-step
    (fn [game]
      (-> game
          (msg/add-message "{0} draws 1 card for their mandatory draw." [(:corp game)])
          (draw/draw :corp 1)))))

(defn draw-phase []
  (phase/make-phase-step
    {:phase :draw
     :condition (fn [game] (= :corp (:active-player game)))
     :steps [(mandatory-draw)]}))
