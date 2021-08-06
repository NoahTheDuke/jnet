(ns engine.game)

(defn new-game [{:keys [corp runner]
                 :or {corp {} runner {}}}]
  {:corp corp
   :runner runner
   :gp {:pipeline []
        :queue []}
   :turns 0})
