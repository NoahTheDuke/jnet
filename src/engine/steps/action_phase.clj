(ns engine.steps.action-phase
  (:require
   [engine.messages :as msg]
   [engine.pipeline :as pipeline]
   [engine.steps.prompt :as prompt]
   [engine.steps.step :refer [simple-step]]
   [engine.steps.phase :as phase]))

(defn action-active-prompt
  [_this game player]
  (let [clicks (get-in game [player :clicks])]
    {:header "Action Phase"
     :text (str "You have " clicks " clicks."
                (when (pos? clicks)
                  " Choose an action."))
     :buttons [{:text "[click]: Gain 1[c]." :arg "credit"}]}))

(defn action-prompt-clicked
  [_this game player _arg]
  (-> game
      (pipeline/complete-current-step)
      (msg/add-message "{0} gains 1 credit." [(get game player)])
      (update-in [player :credits] inc)
      (update-in [player :clicks] dec)))

(defn active-player?
  [_this {:keys [active-player]} player]
  (= player active-player))

(defn action-window []
  (prompt/base-prompt
    {:active-condition active-player?
     :active-prompt action-active-prompt
     :on-prompt-clicked action-prompt-clicked}))

(defn check-for-more-clicks []
  (simple-step
    (fn [{:keys [active-player] :as game}]
      (if (pos? (get-in game [active-player :clicks]))
        (-> game
            (pipeline/queue-step (action-window))
            (pipeline/queue-step (check-for-more-clicks)))
        game))))

(defn action-phase []
  (phase/make-phase
    {:phase :action
     :steps [(check-for-more-clicks)]}))
