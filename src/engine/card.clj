(ns engine.card
  (:require
   [malli.core :as m]))

(def PrintedCard
  [:map
   [:advancement-requirement {:optional true} int?]
   [:agenda-points {:optional true} int?]
   [:base-link {:optional true} int?]
   [:cost {:optional true} int?]
   [:faction keyword?]
   [:id string?]
   [:memory-units {:optional true} int?]
   [:name string?]
   [:side keyword?]
   [:strength {:optional true} int?]
   [:subtypes [:* keyword?]]
   [:text {:optional true} string?]
   [:trash-cost {:optional true} int?]
   [:type keyword?]
   [:uniqueness boolean?]
   [:uuid uuid?]])
(def validate-printed-card (m/validator PrintedCard))
(def explain-printed-card (m/explainer PrintedCard))

(defn map->Card [card]
  (assert (validate-printed-card card)
          (:errors (explain-printed-card card)))
  card)
