(ns engine.steps.step-protocol)

(defprotocol IStep
  "Protocol for all steps. If a function operates on a step direclty,
  even if it takes other args (such as the game state), it should be listed here
  and implemented for all steps."
  (continue [this state] "Calls the :continue function on the step. Should provide wrapping functionality in the protocol implementation.")
  (complete? [this] "Is the step complete?")
  (validate [this] "Validation through an external malli schema.")
  ; (on-card-clicked [this state player card] "What should happen when a card is clicked.")
  ; (on-prompt-clicked [this state arg] "What should happen when a button on a prompt is clicked.")
  )

(extend-protocol IStep
  Object
  (continue [this _state]
    (throw (ex-info (str "Step <" this "> is not a valid step") {:data this})))
  (complete? [this]
    (throw (ex-info (str "Step <" this "> is not a valid step") {:data this})))
  (validate [this]
    (throw (ex-info (str "Step <" this "> is not a valid step") {:data this}))))
