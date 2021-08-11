(ns engine.steps.mulligan-step
  (:require
   [engine.draw :as draw]
   [engine.messages :as message]
   [engine.pipeline :as pipeline]
   [engine.steps.prompt-step :as prompt-state]))

(defn mulligan-active-prompt
  [& _args]
  {:header "Mulligan"
   :text "Keep or mulligan this hand?"
   :buttons [{:text "Keep" :arg "keep"}
             {:text "Mulligan" :arg "mulligan"}]})

(defn mulligan-prompt-clicked
  [_this game player arg]
  (let [message (if (= arg "keep")
                  "{0} has kept their hand"
                  "{0} has taken a mulligan")
        game (-> game
                 (pipeline/complete-current-step)
                 (message/add-message message [player]))]
    (if (= arg "keep")
      [true game]
      (let [hand (get-in game [player :hand])
            deck (get-in game [player :deck])
            new-deck (->> deck
                          (concat hand)
                          (shuffle)
                          (into []))]
        [true (-> game
                  (assoc [player :deck] new-deck)
                  (draw/draw player 5))]))))

(defn mulligan-prompt [player]
  (prompt-state/prompt-step
    {:active-condition player
     :active-prompt mulligan-active-prompt
     :on-prompt-clicked mulligan-prompt-clicked}))

