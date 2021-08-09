(ns engine.player
  (:require
    [engine.prompt-state :as ps]))

(defn new-player
  [{:keys [user identity deck]}]
  {:deck-list (or deck [])
   :deck []
   :discard []
   :hand []
   :hand-size 5
   :identity identity
   :play-area []
   :rfg []
   :scored []
   :set-aside []
   :credits 5
   :clicks 0
   :agenda-points 0
   :ready-to-start false
   :user user})

(defn new-corp [opts]
  (merge
    (new-player opts)
    {:prompt-state (ps/make-prompt-state :corp)
     :bad-publicity {:base 0
                     :additional 0}
     :clicks-per-turn 3}))

(defn new-runner [opts]
  (merge
    (new-player opts)
    {:prompt-state (ps/make-prompt-state :runner)
     :brain-damage 0
     :clicks-per-turn 4
     :link 0
     :memory 4
     :tags 0}))

(defn set-player-prompt
  [player props]
  (update player :prompt-state ps/set-prompt props))

(defn clear-player-prompt
  [player]
  (update player :prompt-state ps/clear-prompt))
