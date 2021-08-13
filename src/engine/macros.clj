(ns engine.macros 
  (:require [engine.steps.step :as step]
            [engine.pipeline :as pipeline]))
  
;TODO move this into a sensible file and actually make it work and stuff
(defn check-effect-prevention [game effect]
	false)
  
(defn build-step-name [fname]
  (symbol (str (clojure.string/capitalize fname) "Step")))
  
(defn build-keyword [fname]
  [(keyword (str "effect-" fname))])

(defn build-effect-body [func-name [game & args] & body]
  body)

(defn build-effect-record [func-name [game & args] & body]
  (let [step-name (build-step-name func-name)]
    `(defrecord ~step-name ~(into [] (conj args 'uuid))
        step/Step
        ~(concat '(continue-step [this game])
          `(~(concat `(let ~(into [] (reduce concat (map #(identity [% `(~(keyword %) ~'this)]) args))))
                  (apply build-effect-body (concat [func-name (concat [game] args)] body)))))
        (~'blocking [~'_ ~'__] false) ; We never halt execution.
        (~'on-prompt-clicked [~'this ~'game ~'player ~'arg] );TODO throw an error here, should never happen
        (~'validate [~'this] ))))
        
(defn build-effect-function [func-name [game & args] & body]
  (let [step-name (build-step-name func-name)
        effect-keyword (build-keyword func-name)]
  `(defn ~func-name ~(into [] (concat [game] args))
     (if (check-effect-prevention ~game ~effect-keyword)
       ~game
       (pipeline/queue-step ~game (->> ~(into `{:type ~(str ":step/" func-name)
                                                :uuid (java.util.UUID/randomUUID)}
                                              (map #(identity [ (keyword %) %]) args))
                                       (~(symbol (str "map->" step-name)))))))))

;TODO error if no game argument, plus a ton of functionality
(defmacro defeffect-full 
  [func-name [game & args] & body]
  (list 'do (apply build-effect-record (concat [func-name (concat [game] args)] body))
        (apply build-effect-function (concat [func-name (concat [game] args)] body))))

;Note that this is defined as a macro because its main utility is to provide access to (queue-step this).
(defmacro queue-simple-step 
  [game & body]
    `(pipeline/queue-step ~game (step/make-base-step {:continue-step 
       ~(concat '(fn [this game])
              body)})))
