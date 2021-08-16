(ns engine.deck
  (:require
   [engine.card :as card]))

(def DeckListSchema
  [:* card/PrintedCard])

(defn prepare-deck
  [deck]
  (map-indexed
    (fn [idx card]
      (assoc card :location idx :zone :zone/deck))
    deck))

(defn shuffle-deck
  [deck]
  (->> (shuffle deck)
       (map-indexed
         (fn [idx card]
           (assoc card :location idx)))))
