(ns engine.game
  (:require
   [engine.player :refer [new-corp new-runner]]))

(defn new-game
  [{:keys [corp runner]}]
  {:corp (new-corp corp)
   :runner (new-runner runner)
   :gp {:pipeline []
        :queue []}
   :messages []
   :turns 0})

(defn prepare-player-deck [game player]
  (let [deck-list (get-in game [player :deck-list])]
    (assoc-in game [player :deck] deck-list)))

(defn start-new-game
  [opts]
  (-> (new-game opts)
      (prepare-player-deck :corp)
      (prepare-player-deck :runner)))
