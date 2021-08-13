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
      (if-let [prompt-game (step/blocking step game)]
        prompt-game
        (let [new-game (step/continue-step step game)]
          (recur (drop-current-step new-game))))
      game)));This should probably be an error, empty pipeline shouldn't happen

(defn handle-prompt-clicked
  [game player button]
  (if-let [step (get-current-step game)]
    (let [step (step/on-prompt-clicked step game player button)]
      (-> game (drop-current-step)
               (queue-step step)
               (continue-game)))
    game))
