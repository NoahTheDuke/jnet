(ns engine.game
  (:require
   [engine.pipeline :as pipeline]
   [engine.player :as player]
   [engine.steps.step :as step]
   [engine.steps.setup-phase :as setup-phase]
   [engine.steps.start-of-turn-phase :as sot-phase]))

(defn new-game
  [{:keys [corp runner]}]
  {:corp (player/new-corp corp)
   :runner (player/new-runner runner)
   :gp {:pipeline []
        :queue []}
   :active-player :runner
   :inactive-player :corp
   :messages []
   :turns 0})

(defn begin-turn []
  (step/simple-step
    (fn [game]
      (let [last-turn-player (:active-player game)
            active-player (if (= :corp last-turn-player) :runner :corp)
            inactive-player last-turn-player]
        (-> game
            (assoc :active-player active-player
                   :inactive-player inactive-player)
            (pipeline/queue-step (sot-phase/start-of-turn-phase)))))))

(defn start-new-game
  [opts]
  (-> (new-game opts)
      (pipeline/queue-step (setup-phase/setup-phase))
      (pipeline/queue-step (begin-turn))
      (pipeline/continue-game)
      (second)))
