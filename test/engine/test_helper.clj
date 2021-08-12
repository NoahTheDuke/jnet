(ns engine.test-helper
  (:require
   [clojure.string :as str]
   [engine.data :as data]
   [engine.pipeline :as pipeline]
   [malli.dev :as dev]
   [malli.dev.pretty :as pretty]))

(dev/start! {:report (pretty/reporter)})

(data/load-card-data)

(defn click-prompt
  [game player button]
  (let [prompt (get-in game [player :prompt-state])
        foundButton (some #(when (= (str/lower-case button)
                                    (str/lower-case (:text %))) %)
                          (:buttons prompt))]
    (if foundButton
      (-> game
          (pipeline/handle-prompt-clicked player (:arg foundButton))
          (second)
          (pipeline/continue-game)
          (second))
      (throw (ex-info (str "Can't find " button
                           " in current prompt for " player)
                      {:data prompt})))))

(defn get-messages
  "Dealing with the :date stuff is annoying so we're ignoring it here"
  [game]
  (->> game
       (:messages)
       (mapv #(dissoc % :date))))

(defn prompt-fmt
  [game player]
  (let [prompt (get-in game [player :prompt-state])
        header (:header prompt)
        text (:text prompt)
        buttons (:buttons prompt)]
    (println
      (str player ": " header "\n"
           text "\n"
           (str/join "\n" (map #(str "[ " (:text %) " ]") buttons)))))
  game)

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
