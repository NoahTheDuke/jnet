(ns engine.steps.phase-step
  (:require
   [engine.pipeline :refer [queue-step]]
   [engine.steps.base-step :refer [BaseStepSchema simple-step]]
   [engine.steps.step-protocol :refer [Step]]
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
  (continue-step [this game] (continue-step this game))
  (complete? [_])
  (on-prompt-clicked [_this game _player _arg] [false game])
  (validate [this]
    (if (validate-phase-step this)
      this
      (let [explained-error (explain-phase-step (into {} this))]
        (throw (ex-info (str "Phase step isn't valid: " (pr-str (me/humanize explained-error)))
                        explained-error))))))

(defn start-phase
  [this game]
  [true (-> game
            (assoc :current-phase (:phase this)))])

(defn end-phase
  [_this game]
  [true (-> game
            (assoc :current-phase nil))])

(defn initialize-steps
  [{:keys [phase steps]
    :or {phase :phase/base}}]
  (let [start-step (-> (simple-step start-phase)
                       (assoc :phase phase)
                       (map->PhaseStep))
        end-step (simple-step end-phase)]
    (-> [start-step]
        (into steps)
        (into [end-step]))))

(defn queue-phase-steps
  [game steps]
  (reduce queue-step game steps))

(defn make-phase-step
  ([] (make-phase-step nil))
  ([opts]
   (simple-step
     (fn [_ game]
       (let [steps (initialize-steps opts)
             game (queue-phase-steps game steps)]
         [true game])))))
