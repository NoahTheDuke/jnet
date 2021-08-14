(ns engine.steps.phase
  (:require
   [engine.pipeline :as pipeline]
   [engine.steps.step :refer [simple-step]]
   [engine.macros :refer [defstep as-step]]
   [malli.core :as m]))

(defstep start-phase
  [game phase]
    (-> game
        (assoc :current-phase (keyword "phase" (name phase)))))

(defstep end-phase [game]
  (-> game
      (assoc :current-phase nil)))

(defn queue-phase-steps
  [game {:keys [phase steps]
         :or {phase :base steps identity}}]
  (-> game (start-phase phase)
           (steps)
           (end-phase)))

(def PhaseOptsSchema
  [:map {:closed true}
   [:phase {:optional true} :keyword]
   [:steps {:optional true} [:=> [:cat :map] [:cat :map]]]])

(def validate-opts (m/validator PhaseOptsSchema))
(def explain-opts (m/explainer PhaseOptsSchema))

(defn make-phase
  "A wrapper around simple-step that queues start-phase and end-phase steps automatically.
  If :condition is a function that returns true, the phase will be run. Otherwise, the step will exit."
  ([] (make-phase {}))
  ([{condition :condition :as opts}]
   (assert (validate-opts opts) (:errors (explain-opts opts)))
   (-> (simple-step
         (fn [game]
           (queue-phase-steps game opts)))
       (assoc :type :step/phase))))

(defn phase [game & args]
  (pipeline/queue-step game (apply make-phase args)))
