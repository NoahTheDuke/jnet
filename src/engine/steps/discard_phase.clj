(ns engine.steps.discard-phase
  (:require
   [engine.messages :as msg]
   [engine.pipeline :as pipeline]
   [engine.steps.prompt :as prompt]
   [engine.steps.step :refer [simple-step]]
   [engine.steps.phase :as phase]))

(defn discard-step []
  (prompt/base-prompt
    {:active-condition :corp
     :active-prompt
     (fn [this game player]
       {:select-card true
        :header "Discard"
        :text "Select cards to discard"
        :buttons [{:text "Card 1" :arg "1"}]})
     :on-prompt-clicked (fn [_this game _player _arg] [true game])}))

(defn should-discard? []
  (simple-step
    (fn [{:keys [active-player] :as game}]
      (let [hand-size (count (get-in game [active-player :hand]))]
        (if (< 5 hand-size)
          (pipeline/queue-step game (discard-step))
          game)))))

(defn lose-remaining-clicks []
  (simple-step
    (fn [{:keys [active-player] :as game}]
      (let [clicks (get-in game [active-player :clicks])]
        (if (pos? clicks)
          (-> game
              (assoc-in [active-player :clicks] 0)
              (msg/add-message "{0} loses their {1} remaining clicks."
                               [(get game active-player) clicks]))
          game)))))

(defn discard-phase []
  (phase/make-phase
    {:phase :discard
     :steps [(should-discard?)
             (lose-remaining-clicks)]}))
