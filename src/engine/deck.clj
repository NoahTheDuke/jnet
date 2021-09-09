(ns engine.deck
  (:require
   [engine.card :as card]))

(def DeckListSchema
  [:* card/PrintedCard])

(defn prepare-deck
  [deck]
  (map #(assoc % :zone :zone/deck) deck))

(defn shuffle-deck
  [deck]
  (shuffle deck))
