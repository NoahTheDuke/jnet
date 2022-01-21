(ns engine.moving
  (:require
   [hyperfiddle.rcf :refer [tests]]))

(defn remove-card-from-zone
  [game {:keys [side uuid zone]}]
  (update-in game [side zone]
             #(reduce
                (fn [zone c]
                  (if (= uuid (:uuid c))
                    zone
                    (conj zone c)))
                []
                %)))

(tests
  (remove-card-from-zone
    {:corp {:deck [{:uuid 1} {:uuid 2} {:uuid 3}]}}
    {:side :corp :zone :deck :uuid 1})
  := {:corp {:deck [{:uuid 2} {:uuid 3}]}}
  (remove-card-from-zone
    {:corp {:deck [{:uuid 1} {:uuid 2} {:uuid 3}]}}
    {:side :corp :zone :deck :uuid 2})
  := {:corp {:deck [{:uuid 1} {:uuid 3}]}}
  (remove-card-from-zone
    {:corp {:deck [{:uuid 1} {:uuid 2} {:uuid 3}]}}
    {:side :corp :zone :deck :uuid 3})
  := {:corp {:deck [{:uuid 1} {:uuid 2}]}}
  (remove-card-from-zone
    {:corp {:deck [{:uuid 1} {:uuid 2} {:uuid 3}]}}
    {:side :corp :zone :deck :uuid 4})
  := {:corp {:deck [{:uuid 1} {:uuid 2} {:uuid 3}]}})
