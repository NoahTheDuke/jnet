(ns engine.pipeline
  (:require
   [com.rpl.specter :as specter]
   [engine.steps.step :as step]))

(defn queue-step
  [game step]
  (step/validate step)
  (specter/setval [:gp :queue specter/AFTER-ELEM] step game))

(defn get-current-step
  [game]
  (get-in game [:gp :pipeline 0]))

(defn complete-current-step
  [game]
  (assoc-in game [:gp :pipeline 0 :complete?] true))

(defn drop-current-step
  [game]
  (specter/setval [:gp :pipeline specter/FIRST] specter/NONE game))

(defn update-pipeline
  [game]
  (let [queue (specter/select [:gp :queue specter/ALL] game)]
    (->> game
         (specter/setval [:gp :queue specter/ALL] specter/NONE)
         (specter/setval [:gp :pipeline specter/BEGINNING] queue))))

(defn continue-game
  [game]
  (let [game (update-pipeline game)]
    (if-let [step (get-current-step game)]
      (let [[result new-game] (step/continue-step step game)]
        (if result
          (recur (drop-current-step new-game))
          new-game))
      game)))

(defn handle-prompt-clicked
  [game player button]
  (if-let [step (get-current-step game)]
    (step/on-prompt-clicked step game player button)
    game))
