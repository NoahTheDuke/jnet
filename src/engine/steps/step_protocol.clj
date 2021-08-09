(ns engine.steps.step-protocol)

(defprotocol Step
  "Base protocol for all steps. If a function operates on a step directly,
  even if it takes other args (such as the game state), it should be listed here
  and implemented for all steps."
  (continue-step [this game] "Calls the :continue function on the step. Should provide wrapping functionality in the protocol implementation.")
  (complete? [this] "Is the step complete?")
  (validate [this] "Validation through an external malli schema.")
  ; (on-card-clicked [this game player card] "What should happen when a card is clicked.")
  (on-prompt-clicked [this game player arg] "What should happen when a button on a prompt is clicked."))

(defn ^:private default-continue-step
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private default-complete?
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private default-validate
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private default-on-prompt-clicked
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))

(extend-protocol Step
  Object
  (continue-step [this _game] (default-continue-step this))
  (complete? [this] (default-complete? this))
  (validate [this] (default-validate this))
  (on-card-clicked [this _game _player _arg]
    (default-on-prompt-clicked this))
  nil
  (continue-step [this _game] (default-continue-step this))
  (complete? [this] (default-complete? this))
  (validate [this] (default-validate this))
  (on-card-clicked [this _game _player _arg]
    (default-on-prompt-clicked this)))
