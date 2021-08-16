(ns engine.deck 
  (:require
    [engine.card :as card]
    [malli.core :as m]))

(defn prepare-deck
  [player deck])

(def DeckListSchema
  [:* card/PrintedCard])

(def validate-deck-list (m/validator DeckListSchema))
(def explain-deck-list (m/explainer DeckListSchema))
