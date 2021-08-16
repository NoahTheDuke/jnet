(ns engine.macros
  (:require [engine.steps.step :as step]
            [engine.pipeline :as pipeline]))


(defmacro as-step
  [game & body]
    `(pipeline/queue-step ~game (step/make-base-step {:continue-step
       (fn [~'this ~'game] ~@body)})))

(defmacro defstep
  [func-name & code]
  (let [[args & body] (if (string? (first code)) (rest code) code)];Trim docstrings to mimic defn, really we want to use a library like defntly to handle all this correctly
    `(defn ~func-name ~args (engine.macros/as-step ~(first args) ~@body))))


(defstep set-result
  [game result]
  (assoc-in game [:step-result] result))

(defmacro let-step
  [game bindings & body]
  (if (< 2 (count bindings));If there's more than 1 var being bound split off the first and recur
      (let [cur-bind (into [] (take 2 bindings))
            others (into [] (drop 2 bindings))]
        `(let-step ~game ~cur-bind (let-step ~'game ~others ~@body)))
      (let [bind-var (first bindings)
            bind-val (second bindings)]
        `(let [~'__result-atom__ (atom nil)
               ~'game (set-result ~game nil) ;Make sure we get nil if the step doesn't set anything.
               ~'game (as-step ~'game ~bind-val) ;The as-step isn't strictly necessary but allows safely examining state from bind values
               ~'game (as-step ~'game (reset! ~'__result-atom__ (get-in ~'game [:step-result])) ~'game)]
           (as-step ~'game (let [~bind-var (deref ~'__result-atom__)]
                      ~@body))))))
