(ns engine.steps.effect
  (:require
   [engine.pipeline :refer [queue-step]]
   [engine.steps.step :as step]))

(defn check-effect-prevention [game type values]
  false)

(defrecord EffectStep
  [uuid continue-step values type];I'm not sure if we should include player as a value all effects have?
  step/Step
  (blocking [this game] false);Effect steps don't block
  (validate [this] false);TODO!
  (on-prompt-clicked [_this game _player _click] game);This should maybe throw, no prompts here

  (continue-step [this game]
    ;TODO all the automatic stuff like triggers etc, the type field is the trigger keyword
    (if (check-effect-prevention game type values)
        game
        (apply continue-step this game values))))


;Do not call this directly, call the function named after the effect you want.
(defn queue-effect-step [game continue-step values type]
  (->> {:continue-step continue-step
        :values values
        :type type}
       (map->EffectStep)
       (queue-step game)))

;I'm only doing defeffect-full atm, lighter versions will be available
(defmacro defeffect-full
  [effect-name args & body]
  (let [unsafe-name (symbol (str effect-name "-unsafe"))
        type-name (keyword effect-name)] ;Maybe a suffix or prefix here. This is the keyword you use for trigger etc
    `(do (defn ~unsafe-name ~(into [] (concat ['this] args)) ~@body) ;Define the function given, with -unsafe appended to its name and "this" prepended to its arguments
         (defn ~effect-name [~'game & ~'values] ;Define a function with the given name that queues an appropriate effect step ;TODO preserve arity
           (queue-effect-step ~'game ~unsafe-name ~'values ~type-name)))))
