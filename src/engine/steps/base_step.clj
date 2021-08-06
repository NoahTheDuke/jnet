(ns engine.steps.base-step
  (:require
    [engine.steps.step-protocol :refer [validate IStep]]
    [engine.steps.base-step-schema :refer [validate-base-step explain-base-step]]
    [malli.error :as me]))

(defrecord BaseStep
  [complete? continue type uuid]
  IStep
  (continue [this state] (continue this state))
  (complete? [this] (:complete? this))
  ; (on-card-clicked [_this _game _player _card])
  ; (on-prompt-clicked [_this _game _arg])
  (validate [this]
    (if (validate-base-step this)
      this
      (let [explained-error (explain-base-step (into {} this))]
        (throw (ex-info (str "Base step isn't valid: " (pr-str (me/humanize explained-error)))
                        explained-error))))))

(defn make-base-step
  ([] (make-base-step nil))
  ([opts]
   (->> opts
        (merge {:complete? false
                :continue (constantly true)
                ; :on-card-clicked (constantly nil)
                ; :on-prompt-clicked (constantly nil)
                :type :step/base
                :uuid (java.util.UUID/randomUUID)})
        (map->BaseStep)
        (validate))))
