(ns engine.pipeline
  (:require
   [com.rpl.specter :refer [AFTER-ELEM ALL BEGINNING FIRST NONE select setval]]
   [engine.steps.step-protocol :refer [continue-step on-prompt-clicked validate]]))

(defn queue-step
  [game step]
  (validate step)
  (setval [:gp :queue AFTER-ELEM] step game))

(defn get-current-step
  [game]
  (get-in game [:gp :pipeline 0]))

(defn complete-current-step
  [game]
  (assoc-in game [:gp :pipeline 0 :complete?] true))

(defn drop-current-step
  [game]
  (setval [:gp :pipeline FIRST] NONE game))

(defn update-pipeline
  [game]
  (let [queue (select [:gp :queue ALL] game)]
    (->> game
         (setval [:gp :queue ALL] NONE)
         (setval [:gp :pipeline BEGINNING] queue))))

(defn continue-game
  [game]
  (let [game (update-pipeline game)]
    (if-let [step (get-current-step game)]
      (let [[result new-game] (continue-step step game)]
        (if result
          (recur (drop-current-step new-game))
          [false new-game]))
      [true game])))

(defn handle-prompt-clicked
  [game player button]
  (if-let [step (get-current-step game)]
    (on-prompt-clicked step game player button)
    [false game]))
