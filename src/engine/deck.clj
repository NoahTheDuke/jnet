(ns engine.deck
  (:require
   [engine.card :as card]))

(def DeckListSchema
  [:* card/PrintedCard])

(defn prepare-deck
  [deck]
  (mapv #(assoc % :zone :deck) deck))

(defn shuffle-deck
  [deck]
  (shuffle deck))
