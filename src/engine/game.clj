(ns engine.game
  (:require
   [engine.player :refer [new-corp new-runner]]
   [engine.pipeline :refer [continue-game]]
   [engine.steps.setup-phase :refer [setup-phase]]))

(defn new-game
  [{:keys [corp runner]}]
  {:corp (new-corp corp)
   :runner (new-runner runner)
   :gp {:pipeline []
        :queue []}
   :messages []
   :turns 0})

(defn start-new-game
  [opts]
  (-> (new-game opts)
      (setup-phase)
      (continue-game)
      (second)))
