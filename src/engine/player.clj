(ns engine.player)

(defn new-player
  [{:keys [user identity deck]}]
  {:deck-list deck
   :deck deck
   :discard []
   :hand []
   :hand-size 5
   :identity identity
   :play-area []
   :prompt {:select-card false
            :menu-title ""
            :prompt-title ""
            :buttons []}
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
    {:bad-publicity {:base 0
                     :additional 0}
     :clicks-per-turn 3}))

(defn new-runner [opts]
  (merge
    (new-player opts)
    {:brain-damage 0
     :clicks-per-turn 4
     :link 0
     :memory 4
     :tags 0}))
