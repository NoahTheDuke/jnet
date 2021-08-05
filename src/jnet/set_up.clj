(ns jnet.set-up
  (:require [jnet.card :refer [build-card]]
            [jnet.player :refer [new-corp new-runner init-player]]
            [jnet.game :refer [new-game]]))

(defn create-deck
  [deck]
  (->> (:cards deck)
       (mapcat #(repeat (:qty %) (:card %)))
       (map build-card)
       (map #(assoc % :zone :deck))
       (shuffle)))

(defn init-game
  [{:keys [game-id name corp runner]}]
  (let [corp-deck (create-deck (:deck corp))
        runner-deck (create-deck (:deck runner))
        corp-identity (build-card (or (get-in corp [:deck :identity])
                                      {:title "Custom Biotics: Engineered for Success"}))
        runner-identity (build-card (or (get-in runner [:deck :identity])
                                        {:title "The Professor: Keeper of Knowledge"}))
        corp (new-corp (:user corp) corp-identity corp-deck)
        runner (new-runner (:user runner) runner-identity runner-deck)]
    (new-game
      {:game-id game-id
       :name name
       :corp (init-player corp)
       :runner (init-player runner)})))
