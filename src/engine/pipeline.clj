(ns engine.pipeline
  (:require
   [com.rpl.specter :refer [AFTER-ELEM FIRST NONE setval]]
   [engine.steps.step-protocol :refer [validate]]))

(defn queue-step
  [game step]
  (validate step)
  (setval [:gp :queue AFTER-ELEM] step game))

(defn get-current-step
  [game]
  (get-in game [:gp :pipeline 0]))

(defn drop-current-step
  [game]
  (setval [:gp :pipeline FIRST] NONE game))
