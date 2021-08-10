(ns engine.data
  (:require
    [engine.card :as card]
    [clojure.java.io :as io]
    [clojure.set :as set]
    [clojure.edn :as edn]))

(def card-data (atom nil))

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
      (update :faction keyword)
      (update :side keyword)
      (update :subtypes #(mapv keyword %))
      (update :type keyword)))

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
