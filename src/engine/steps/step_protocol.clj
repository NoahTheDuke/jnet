(ns engine.steps.step-protocol)

(defprotocol Step
  "Base protocol for all steps. If a function operates on a step directly,
  even if it takes other args (such as the game state), it should be listed here
  and implemented for all steps."
  (continue-step [this game] "Calls the :continue function on the step. Should provide wrapping functionality in the protocol implementation.")
  (complete? [this] "Is the step complete?")
  (validate [this] "Validation through an external malli schema.")
  ; (on-card-clicked [this game player card] "What should happen when a card is clicked.")
  ; (on-prompt-clicked [this game arg] "What should happen when a button on a prompt is clicked.")
  )

(defn ^:private default-continue-step
  [this game]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this
                                                               :game game})))
(defn ^:private default-complete?
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private default-validate
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))

(extend-protocol Step
  Object
  (continue-step [this game] (default-continue-step this game))
  (complete? [this] (default-complete? this))
  (validate [this] (default-validate this))
  nil
  (continue-step [this game] (default-continue-step this game))
  (complete? [this] (default-complete? this))
  (validate [this] (default-validate this)))
