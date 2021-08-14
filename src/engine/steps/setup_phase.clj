(ns engine.steps.setup-phase
  (:require
   [engine.draw :as draw]
   [engine.steps.mulligan-step :as mulligan]
   [engine.steps.phase :as phase]
   [engine.pipeline :refer [queue-step]]
   [engine.macros :refer [defstep as-step]]))

(defstep draw-initial-hands [game]
  (-> game
      (update-in [:corp :deck] shuffle)
      (update-in [:runner :deck] shuffle)
      (as-step (draw/draw-unsafe this game :corp 5)) ;Don't trigger effects due to this draw
      ;The as-step isn't really necessary here as the game has hardly started, just showing off the safe way to avoid interrupts/triggers/prevention etc. 
      ;In most cases it would be though, you're still changing the state and need to put your stuff in the pipeline.
      ;Note that the function takes "this" as an argument allow effects to use (queue-step this) and be recursive if they wish, mostly for consistency. Effects probably shouldn't be doing that.
      (as-step (draw/draw-unsafe this game :runner 5))))

(defn setup-phase [game]
  (phase/phase game
    {:phase :setup
     :steps #(-> % (draw-initial-hands)
                   (mulligan/mulligan-prompt :corp)
                   (mulligan/mulligan-prompt :runner))}))

