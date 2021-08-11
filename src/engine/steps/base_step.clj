(ns engine.steps.base-step
  (:require
    [engine.steps.step-protocol :refer [validate Step]]
    [malli.core :as m]
    [malli.error :as me]))

(def BaseStepSchema
  [:map {:closed true}
   [:continue-step [:=> [:cat :any :map]
                    [:cat :boolean :any]]]
   [:type [:qualified-keyword {:namespace :step}]]
   [:uuid uuid?]])

(def validate-base-step (m/validator BaseStepSchema))
(def explain-base-step (m/explainer BaseStepSchema))

(defrecord BaseStep
  [continue-step type uuid]
  Step
  (continue-step [this state] (continue-step this state))
  (complete? [_])
  (on-prompt-clicked [_this game _player _arg] [false game])
  (validate [this]
    (if (validate-base-step this)
      this
      (let [explained-error (explain-base-step (into {} this))]
        (throw (ex-info (str "Base step isn't valid: " (pr-str (me/humanize explained-error)))
                        (select-keys explained-error [:errors])))))))

(defn default-continue-step [_step game] [true game])

(defn make-base-step
  ([] (make-base-step nil))
  ([{:keys [continue-step]}]
   (->> {:continue-step (or continue-step default-continue-step)
         :type :step/base
         :uuid (java.util.UUID/randomUUID)}
        (map->BaseStep)
        (validate))))

(defn simple-step
  [continue-step]
  (make-base-step {:continue-step continue-step}))
