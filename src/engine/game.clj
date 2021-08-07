(ns engine.game
  (:require [engine.player :refer [new-corp new-runner]]))

(defn new-game
  [{:keys [corp runner]
    :or {corp {} runner {}}}]
  {:corp (new-corp corp)
   :runner (new-runner runner)
   :gp {:pipeline []
        :queue []}
   :turns 0})

(defn initialize-game [game]
  (assoc game :phase :setup))
