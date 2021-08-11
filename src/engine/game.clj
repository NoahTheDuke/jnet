(ns engine.game
  (:require
   [engine.pipeline :refer [continue-game queue-step]]
   [engine.player :refer [new-corp new-runner]]
   [engine.steps.base-step :refer [simple-step]]
   [engine.steps.setup-phase :as sp]
   [engine.steps.start-of-turn-phase :refer [start-of-turn-phase]]))

(defn new-game
  [{:keys [corp runner]}]
  {:corp (new-corp corp)
   :runner (new-runner runner)
   :gp {:pipeline []
        :queue []}
   :active-player :runner
   :messages []
   :turns 0})

(defn begin-turn []
  (simple-step
    (fn [game]
      (let [last-turn-player (:active-player game)
            active-player (if (= :corp last-turn-player) :runner :corp)]
        (-> game
            (assoc :active-player active-player)
            (queue-step (start-of-turn-phase)))))))

(defn start-new-game
  [opts]
  (-> (new-game opts)
      (queue-step (sp/setup-phase))
      (queue-step (begin-turn))
      (continue-game)
      (second)))
