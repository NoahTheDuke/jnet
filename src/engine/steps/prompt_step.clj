(ns engine.steps.prompt-step
  "Prompt Steps are steps that "
  (:require
   [engine.steps.base-step :refer [BaseStepSchema]]
   [engine.steps.step-protocol :refer [Step complete? validate]]
   [engine.player :refer [clear-player-prompt set-player-prompt]]
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]))

(def PromptStepSchema
  (mu/merge
    BaseStepSchema
    [:map {:closed true}
     [:active-condition [:=> [:cat BaseStepSchema :map :keyword] :boolean]]
     [:active-prompt [:=> [:cat BaseStepSchema :map :keyword] :any]]
     [:waiting-prompt [:=> [:cat BaseStepSchema :map :keyword] :any]]]))

(def validate-prompt-step (m/validator PromptStepSchema))
(def explain-prompt-step (m/explainer PromptStepSchema))

(defrecord PromptStep
  [complete? continue-step type uuid]
  Step
  (continue-step [this game] (continue-step this game))
  (complete? [this] (:complete? this))
  ; (on-card-clicked [_this _game _player _card])
  ; (on-prompt-clicked [_this _game _arg])
  (validate [this]
    (if (validate-prompt-step this)
      this
      (let [explained-error (explain-prompt-step (into {} this))]
        (throw (ex-info (str "Prompt step isn't valid: " (pr-str (me/humanize explained-error)))
                        (select-keys explained-error [:errors])))))))

(defn set-active-prompt
  [game player {:keys [active-prompt] :as this}]
  (update game player set-player-prompt (active-prompt this game player)))

(defn set-waiting-prompt
  [game player {:keys [waiting-prompt] :as this}]
  (update game player set-player-prompt (waiting-prompt this game player)))

(defn set-prompt
  [{:keys [active-condition] :as this} game]
  (let [[active-player waiting-player]
        (if (active-condition this game :corp) [:corp :runner] [:runner :corp])]
    (-> game
        (set-active-prompt active-player this)
        (set-waiting-prompt waiting-player this))))

(defn clear-prompt
  [game]
  (-> game
      (update :corp clear-player-prompt)
      (update :runner clear-player-prompt)))

(defn prompt-continue-step
  [this game]
  (let [completed (complete? this)
        game (if completed
               (clear-prompt game)
               (set-prompt this game))]
    [completed game]))

(def default-waiting-prompt {:text "Waiting for opponent"})

(defn make-prompt-step
  ([] (make-prompt-step nil))
  ([{:keys [active-condition active-prompt waiting-prompt]}]
   (->> {:active-condition (or active-condition
                               (fn [_this _game _player] true))
         :active-prompt (or active-prompt
                            (fn [_this _game _player] nil))
         :waiting-prompt (or waiting-prompt
                             (fn [_this _game _player] default-waiting-prompt))
         :complete? false
         :continue-step prompt-continue-step
         ; :on-card-clicked (constantly nil)
         ; :on-prompt-clicked (constantly nil)
         :type :step/prompt
         :uuid (java.util.UUID/randomUUID)}
        (map->PromptStep)
        (validate))))
