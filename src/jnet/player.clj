(ns jnet.player)

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

(defn new-corp
  [user identity deck]
  (merge
    (new-player user identity deck)
    {:bad-publicity {:base 0
                     :additional 0}
     :clicks-per-turn 3}))

(defn new-runner
  [user identity deck]
  (merge
    (new-player user identity deck)
    {:brain-damage 0
     :clicks-per-turn 4
     :link 0
     :memory 4
     :tag 0}))

(defn init-hand
  [{:keys [deck-list] :as player}]
  (let [[hand deck] (split-at 5 (shuffle deck-list))]
    (assoc player
           :deck (into [] deck)
           :hand (into [] hand))))

(defn set-prompt
  [player {:keys [select-card menu-title buttons]}]
  (let [prompt {:select-card (or select-card false)
                :menu-title (or menu-title "")
                :buttons (or buttons [])}]
    (assoc player :prompt prompt)))

(defn clear-prompt
  [player]
  (assoc player :prompt {:select-card false
                         :menu-title ""
                         :buttons []}))

(defn init-player
  [player]
  (let [mulligan-prompt {:menu-title "Keep starting hand?"
                         :buttons [{:command "keep"
                                    :text "Keep hand"}
                                   {:command "mulligan"
                                    :text "Mulligan"}]}]
    (-> player
        (init-hand)
        (set-prompt mulligan-prompt))))

(defn keep-hand
  [player]
  (-> player
      (assoc :ready-to-start true)
      (set-prompt {:menu-title "Waiting for opponent to keep or mulligan"})))

(defn mulligan-hand
  [player]
  (-> player
      (assoc :ready-to-start true
             :mulligan true)
      (init-hand)
      (set-prompt {:menu-title "Waiting for opponent to keep or mulligan"})))

(defn get-player-state
  [player side active-player?]
  (merge
    {:user (select-keys (:user player) [:username :id])
     :deck-count (count (:deck player))
     :hand-count (count (:hand player))
     :hand (when active-player? (:hand player))
     :prompt (when active-player? (:prompt player))}
    (select-keys player [:identity
                         :discard :agenda-points :credits
                         :play-area :scored :rfg :set-aside])))
