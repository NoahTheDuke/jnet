(ns engine.player)

(defn new-player
  [user identity deck]
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

; (defn new-corp
;   [user identity deck]
;   (merge
;     (new-player user identity deck)
;     {:bad-publicity {:base 0
;                      :additional 0}
;      :clicks-per-turn 3}))

; (defn new-runner
;   [user identity deck]
;   (merge
;     (new-player user identity deck)
;     {:brain-damage 0
;      :clicks-per-turn 4
;      :link 0
;      :memory 4
;      :tag 0}))
