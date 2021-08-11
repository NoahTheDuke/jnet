(ns engine.steps.phase-step-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as ppln]
   [engine.steps.base-step :as base-step]
   [engine.steps.phase-step :as sut]))

(deftest initialize-steps-test
  (let [start-step (-> sut/start-phase
                       (base-step/simple-step)
                       (assoc :phase :phase/base)
                       (dissoc :uuid))
        end-step (-> sut/end-phase
                     (base-step/simple-step)
                     (dissoc :uuid))]
    (is (= [start-step end-step]
           (->> (sut/initialize-steps nil)
                (map #(dissoc % :uuid)))))
    (let [extra-step (-> (constantly :foo)
                         (base-step/simple-step)
                         (dissoc :uuid))]
      (is (= [start-step
              extra-step
              end-step]
             (->> (sut/initialize-steps {:steps [extra-step]})
                  (map #(dissoc % :uuid))))))))

(deftest queue-steps-test
  (let [step1 (base-step/simple-step (fn [_ g] [true g]))
        step2 (base-step/simple-step (fn [_ g] [true g]))
        step3 (base-step/simple-step (fn [_ g] [true g]))]
    (is (= [step1 step2 step3]
           (-> (game/new-game nil)
               (sut/queue-phase-steps [step1 step2 step3])
               (get-in [:gp :queue]))))))

(deftest make-phase-step-test
  (let [step1 (base-step/simple-step (fn [_ g] [false g]))
        step2 (base-step/simple-step (fn [_ g] [true g]))
        step3 (base-step/simple-step (fn [_ g] [true g]))]
    (is (= 1 (-> (game/new-game nil)
                 (ppln/queue-step (sut/make-phase-step))
                 (get-in [:gp :queue])
                 (count))))
    (testing "without steps, start and end happen immediately"
      (is (= 0 (-> (game/new-game nil)
                   (ppln/queue-step (sut/make-phase-step))
                   (ppln/continue-game)
                   (get-in [:gp :queue])
                   (count)))))
    (testing "steps are queued"
      (is (= 4 (-> (game/new-game nil)
                   (ppln/queue-step (sut/make-phase-step
                                      {:steps [step1 step2 step3]}))
                   (ppln/continue-game)
                   (second)
                   (get-in [:gp :pipeline])
                   (count)))))
    (is (= :phase/start-of-turn
           (-> (game/new-game nil)
               (ppln/queue-step (sut/make-phase-step
                                  {:phase :phase/start-of-turn
                                   :steps [step1]}))
               (ppln/continue-game)
               (second)
               (:current-phase))))
    (is (nil? (-> (game/new-game nil)
                  (ppln/queue-step (sut/make-phase-step
                                     {:phase :phase/start-of-turn}))
                  (ppln/continue-game)
                  (second)
                  (:current-phase))))))
