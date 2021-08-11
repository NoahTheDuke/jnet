(ns engine.steps.draw-phase
  (:require
   [engine.draw :as draw]
   [engine.messages :as msg]
   [engine.steps.phase-step :as phase]
   [engine.steps.step :as step :refer [simple-step]]))

(defn mandatory-draw []
  (simple-step
    (fn [game]
      (if (= :corp (:active-player game))
        (-> game
            (msg/add-message "{0} draws 1 card for their mandatory draw." [(:corp game)])
            (draw/draw :corp 1))
        game))))

(defn draw-phase []
  (phase/make-phase-step
    {:phase :draw
     :steps [(mandatory-draw)]}))
