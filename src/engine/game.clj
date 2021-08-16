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
   [engine.steps.step :refer [simple-step]]
   [malli.error :as me]
   [malli.core :as m]))

(defn make-game
  [{:keys [corp runner]}]
  {:corp (player/new-corp corp)
   :runner (player/new-runner runner)
   :gp {:pipeline []
        :queue []}
   :active-player :corp
   :inactive-player :runner
   :messages []
   :turns 0})

(defn switch-active-player []
  (simple-step
    (fn [{:keys [active-player] :as game}]
      (let [new-active (if (= active-player :corp) :runner :corp)]
        (-> game
            (assoc :active-player new-active)
            (update :turn #(if (= new-active :corp) (inc %) %)))))))

(defn begin-turn []
  (simple-step
    (fn [game]
      (-> game
          (pipeline/queue-step (start-of-turn-phase))
          (pipeline/queue-step (draw-phase))
          (pipeline/queue-step (action-phase))
          (pipeline/queue-step (discard-phase))
          (pipeline/queue-step (switch-active-player))
          (pipeline/queue-step (begin-turn))))))

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
          (me/humanize (explain-new-game-opts opts)))
  (-> (make-game opts)
      (pipeline/queue-step (setup-phase))
      (pipeline/queue-step (begin-turn))
      (pipeline/continue-game)))
