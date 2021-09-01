(ns engine.steps.step
  (:require
    [malli.core :as m]
    [malli.error :as me]))

(defprotocol Step
  "Base protocol for all steps. If a function operates on a step directly,
  even if it takes other args (such as the game state), it should be listed here
  and implemented for all steps."
  (continue-step [this game] "Calls the :continue function on the step. Should provide wrapping functionality in the protocol implementation.")
  (complete? [this] "Is the step complete?")
  (validate [this] "Validation through an external malli schema."))

(defn ^:private -continue-step
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private -complete?
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private -validate
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private -on-prompt-clicked
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private -on-card-clicked
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))

(extend-protocol Step
  Object
  (continue-step [this _game] (-continue-step this))
  (complete? [this] (-complete? this))
  (validate [this] (-validate this))
  nil
  (continue-step [this _game] (-continue-step this))
  (complete? [this] (-complete? this))
  (validate [this] (-validate this)))

(defprotocol Prompt
  (on-prompt-clicked [this game player arg] "What should happen when a button on a prompt is clicked.")
  (on-card-clicked [this game player card] "What should happen when a card is clicked."))

(extend-protocol Prompt
  Object
  (on-prompt-clicked [this _game _player _arg] (-on-prompt-clicked this))
  (on-card-clicked [this _game _player _arg] (-on-card-clicked this))
  nil
  (on-prompt-clicked [this _game _player _arg] (-on-prompt-clicked this))
  (on-card-clicked [this _game _player _arg] (-on-card-clicked this)))

(def BaseStepSchema
  [:map {:closed true}
   [:continue-step [:=> [:cat :any :map]
                    [:cat :boolean :any]]]
   [:type [:qualified-keyword {:namespace :step}]]
   [:uuid uuid?]])
(def validate-base-step (m/validator BaseStepSchema))
(def explain-base-step (m/explainer BaseStepSchema))

(defrecord BaseStep
  [continue-step type uuid]
  Step
  (continue-step [this game] (continue-step this game))
  (complete? [_])
  (validate [this]
    (assert (validate-base-step this)
            (me/humanize (explain-base-step this)))
    this))

(defn default-continue-step [_step game] [true game])

(defn make-base-step
  ([] (make-base-step nil))
  ([{:keys [continue-step]}]
   (->> {:continue-step (or continue-step default-continue-step)
         :type :step/base
         :uuid (java.util.UUID/randomUUID)}
        (map->BaseStep)
        (validate))))

(defn simple-step-wrapper
  [continue-step]
  (fn simple-step-wrapper [_ game]
    [true (continue-step game)]))

(defn simple-step
  [continue-step]
  (make-base-step {:continue-step (simple-step-wrapper continue-step)}))
