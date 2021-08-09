(ns engine.steps.prompt-step
  "Prompt Steps are steps that "
  (:require
   [engine.steps.base-step :refer [BaseStepSchema]]
   [engine.steps.step-protocol :refer [Step complete? validate]]
   [engine.player :refer [clear-player-prompt set-player-prompt]]
   [malli.core :as m]
   [malli.error :as me]
   [malli.util :as mu]))

(def PromptStepSchema
  (mu/merge
    BaseStepSchema
    [:map {:closed true}
     [:active-condition [:=> [:cat :map :map :keyword] :boolean]]
     [:active-prompt [:=> [:cat :map :map :keyword] :any]]
     [:waiting-prompt [:=> [:cat :map :map :keyword] :any]]]))

(def validate-prompt-step (m/validator PromptStepSchema))
(def explain-prompt-step (m/explainer PromptStepSchema))

(defrecord PromptStep
  [complete? continue-step type uuid]
  Step
  (continue-step [this game] (continue-step this game))
  (complete? [this] (:complete? this))
  ; (on-card-clicked [_this _game _player _card])
  ; (on-prompt-clicked [_this _game _arg])
  (validate [this]
    (if (validate-prompt-step this)
      this
      (let [explained-error (explain-prompt-step (into {} this))]
        (throw (ex-info (str "Prompt step isn't valid: " (pr-str (me/humanize explained-error)))
                        (select-keys explained-error [:errors])))))))

(defn set-active-prompt
  [game player {:keys [active-prompt] :as this}]
  (update game player set-player-prompt (active-prompt this game player)))

(defn set-waiting-prompt
  [game player {:keys [waiting-prompt] :as this}]
  (update game player set-player-prompt (waiting-prompt this game player)))

(defn set-prompt
  [{:keys [active-condition] :as this} game]
  (let [[active-player waiting-player]
        (if (active-condition this game :corp) [:corp :runner] [:runner :corp])]
    (-> game
        (set-active-prompt active-player this)
        (set-waiting-prompt waiting-player this))))

(defn clear-prompt
  [game]
  (-> game
      (update :corp clear-player-prompt)
      (update :runner clear-player-prompt)))

(defn prompt-continue-step
  [this game]
  (let [completed (complete? this)
        game (if completed
               (clear-prompt game)
               (set-prompt this game))]
    [completed game]))

(def default-waiting-prompt {:text "Waiting for opponent"})

(defn make-prompt-step
  ([] (make-prompt-step nil))
  ([{:keys [active-condition active-prompt waiting-prompt]}]
   (->> {:active-condition (or active-condition (constantly true))
         :active-prompt (or active-prompt (constantly nil))
         :waiting-prompt (or waiting-prompt (constantly default-waiting-prompt))
         :complete? false
         :continue-step prompt-continue-step
         ; :on-card-clicked (constantly nil)
         ; :on-prompt-clicked (constantly nil)
         :type :step/prompt
         :uuid (java.util.UUID/randomUUID)}
        (map->PromptStep)
        (validate))))

; (def ButtonSchema
;   [:map
;    [:text string?]
;    [:command string?]
;    [:uuid uuid?]])

; (def PromptSchema
;   [:map
;    [:msg string?]
;    [:buttons [:* ButtonSchema]]
;    [:effect [:fn fn?]]
;    [:card map?]
;    [:prompt-type keyword?]
;    [:show-discard boolean?]])

; (def validate-prompt (m/validator PromptSchema))

; (defn clear-prompt
;   [state]
;   (doseq [player [:corp :runner]]
;     (swap! state assoc-in [player :prompt-state] {})))

; (defn add-default-commands-to-buttons
;   [prompt]
;   (let [button-fn (fn [button]
;                     (assoc button
;                            :command (:command button :button)
;                            :uuid (:uuid prompt)))
;         buttons (mapv button-fn (:buttons prompt))]
;     (when (seq buttons)
;       (assoc prompt :buttons buttons))))

; (defn set-prompt
;   [step state]
;   (let [active-player (:active-player @step)
;         active-prompt (:active-prompt @step)
;         waiting-prompt (:waiting-prompt @step)]
;     (doseq [player [:corp :runner]]
;       (if (= player active-player)
;         (let [prompt (add-default-commands-to-buttons (active-prompt state))]
;           (assert (validate-prompt prompt) "Active prompt isn't valid")
;           (swap! state assoc-in [player :prompt-state] prompt))
;         (let [prompt (waiting-prompt state)]
;           (assert (validate-prompt prompt) "Waiting prompt isn't valid")
;           (swap! state assoc-in [player :prompt-state] prompt))))))

; (defn default-prompt-continue
;   [step & state]
;   (if (complete? step)
;       (do (clear-prompt state)
;           true)
;       (do (set-prompt step state)
;           false)))

; (defn ->PromptStep
;   "A step that displays a prompt to a given player"
;   [player active-prompt waiting-prompt]
;   (let [step (make-base-step :step/prompt default-prompt-continue)]
;     (vswap! step assoc
;             :active-player player
;             :active-prompt active-prompt
;             :waiting-prompt waiting-prompt)
;     (validate-step step)))
