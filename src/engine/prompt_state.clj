(ns engine.prompt-state
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defrecord PromptState [select-card header text buttons])

(def PromptStateSchema
  [:map {:closed true}
   [:select-card boolean?]
   [:header string?]
   [:text string?]
   [:buttons [:* :any]]])

(def valid-prompt-state? (m/validator PromptStateSchema))
(def explain-prompt-state (m/explainer PromptStateSchema))

(defn validate-prompt-state [this]
  (assert (valid-prompt-state? this)
          (-> (into {} this)
              (explain-prompt-state)
              (me/humanize)
              (pr-str)))
  this)

(defn make-prompt-state
  []
  (-> {:select-card false
       :header ""
       :text ""
       :buttons []}
      (map->PromptState)
      (validate-prompt-state)))

(def PromptOptsSchema
  [:map {:closed true}
   [:select-card {:optional true} boolean?]
   [:header {:optional true} string?]
   [:text string?]
   [:buttons {:optional true} [:* :any]]])
(def valid-prompt-opts? (m/validator PromptOptsSchema))
(def explain-prompt-opts (m/explainer PromptOptsSchema))

(defn validate-prompt-opts [props]
  (assert (valid-prompt-opts? props)
          (-> (explain-prompt-opts props)
              (me/humanize)
              (pr-str)))
  props)

(defn set-prompt
  [this props]
  (let [{:keys [select-card header text buttons]} (validate-prompt-opts props)]
    (-> this
        (assoc
          :select-card (if (some? select-card) select-card false)
          :header (or header "")
          :text text
          :buttons (or (not-empty buttons) []))
        (validate-prompt-state))))

(defn clear-prompt
  [this]
  (-> this
      (assoc
        :select-card false
        :header ""
        :text ""
        :buttons [])
      (validate-prompt-state)))

(defn prompt-text
  [game player]
  (get-in game [player :prompt-state :text]))

(defn prompt-header
  [game player]
  (get-in game [player :prompt-state :header]))
