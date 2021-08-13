(ns engine.game
  (:require
   [engine.pipeline :as pipeline]
   [engine.player :as player]
   [engine.steps.step :refer [simple-step]]
   [engine.steps.setup-phase :refer [setup-phase]]
   [engine.steps.draw-phase :refer [draw-phase]]
   [engine.steps.action-phase :refer [action-phase]]
   [engine.steps.start-of-turn-phase :refer [start-of-turn-phase]]))

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

(defn begin-turn []
  (simple-step
    (fn [game]
      (-> game
          (pipeline/queue-step (start-of-turn-phase))
          (pipeline/queue-step (draw-phase))
          (pipeline/queue-step (action-phase))))))

(defn start-new-game
  [opts]
  (-> (new-game opts)
      (pipeline/queue-step (setup-phase))
      (pipeline/queue-step (begin-turn))
      (pipeline/continue-game)))
