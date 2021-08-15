(ns engine.steps.prompt
  "Prompt Steps are steps that "
  (:require
   [engine.pipeline :as pipeline]
   [engine.player :as player]
   [engine.prompt-state :as prompt-state]
   [engine.steps.step :as step]
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]))

(def BasePromptSchema
  (mu/merge
    step/BaseStepSchema
    [:map {:closed true}
     [:continue-step {:optional true} :any]
     [:response {:optional true} :any]
     [:active-condition
      [:or [:enum :corp :runner] [:=> [:cat :map :map :keyword] :boolean]]]
     [:active-prompt [:=> [:cat :map :map :keyword] :map]]
     [:waiting-prompt [:map [:text :string]]]
     [:on-prompt-clicked [:=> [:cat :map :map [:enum :corp :runner] :string]
                          :map]]]))

(def validate-prompt (m/validator BasePromptSchema))
(def explain-prompt (m/explainer BasePromptSchema))

(defn bind-buttons
  [step prompt]
  (if-let [buttons (:buttons step)]
    (assoc prompt :buttons buttons)
    prompt))

;; active-prompt -> PromptOptsSchema
(defn set-active-prompt
  [game player {:keys [active-prompt] :as this}]
  (->> (active-prompt this game player)
       (bind-buttons this)
       (update game player player/set-player-prompt)))

(defn set-waiting-prompt
  [game player {:keys [waiting-prompt]}]
  (update game player player/set-player-prompt waiting-prompt))

(defn clear-prompt
  [game]
  (-> game
      (update :corp player/clear-player-prompt)
      (update :runner player/clear-player-prompt)))


(defn set-prompt
  [{:keys [active-condition] :as this} game]
  (let [[active-player waiting-player]
        (if (active-condition this game :corp)
          [:corp :runner]
          [:runner :corp])]
    (-> game
        (set-active-prompt active-player this)
        (set-waiting-prompt waiting-player this))))

;Moved down in the file as for some reason it can't see functions defined below it.
(defrecord PromptStep
  [on-prompt-clicked type uuid]
  step/Step
  (continue-step [this game] (apply on-prompt-clicked this (clear-prompt game) (:response this)))
  (blocking [this game] (if (:response this) false (set-prompt this game)))
  (on-prompt-clicked [this game player arg]
    (assoc-in this [:response] [player arg]))
  (validate [this]
    (if (validate-prompt this)
      this
      (let [explained-error (explain-prompt (into {} this))]
        (throw (ex-info (str "Prompt step isn't valid: " (pr-str (me/humanize explained-error)))
                        (select-keys explained-error [:errors])))))))

(defn base-prompt-step
  [{:keys [active-condition active-prompt waiting-text
           on-prompt-clicked]}]
  (->> {:active-condition
        (cond
          (fn? active-condition) active-condition
          (keyword? active-condition) (fn [_this _game player] (= player active-condition)))
        :active-prompt active-prompt
        :waiting-prompt {:text (or waiting-text "Waiting for opponent")}
        :on-prompt-clicked on-prompt-clicked
        :type :step/prompt
        :uuid (java.util.UUID/randomUUID)}
       (map->PromptStep)
       (step/validate)))

(defn base-prompt
  [game & args]
    (pipeline/queue-step game (apply base-prompt-step args)))

(defn handler-active-prompt
  [active-text buttons]
  (fn [_this _game _player]
    {:header "Choices prompt"
     :text (or active-text "Select one")
     :buttons buttons}))

(defn handler-on-prompt-clicked
  [choices]
  (fn [this game _player arg]
    (if-let [choic (get choices arg)]
      (choic game)
      (pipeline/queue-step game this))))

(def HandlerPromptPropsSchema
  [:map {:closed true}
   [:active-condition
    {:optional true}
    [:or [:enum :corp :runner] [:=> [:cat :map :map :keyword] :boolean]]]
   [:active-text {:optional true} :string]
   [:waiting-text {:optional true} :string]
   [:choices [:map-of :string [:=> [:cat :map] :map]]]])

(def validate-handler-props (m/validator HandlerPromptPropsSchema))
(def explain-handler-props (m/explainer HandlerPromptPropsSchema))

(defn handler-prompt-step
  [props]
  (assert (validate-handler-props props) (me/humanize (explain-handler-props props)))
  (let [{:keys [active-condition active-text waiting-text choices]} props
        buttons (mapv (fn [k] {:text k :arg k}) (keys choices))]
    (base-prompt-step
      {:active-condition active-condition
       :waiting-text waiting-text
       :active-prompt (handler-active-prompt active-text buttons)
       :on-prompt-clicked (handler-on-prompt-clicked choices)})))

(defn handler-prompt
  [game & args]
  (pipeline/queue-step game (apply handler-prompt-step args)))
