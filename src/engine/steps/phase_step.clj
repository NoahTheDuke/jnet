(ns engine.steps.phase-step
  (:require
   [engine.steps.base-step :refer [BaseStepSchema default-continue-step]]
   [engine.steps.step-protocol :refer [Step validate]]
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]))

(def PhaseStepSchema
  (mu/merge
    BaseStepSchema
    [:map {:closed true}
     [:phase [:qualified-keyword {:namespace :phase}]]]))

(def validate-phase-step (m/validator PhaseStepSchema))
(def explain-phase-step (m/explainer PhaseStepSchema))

(defrecord PhaseStep
  [continue-step type uuid]
  Step
  (continue-step [this state] (continue-step this state))
  (complete? [_])
  (on-prompt-clicked [_this game _player _arg] [false game])
  (validate [this]
    (if (validate-phase-step this)
      this
      (let [explained-error (explain-phase-step (into {} this))]
        (throw (ex-info (str "Phase step isn't valid: " (pr-str (me/humanize explained-error)))
                        explained-error))))))

(defn make-phase-step
  ([] (make-phase-step nil))
  ([{:keys [continue-step phase]}]
   (->> {:continue-step (or continue-step default-continue-step)
         :phase (or phase :phase/base)
         :type :step/phase
         :uuid (java.util.UUID/randomUUID)}
        (map->PhaseStep)
        (validate))))
