(ns engine.steps.mulligan-step
  "Creation"
  (:require
   [engine.draw :refer [draw]]
   [engine.pipeline :refer [complete-current-step]]
   [engine.steps.prompt-step :refer [prompt-step]]))

(defn mulligan-active-prompt
  [& _args]
  {:header "Mulligan"
   :text "Keep or mulligan this hand?"
   :buttons [{:text "Keep" :arg "keep"}
             {:text "Mulligan" :arg "mulligan"}]})

(defn mulligan-prompt-clicked
  [_this game player arg]
  (let [game (complete-current-step game)]
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
                  (draw player 5))]))))

(defn mulligan-prompt [player]
  (prompt-step
    {:active-condition player
     :active-prompt mulligan-active-prompt
     :on-prompt-clicked mulligan-prompt-clicked}))

