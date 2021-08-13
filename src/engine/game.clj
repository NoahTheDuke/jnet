(ns engine.game
  (:require
   [engine.pipeline :as pipeline]
   [engine.player :as player]
   [engine.macros :refer [queue-simple-step]]
   [engine.steps.step :as step]
   [engine.steps.setup-phase :as setup-phase]
   [engine.steps.draw-phase :as draw-phase]
   [engine.steps.start-of-turn-phase :as sot-phase]))

(defn new-game
  [{:keys [corp runner]}]
  {:corp (player/new-corp corp)
   :runner (player/new-runner runner)
   :gp {:pipeline []
        :queue []}
   :active-player :corp
   :inactive-player :runner
   :messages []
   :turns 0})

(defn begin-turn [game]
  (-> game (sot-phase/start-of-turn-phase)
           (draw-phase/draw-phase)))

(defn start-new-game
  [opts]
  (-> (new-game opts)
      (setup-phase/setup-phase)
      (begin-turn)
      (pipeline/continue-game)))
