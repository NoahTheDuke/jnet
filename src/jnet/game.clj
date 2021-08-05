(ns jnet.game
  (:require [jnet.pipeline :refer [make-pipeline]]
            [jnet.player :as player]))

(defn new-game
  [{:keys [name game-id corp runner]}]
  {:active-player :corp
   :corp corp
   :game-id game-id
   :log []
   :gp (make-pipeline)
   :name name
   :runner runner
   :turn 0})

(defn get-state
  [game]
  {:name (:name game)
   :corp (player/get-player-state (:corp game))
   :runner (player/get-player-state (:runner game))})

(defn active-player?
  [game side]
  (= side (:active-player game)))

(defn set-prompt
  [game side prompt]
  (update game side player/set-prompt prompt))
