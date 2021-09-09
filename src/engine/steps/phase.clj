(ns engine.steps.phase
  (:require
   [engine.pipeline :as pipeline]
   [engine.steps.step :refer [defstep make-base-step]]
   [malli.core :as m]
   [malli.error :as me]))

(defstep start-phase
  [phase]
  (-> game
      (assoc :current-phase (keyword "phase" (name phase)))))

(defstep end-phase []
  (-> game
      (assoc :current-phase nil)))

(defn initialize-steps
  [{:keys [phase steps]
    :or {phase :base}}]
  (let [start-step (start-phase phase)
        end-step (end-phase)]
    (-> [start-step]
        (into steps)
        (into [end-step]))))

(defn queue-phase-steps
  [game steps]
  (reduce pipeline/queue-step game steps))

(def PhaseOptsSchema
  [:map {:closed true}
   [:condition {:optional true} [:=> [:cat :map] :boolean]]
   [:phase {:optional true} :keyword]
   [:steps {:optional true} [:* :any]]])
(def validate-opts (m/validator PhaseOptsSchema))
(def explain-opts (m/explainer PhaseOptsSchema))

(defn make-phase
  "A wrapper around simple-step that queues start-phase and end-phase steps automatically.
  If :condition is a function that returns true, the phase will be run. Otherwise, the step will exit."
  ([] (make-phase {}))
  ([{condition :condition :as opts}]
   (assert (validate-opts opts) (pr-str (me/humanize (explain-opts opts))))
   (assoc
     (make-base-step {:continue-step
                      (fn [_ game]
                        [true
                         (if (or (not (fn? condition))
                                 (condition game))
                           (queue-phase-steps game (initialize-steps opts))
                           game)])})
       :type :step/phase)))
