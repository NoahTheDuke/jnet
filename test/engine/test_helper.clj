(ns engine.test-helper
  (:require
   [clojure.string :as str]
   [engine.pipeline :as pipeline]
   [malli.dev :as dev]
   [malli.dev.pretty :as pretty]))

(dev/start! {:report (pretty/reporter)})

(defn click-prompt
  [game player button]
  (let [prompt (get-in game [player :prompt-state])
        foundButton (first (some #(when (= (str/lower-case button)
                                           (str/lower-case (:text %))) %)
                                 (:buttons prompt)))]
    (if foundButton
      (-> game
          (pipeline/handle-prompt-clicked player button)
          (second)
          (pipeline/continue-game))
      (throw (ex-info (str "Can't find " button
                           " in current prompt for " player)
                      {:data prompt})))))

; (when (empty? @all-cards)
;   (->> (io/file "data/cards.edn")
;        slurp
;        edn/read-string
;        merge
;        (map (juxt :title identity))
;        (into {})
;        (reset! all-cards)))


; (defn qty [card amt]
;   (when (pos? amt)
;     (repeat amt card)))

; (defn card-vec->card-map
;   [[card amt]]
;   (let [loaded-card (if (string? card) (server-card card) card)]
;     (when-not loaded-card
;       (throw (Exception. (str card " not found in @all-cards"))))
;     {:card loaded-card
;      :qty amt}))

; (defn transform
;   [cards]
;   (->> cards
;        flatten
;        (filter string?)
;        frequencies
;        (map card-vec->card-map)
;        seq))

; (defn make-decks
;   [{:keys [corp runner options]}]
;   {:corp {:deck (or (transform (conj (:deck corp)
;                                      (:hand corp)
;                                      (:discard corp)))
;                     (transform (qty "Hedge Fund" 10)))
;           :hand (when-let [hand (:hand corp)]
;                   (flatten hand))
;           :discard (when-let [discard (:discard corp)]
;                      (flatten discard))
;           :identity (when-let [id (:id corp)]
;                       (server-card id))}
;    :runner {:deck (or (transform (conj (:deck runner)
;                                        (:hand runner)
;                                        (:discard runner)))
;                       (transform (qty "Sure Gamble" 10)))
;             :hand (when-let [hand (:hand runner)]
;                     (flatten hand))
;             :discard (when-let [discard (:discard runner)]
;                        (flatten discard))
;             :identity (when-let [id (:id runner)]
;                         (server-card id))}})

; (defn make-game
;   ([] (make-game nil))
;   ([players]
;    (let [{:keys [corp runner]} (make-decks players)]
;      (init-game
;        {:gameid 1
;         :name "test game"
;         :corp {:side :corp
;                :user {:username "Corp"
;                       :id 1}
;                :deck {:identity (:identity corp)
;                       :cards (:deck corp)}}
;         :runner {:side :runner
;                  :user {:username "Runner"
;                         :id 2}
;                  :deck {:identity (:identity runner)
;                         :cards (:deck runner)}}}))))
