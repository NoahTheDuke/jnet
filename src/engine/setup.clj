(ns engine.setup
  (:require
   [engine.pipeline :refer [queue-step]]
   [engine.steps.base-step :refer [simple-step]]
   [engine.steps.prompt-step :refer [prompt-step]]))

(defn setup-begin []
  (simple-step
    (fn [_this game]
      [true (-> game
                (update-in [:corp :deck] shuffle)
                (update-in [:runner :deck] shuffle))])))

(defn draw
  [game player amount]
  (let [[drawn deck] (split-at amount (get-in game [player :deck]))]
    (-> game
        (update-in [player :hand] into drawn)
        (assoc-in [player :deck] (into [] deck)))))

(defn draw-initial-hands []
  (simple-step
    (fn [_this game]
      [true (-> game
                (draw :corp 5)
                (draw :runner 5))])))

(defn mulligan-prompt [player]
  (prompt-step
    {:active-condition player
     :active-prompt
     (fn [_this _game _player]
       {:header "Mulligan"
        :text "Keep or mulligan this hand?"
        :buttons [{:text "Keep" :arg "keep"}
                  {:text "Mulligan" :arg "mulligan"}]})
     :on-prompt-clicked
     (fn [_this game _player arg]
       (let [game (assoc-in game [:gp :pipeline 0 :complete?] true)]
         [true
          (if (= arg "keep")
            game
            (let [hand (get-in game [player :hand])
                  deck (get-in game [player :deck])
                  new-deck (->> deck
                                (concat hand)
                                (shuffle)
                                (into []))]
              (-> game
                  (assoc [player :deck] new-deck)
                  (draw player 5))))]))}))

(defn setup-phase
  [game]
  (-> game
      (assoc :current-phase :phase/setup)
      (queue-step (setup-begin))
      (queue-step (draw-initial-hands))
      (queue-step (mulligan-prompt :corp))
      (queue-step (mulligan-prompt :runner))))
