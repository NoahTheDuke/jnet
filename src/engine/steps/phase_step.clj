(ns engine.steps.phase-step
  (:require
   [engine.pipeline :as pipeline]
   [engine.steps.step :refer [simple-step]]
   [malli.core :as m]))

(defn start-phase
  [phase]
  (simple-step
    (fn [game]
      (-> game
          (assoc :current-phase (keyword "phase" (name phase)))))))

(defn end-phase []
  (simple-step
    (fn [game]
      (-> game
          (assoc :current-phase nil)))))

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

(defn make-phase-step
  "A wrapper around simple-step that queues start-phase and end-phase steps automatically.
  If :condition is a function that returns true, the phase will be run. Otherwise, the step will exit."
  ([] (make-phase-step {}))
  ([{condition :condition :as opts}]
   (assert (validate-opts opts) (:errors (explain-opts opts)))
   (simple-step
     (fn [game]
       (if (or (not (fn? condition))
               (condition game))
         (queue-phase-steps game (initialize-steps opts))
         game)))))
