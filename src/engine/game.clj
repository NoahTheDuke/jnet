(ns engine.game
  (:require
   [engine.card :as card]
   [engine.deck :as deck]
   [engine.pipeline :as pipeline]
   [engine.player :as player]
   [engine.steps.action-phase :refer [action-phase]]
   [engine.steps.discard-phase :refer [discard-phase]]
   [engine.steps.draw-phase :refer [draw-phase]]
   [engine.steps.setup-phase :refer [setup-phase]]
   [engine.steps.start-of-turn-phase :refer [start-of-turn-phase]]
   [engine.steps.step :refer [defstep]]
   [malli.core :as m]
   [malli.error :as me]))

(defn make-game
  [{:keys [corp runner]}]
  {:corp (player/new-corp corp)
   :runner (player/new-runner runner)
   :gp {:pipeline '()
        :queue []}
   :active-player :corp
   :inactive-player :runner
   :messages []
   :turns 0})

(defstep switch-active-player []
  (let [{:keys [active-player]} game
        new-active (if (= active-player :corp) :runner :corp)]
    (-> game
        (assoc :active-player new-active)
        (update :turn #(if (= new-active :corp) (inc %) %)))))

(defstep begin-turn []
  (-> game
      (start-of-turn-phase)
      (draw-phase)
      (action-phase)
      (discard-phase)
      (pipeline/queue-step (switch-active-player))
      (pipeline/queue-step (begin-turn))))

(def NewPlayerOptsSchema
  [:map
   [:user [:map [:username string?]]]
   [:identity card/PrintedCard]
   [:deck-list deck/DeckListSchema]])

(def NewGameOptsSchema
  [:map
   [:corp NewPlayerOptsSchema]
   [:runner NewPlayerOptsSchema]])
(def validate-new-game-opts (m/validator NewGameOptsSchema))
(def explain-new-game-opts (m/explainer NewGameOptsSchema))

(defn start-new-game
  [opts]
  (assert (validate-new-game-opts opts)
          (pr-str (me/humanize (explain-new-game-opts opts))))
  (-> (make-game opts)
      (setup-phase)
      (pipeline/queue-step (begin-turn))
      (pipeline/continue-game)))
