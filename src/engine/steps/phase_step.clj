(ns engine.steps.phase-step
  (:require
   [engine.pipeline :refer [queue-step]]
   [engine.steps.base-step :refer [simple-step]]))

(defn start-phase
  [phase]
  (simple-step
    (fn [game]
      (-> game
          (assoc :current-phase phase)))))

(defn end-phase []
  (simple-step
    (fn [game]
      (-> game
          (assoc :current-phase nil)))))

(defn initialize-steps
  [{:keys [phase steps]
    :or {phase :phase/base}}]
  (let [start-step (start-phase phase)
        end-step (end-phase)]
    (-> [start-step]
        (into steps)
        (into [end-step]))))

(defn queue-phase-steps
  [game steps]
  (reduce queue-step game steps))

(defn make-phase-step
  "A wrapper around simple-step that queues start-phase and end-phase steps automatically."
  ([] (make-phase-step nil))
  ([opts]
   (simple-step
     (fn [game] (queue-phase-steps game (initialize-steps opts))))))
