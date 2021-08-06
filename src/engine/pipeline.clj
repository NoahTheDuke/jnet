(ns engine.pipeline
  (:require
  ;   [com.rpl.specter :refer [setval AFTER-ELEM]]
    [engine.steps.step-protocol :refer [validate]]
    )
  )

(defn queue-step
  [game step]
  (validate step)
  (update-in game [:gp :queue] conj step)
  ; (setval [:gp :queue AFTER-ELEM] step game)
  )
