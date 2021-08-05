(ns jnet.pipeline)

(defn make-pipeline []
  {:pipeline []
   :queue []})

(defn make-step
  [{:keys [continue step-fn card-clicked menu-command]}]
  {:continue (or continue false)
   :step-fn (or step-fn (constantly false))
   :card-clicked (or card-clicked (constantly false))
   :menu-command (or menu-command (constantly false))})

(defn queue-step
  [game step]
  (update-in game [:gp :queue] conj step))

(defn get-current-step
  [{pipeline :pipeline}]
  (first pipeline))

(defn cancel-step
  [game]
  (update-in game [:gp :pipeline] #(into [] (next %))))

(defn prepare-current-step
  [game]
  (let [gp (:gp game)
        gp (assoc gp
                   :pipeline (into [] (concat (:queue gp) (:pipeline gp)))
                   :queue []
                   :continue-step nil)
        game (assoc game :gp gp)]
    (if (empty? (:pipeline gp))
      game
      (let [step (get-current-step gp)
            continue? (:continue step)
            result (if (fn? continue?)
                     (continue? game step)
                     continue?)]
        (assoc game :gp (assoc gp :continue-step result))))))

(defn continue
  [game]
  (let [{:keys [gp] :as game} (prepare-current-step game)]
    (if (:continue-step gp)
      (let [step-fn (:step-fn (get-current-step gp))]
        (-> game
            (step-fn)
            (update-in [:gp :pipeline] #(into [] (next %)))
            (recur)))
      game)))

(defn draw
  [game side qty]
  (let [cards (take qty (get-in game [side :deck]))]
    (-> game
        (update-in [side :deck] #(drop qty %))
        (update-in [side :hand] #(into [] (concat % cards))))))
