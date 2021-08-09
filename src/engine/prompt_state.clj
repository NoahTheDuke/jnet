(ns engine.prompt-state
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(defrecord PromptState [player select-card header text buttons])

(def PromptStateSchema
  [:map {:closed true}
   [:player [:enum :corp :runner]]
   [:select-card boolean?]
   [:header string?]
   [:text string?]
   [:buttons [:* :any]]])

(def valid-prompt-state? (m/validator PromptStateSchema))
(def explain-prompt-state (m/explainer PromptStateSchema))

(defn validate-prompt-state [this]
  (if (valid-prompt-state? this)
    this
    (let [explained-error (explain-prompt-state (into {} this))]
      (throw (ex-info (str (:player this)
                           " prompt state isn't valid: "
                           (pr-str (me/humanize explained-error)))
                      explained-error)))))

(defn make-prompt-state
  [player]
  (-> {:player player
       :select-card false
       :header ""
       :text ""
       :buttons []}
      (map->PromptState)
      (validate-prompt-state)))

(def PromptOptsSchema
  (-> [:map {:closed true}
       [:select-card {:optional true} boolean?]
       [:header {:optional true} string?]
       [:text string?]
       [:buttons {:optional true} [:* :any]]]))

(def valid-prompt-opts? (m/validator PromptOptsSchema))
(def explain-prompt-opts (m/explainer PromptOptsSchema))

(defn validate-prompt-opts [props]
  (if (valid-prompt-opts? props)
    props
    (let [explained-error (explain-prompt-opts props)]
      (throw (ex-info (str "Prompt props aren't valid: "
                           (pr-str (me/humanize explained-error)))
                      (select-keys explained-error [:errors]))))))

(defn set-prompt
  [this {:keys [select-card header text buttons] :as props}]
  (validate-prompt-opts props)
  (-> this
      (assoc
        :select-card (if (some? select-card) select-card false)
        :header (or header "")
        :text text
        :buttons (or (not-empty buttons) []))
      (validate-prompt-state)))

(defn clear-prompt
  [this]
  (-> this
      (assoc
        :select-card false
        :header ""
        :text ""
        :buttons [])
      (validate-prompt-state)))
