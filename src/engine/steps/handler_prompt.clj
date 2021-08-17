(ns engine.steps.handler-prompt
  (:require
   [engine.pipeline :as pipeline]
   [engine.steps.prompt :as prompt]
   [malli.core :as m]
   [malli.error :as me]))

(defn handler-active-prompt
  [active-text buttons]
  (fn [_this _game _player]
    {:header "Choices prompt"
     :text (or active-text "Select one")
     :buttons buttons}))

(defn handler-on-prompt-clicked
  [choices]
  (fn [_this game _player arg]
    (if-let [choice (get choices arg)]
      (-> game
          (choice)
          (pipeline/complete-current-step))
      game)))

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

(defn handler-prompt
  [{:keys [active-condition active-text waiting-text choices] :as props}]
  (let [buttons (mapv (fn [k] {:text k :arg k}) (keys choices))]
    (assert (validate-handler-props props)
            (me/humanize (explain-handler-props props)))
    (prompt/base-prompt
      {:active-condition active-condition
       :waiting-text waiting-text
       :active-prompt (handler-active-prompt active-text buttons)
       :on-prompt-clicked (handler-on-prompt-clicked choices)})))
