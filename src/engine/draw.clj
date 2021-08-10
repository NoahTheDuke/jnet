(ns engine.draw)

(defn draw
  [game player amount]
  (let [[drawn deck] (split-at amount (get-in game [player :deck]))]
    (-> game
        (update-in [player :hand] into drawn)
        (assoc-in [player :deck] (into [] deck)))))
