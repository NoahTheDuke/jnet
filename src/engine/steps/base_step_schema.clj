(ns engine.steps.base-step-schema
  (:require
   [malli.core :as m]))

(def BaseStepSchema
  [:map {:closed true}
   [:complete? boolean?]
   [:continue [:=> [:cat :map :map] [:cat :boolean :any]]]
   ; [:on-card-clicked [:=> [:cat :map] [:boolean any?]]]
   ; [:on-prompt-clicked [:=> [:cat :map] [:boolean any?]]]
   [:type [:qualified-keyword {:namespace :step}]]
   [:uuid uuid?]])

(def validate-base-step (m/validator BaseStepSchema))
(def explain-base-step (m/explainer BaseStepSchema))
