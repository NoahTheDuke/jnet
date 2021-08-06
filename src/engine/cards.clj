(ns engine.cards
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [integrant.core :as ig]))

; (defmethod ig/init-key :jinteki/cards [_ {{:keys [db]} :db}]
;   (let [cards (mc/find-maps db "cards" nil)
;         stripped-cards (map #(update % :_id str) cards)
;         all-cards (into {} (map (juxt :title identity) stripped-cards))
;         sets (mc/find-maps db "sets" nil)
;         cycles (mc/find-maps db "cycles" nil)
;         mwl (mc/find-maps db "mwls" nil)
;         latest-mwl (->> mwl
;                         (filter #(= "standard" (:format %)))
;                         (map (fn [e] (update e :date-start #(f/parse (f/formatters :date) %))))
;                         (sort-by :date-start)
;                         (last))
;         ;; Gotta turn the card names back to strings
;         latest-mwl (assoc latest-mwl
;                           :cards (reduce-kv
;                                    (fn [m k v] (assoc m (name k) v))
;                                    {}
;                                    (:cards latest-mwl)))]
;     (reset! cards/all-cards all-cards)
;     (reset! cards/sets sets)
;     (reset! cards/cycles cycles)
;     (reset! cards/mwl latest-mwl)
;     {:all-cards all-cards
;      :sets sets
;      :cycles cycles
;      :mwl latest-mwl}))

(defmethod ig/init-key ::cards [_ _]
  (-> (io/file "resources/game/cards.edn")
      (slurp)
      (edn/read-string)
      (merge)))

(defmethod ig/halt-key! ::cards [_ _]
  nil)

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
