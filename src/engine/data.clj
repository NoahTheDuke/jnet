(ns engine.data
  (:require
    [engine.card :as card]
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.edn :as edn]))

(defn normalize-text [s]
  (some-> (not-empty s)
          (name)
          (java.text.Normalizer/normalize java.text.Normalizer$Form/NFD)
          (str/replace #"[\P{ASCII}]+" "")
          (str/trim)))

(defn slugify
  "As defined here: https://you.tools/slugify/"
  ([s] (slugify s "-"))
  ([s sep]
   (if (nil? s) ""
     (as-> s s
       (normalize-text s)
       (str/lower-case s)
       (str/split s #"[\p{Space}\p{Punct}]+")
       (filter seq s)
       (str/join sep s)))))

(defn clean-card-data
  "This won't be necessary after all work is complete and we switch to this engine,
  but for now, gotta maintain backwards compatibility with the existing data."
  [card]
  (-> card
      (select-keys [:advancementcost :agendapoints :baselink :cost
                    :faction :memoryunits :normalizedtitle :side :strength
                    :subtypes :text :title :trash :type :uniqueness])
      (set/rename-keys {:advancementcost :advancement-requirement
                        :agendapoints :agenda-points
                        :baselink :base-link
                        :memoryunits :memory-cost
                        :normalizedtitle :id
                        :title :name
                        :trash :trash-cost})
      (update :faction (comp keyword slugify))
      (update :side (comp keyword slugify))
      (update :subtypes #(mapv (comp keyword slugify) %))
      (update :type (comp keyword slugify))))

(def card-data (atom nil))

(defn make-card [id]
  (let [card (get @card-data id)]
    (-> card
        ; (assoc :printed-advancement-requirement (:advancement-requirement card))
        ; (assoc :printed-agenda-points (:agenda-points card))
        ; (assoc :printed-strength (:strength card))
        ; (assoc :printed-subtypes (:subtypes card))
        )))

(defn load-card-data []
  (->> (io/resource "engine/raw_data.edn")
       (slurp)
       (edn/read-string)
       (:cards)
       (map clean-card-data)
       (map card/map->Card)
       (map (juxt :id identity))
       (into {})
       (reset! card-data)))

(defn make-cards-in-deck
  "Called by the server on the client's deck (from db or direct)"
  [{:keys [identity cards]}]
  (let [deck (->> cards
                  (mapcat #(repeat (:qty %) (:name %)))
                  (keep #(make-card %)))]
    {:deck-list deck
     :identity (make-card identity)}))
