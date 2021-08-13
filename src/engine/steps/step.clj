(ns engine.steps.step
  (:require
    [malli.core :as m]
    [malli.error :as me]))

(defprotocol Step
  "Base protocol for all steps. If a function operates on a step directly,
  even if it takes other args (such as the game state), it should be listed here
  and implemented for all steps."
  (continue-step [this game] "Calls the :continue function on the step. Should provide wrapping functionality in the protocol implementation.")
  (blocking [this game] "Should this step stop the engine. If not, this should be false. If so, it should be the state that is sent to the user.")
  (validate [this] "Validation through an external malli schema.")
  ; (on-card-clicked [this game player card] "What should happen when a card is clicked.")
  (on-prompt-clicked [this game player arg] "What should happen when a button on a prompt is clicked."))

(defn ^:private -continue-step
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private -blocking
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private -validate
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))
(defn ^:private -on-prompt-clicked
  [this]
  (throw (ex-info (str "Step <" this "> is not a valid step") {:step this})))

(extend-protocol Step
  Object
  (continue-step [this _game] (-continue-step this))
  (blocking [this _game] (-blocking this))
  (validate [this] (-validate this))
  (on-prompt-clicked [this _game _player _arg]
    (-on-prompt-clicked this))
  nil
  (continue-step [this _game] (-continue-step this))
  (blocking [this _game] (-blocking this))
  (validate [this] (-validate this))
  (on-prompt-clicked [this _game _player _arg]
    (-on-prompt-clicked this)))

(def BaseStepSchema
  [:map {:closed true}
   [:continue-step [:=> [:cat :any :map]
                    [:cat :any]]]
   [:type [:qualified-keyword {:namespace :step}]]
   [:uuid uuid?]])

(def validate-base-step (m/validator BaseStepSchema))
(def explain-base-step (m/explainer BaseStepSchema))

(defrecord BaseStep
  [continue-step type uuid]
  Step
  (continue-step [this state] (continue-step this state))
  (blocking [_ game] false) ;This returns false to continue processing, or a game state that will be sent to the client. It is called before continue-step.
  (on-prompt-clicked [_this game _player _arg] game)
  (validate [this]
    (if (validate-base-step this)
      this
      (let [explained-error (explain-base-step (into {} this))]
        (throw (ex-info (str "Base step isn't valid: " (pr-str (me/humanize explained-error)))
                        (select-keys explained-error [:errors])))))))

(defn default-continue-step [_step game] game)

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
    (continue-step game)))

(defn simple-step
  [continue-step]
  (make-base-step {:continue-step (simple-step-wrapper continue-step)}))
