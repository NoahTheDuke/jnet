(ns jnet.card)

(def all-cards (atom {}))

(defn server-card
  [title]
  (let [card (get @all-cards title)]
    (if (and title card)
      card
      (println (str "Tried to select server-card for " title)))))

(defn build-card
  [card]
  (dissoc (or (server-card (:title card)) card)
          :cycle_code :deck-limit :factioncost :format :image_url :influence :influencelimit
          :minimumdecksize :number :quantity :rotated :set_code :setname :text))
