(ns engine.messages
  (:require
    [clojure.string :as str]))

(defn strip-for-message
  [obj]
  (select-keys obj [:id :name :type :uuid]))

(defn format-message
  [message args]
  (let [args (into [] args)]
    (reduce
      (fn [output cur]
        (if-let [[_ idx] (re-find #"\{(\d+)\}" cur)]
          (let [match (nth args (Integer/parseInt idx))]
            (if (map? match)
              (conj output (strip-for-message match))
              (conj output match)))
          (->> (str/split cur #" ")
               (remove empty?)
               (interpose " ")
               (into output))))
      []
      (str/split message #"((?<=\{\d\d?\})|(?=\{\d\d?\}))"))))

(defn add-message
  ([game message] (add-message game message nil))
  ([game message args]
   (update game :messages conj {:date (java.util.Date.)
                                :text (format-message message args)})))
