(ns engine.test-utils
  (:require
   [clojure.string :as str]
   [engine.data :as data]
   [engine.pipeline :as pipeline]
   [malli.dev :as dev]
   [malli.dev.pretty :as pretty]
   [engine.game :as game]))

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
          (pipeline/continue-game))
      (throw (ex-info (str "Can't find " button
                           " in current prompt for " player)
                      {:data prompt})))))

(defn get-messages
  "Dealing with the :date stuff is annoying so we're ignoring it here"
  [game]
  (->> game
       (:messages)
       (mapv #(dissoc % :date))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn print-messages
  [game]
  (println (get-messages game))
  game)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

(defn card-vec->card-map
  [[card amt]]
  {:name card
   :qty amt})

(defn transform
  [cards]
  (->> cards
       (flatten)
       (filter string?)
       (frequencies)
       (map card-vec->card-map)))

(defn make-decks
  [{:keys [corp runner]}]
  {:corp (data/make-cards-in-deck
           {:cards (transform (or (:deck-list corp)
                                  (repeat 10 "hedge-fund")))
            :identity (or (:id corp)
                          "the-syndicate-profit-over-principle")})
   :runner (data/make-cards-in-deck
             {:cards (transform (or (:deck-list runner)
                                    (repeat 10 "sure-gamble")))
              :identity (or (:id runner)
                            "the-catalyst-convention-breaker")})})

(comment
  (println (make-decks nil))
  )

(defn new-game
  ([] (new-game nil))
  ([players]
   (let [{:keys [corp runner]} (make-decks players)]
     (game/start-new-game
       {:corp (merge corp {:user {:username "Corp player"}})
        :runner (merge runner {:user {:username "Runner player"}})}))))

(defn a-deck [side]
  (case side
    :corp (mapv data/make-card (repeat 10 "hedge-fund"))
    :runner (mapv data/make-card (repeat 10 "sure-gamble"))))
