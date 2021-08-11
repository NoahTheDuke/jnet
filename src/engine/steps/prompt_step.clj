(ns engine.steps.prompt-step
  "Prompt Steps are steps that "
  (:require
   [engine.player :refer [clear-player-prompt set-player-prompt]]
   [engine.steps.base-step :refer [BaseStepSchema]]
   [engine.steps.step-protocol :refer [Step complete? validate]]
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]))

(def PromptStepSchema
  (mu/merge
    BaseStepSchema
    [:map {:closed true}
     [:complete? boolean?]
     [:active-condition [:or
                         [:enum :corp :runner]
                         [:=> [:cat BaseStepSchema :map :keyword] :boolean]]]
     [:active-prompt [:=> [:cat BaseStepSchema :map :keyword] :any]]
     [:waiting-prompt [:=> [:cat BaseStepSchema :map :keyword] :any]]
     [:on-prompt-clicked [:=> [:cat BaseStepSchema :map [:enum :corp :runner] :string]
                          [:cat :boolean :any]]]]))

(def validate-prompt-step (m/validator PromptStepSchema))
(def explain-prompt-step (m/explainer PromptStepSchema))

(defrecord PromptStep
  [complete? on-prompt-clicked continue-step type uuid]
  Step
  (continue-step [this game] (continue-step this game))
  (complete? [_] complete?)
  (on-prompt-clicked [this game player arg]
    (on-prompt-clicked this game player arg))
  (validate [this]
    (if (validate-prompt-step this)
      this
      (let [explained-error (explain-prompt-step (into {} this))]
        (throw (ex-info (str "Prompt step isn't valid: " (pr-str (me/humanize explained-error)))
                        (select-keys explained-error [:errors])))))))

(defn bind-buttons
  [step prompt]
  (if-let [buttons (:buttons step)]
    (assoc prompt :buttons buttons)
    prompt))

(defn set-active-prompt
  [game player {:keys [active-prompt] :as this}]
  (->> (active-prompt this game player)
       (bind-buttons this)
       (update game player set-player-prompt)))

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

(defn prompt-step
  [{:keys [active-condition active-prompt waiting-prompt
           on-prompt-clicked]}]
  (->> {:active-condition
        (cond
          (fn? active-condition) active-condition
          (keyword? active-condition) (fn [_this _game player] (= player active-condition)))
        :active-prompt active-prompt
        :waiting-prompt (or waiting-prompt
                            (constantly {:text "Waiting for opponent"}))
        :complete? false
        :continue-step prompt-continue-step
        :on-prompt-clicked (or on-prompt-clicked
                               (fn [_this game _player _arg] [false game]))
        :type :step/prompt
        :uuid (java.util.UUID/randomUUID)}
       (map->PromptStep)
       (validate)))
