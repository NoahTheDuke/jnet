(ns engine.steps.base-step
  (:require
    [engine.steps.step-protocol :refer [validate Step]]
    [malli.core :as m]
    [malli.error :as me]))

(def BaseStepSchema
  [:map {:closed true}
   [:complete? boolean?]
   [:continue-step [:=> [:cat :map :map] [:cat :boolean :any]]]
   ; [:on-card-clicked [:=> [:cat :map] [:boolean any?]]]
   ; [:on-prompt-clicked [:=> [:cat :map] [:boolean any?]]]
   [:type [:qualified-keyword {:namespace :step}]]
   [:uuid uuid?]])

(def validate-base-step (m/validator BaseStepSchema))
(def explain-base-step (m/explainer BaseStepSchema))

(defrecord BaseStep
  [complete? continue-step type uuid]
  Step
  (continue-step [this state] (continue-step this state))
  (complete? [this] (:complete? this))
  ; (on-card-clicked [_this _game _player _card])
  ; (on-prompt-clicked [_this _game _arg])
  (validate [this]
    (if (validate-base-step this)
      this
      (let [explained-error (explain-base-step (into {} this))]
        (throw (ex-info (str "Base step isn't valid: " (pr-str (me/humanize explained-error)))
                        explained-error))))))

(defn default-continue-step [_step game] [true game])

(defn make-base-step
  ([] (make-base-step nil))
  ([{:keys [continue-step]}]
   (->> {:complete? false
         :continue-step (or continue-step default-continue-step)
         ; :on-card-clicked (constantly nil)
         ; :on-prompt-clicked (constantly nil)
         :type :step/base
         :uuid (java.util.UUID/randomUUID)}
        (map->BaseStep)
        (validate))))

(defn simple-step
  [continue-step]
  (make-base-step {:continue-step continue-step}))
