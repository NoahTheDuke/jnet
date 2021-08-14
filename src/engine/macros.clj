(ns engine.macros 
  (:require [engine.steps.step :as step]
            [engine.pipeline :as pipeline]))
  

(defmacro as-step
  [game & body]
    `(pipeline/queue-step ~game (step/make-base-step {:continue-step 
       ~(concat '(fn [this game])
              body)})))

(defmacro defstep
  [func-name & code]
  (let [[args & body] (if (string? (first code)) (rest code) code)];Trim docstrings to mimic defn, really we want to use a library like defntly to handle all this correctly
    `(defn ~func-name ~args ~(concat `(engine.macros/as-step ~(first args)) body))))
