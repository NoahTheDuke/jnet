(ns engine.pipeline
  (:require
   [engine.steps.step :as step]))

(defn queue-step
  [game step]
  (step/validate step)
  (update-in game [:gp :queue] conj step))

(defn get-current-step
  [game]
  (peek (get-in game [:gp :pipeline])))

(defn complete-current-step
  [game]
  (update-in game [:gp :pipeline] #(conj (rest %)
                                         (assoc (first %) :complete? true))))

(defn drop-current-step
  [game]
  (update-in game [:gp :pipeline] rest))

(defn update-pipeline
  [game]
  (let [queue (get-in game [:gp :queue])]
    (-> game
        (assoc-in [:gp :queue] [])
        (update-in [:gp :pipeline] into (reverse queue)))))

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
