(ns engine.card
  (:require
   [malli.core :as m]
   [malli.error :as me]))

(def Card
  [:map
   [:advancement-requirement {:optional true} int?]
   [:agenda-points {:optional true} int?]
   [:base-link {:optional true} int?]
   [:cost {:optional true} int?]
   [:faction keyword?]
   [:id string?]
   [:memory-units {:optional true} int?]
   [:name string?]
   [:side keyword?]
   [:strength {:optional true} int?]
   [:subtypes [:* keyword?]]
   [:text {:optional true} string?]
   [:trash-cost {:optional true} int?]
   [:type keyword?]
   [:uniqueness boolean?]])

(defn map->Card [card]
  (if (m/validate Card card)
    card
    (let [reason (m/explain Card card)]
      (throw (ex-info (str "Card " card " isn't valid: "
                           (pr-str (me/humanize reason)))
                      (select-keys reason [:errors]))))))
