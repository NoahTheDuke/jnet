(ns engine.draw
  (:require [engine.steps.effect :refer [defeffect-full]]
            [engine.macros :refer [set-result]]))

(defeffect-full draw
  [game player amount]
  (let [[drawn deck] (split-at amount (get-in game [player :deck]))]
    (-> game
        (update-in [player :hand] into drawn)
        (assoc-in [player :deck] (into [] deck))
        (set-result amount))))
