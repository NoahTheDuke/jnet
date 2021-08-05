(ns jnet.ui-prompt
  (:require [jnet.game :as game]
            [jnet.player :as player]
            [jnet.pipeline :as pipeline]))

(defn- set-prompts
  [game step]
  (let [[ap nap] (if ((:active-condition step) game :corp)
                   [:corp :runner]
                   [:runner :corp])]
    (-> game
        (update ap game/set-prompt (:active-prompt step))
        (update nap game/set-prompt (:waiting-prompt step)))))

(defn- clear-prompts
  [game]
  (-> game
      (update :corp player/clear-prompt)
      (update :runner player/clear-prompt)))

(defn ui-prompt
  [{:keys [active-prompt waiting-prompt] :as step}]
  (merge
    (pipeline/make-step step)
    {:completed false
     :continue (fn [game step]
                 (if-let [completed (:completed step)]
                   (set-prompts game step)
                   (clear-prompts game)))
     :active-condition (fn [game side] true)
     :active-prompt (or active-prompt {:menu-title "Do the thing"})
     :waiting-prompt (or waiting-prompt {:menu-title "Waiting for opponent"})}))
