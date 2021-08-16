(ns engine.steps.phase-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.step :as step]
   [engine.steps.phase :as sut]))

(deftest initialize-steps-test
  (is (= 2 (count (sut/initialize-steps nil))))
  (is (= 3 (count (sut/initialize-steps
                    {:steps [(step/simple-step (constantly :foo))]})))))

(deftest queue-steps-test
  (let [step1 (step/simple-step (fn [g] g))
        step2 (step/simple-step (fn [g] g))
        step3 (step/simple-step (fn [g] g))]
    (is (= [step1 step2 step3]
           (-> (game/make-game nil)
               (sut/queue-phase-steps [step1 step2 step3])
               (get-in [:gp :queue]))))))

(deftest make-phase-test
  (let [step1 (step/make-base-step
                {:continue-step (fn [_ g] [false g])})
        step2 (step/simple-step (fn [g] g))
        step3 (step/simple-step (fn [g] g))]
    (is (= 1 (-> (game/make-game nil)
                 (pipeline/queue-step (sut/make-phase))
                 (get-in [:gp :queue])
                 (count))))
    (testing "without steps, start and end happen immediately"
      (is (zero? (-> (game/make-game nil)
                     (pipeline/queue-step (sut/make-phase))
                     (pipeline/continue-game)
                     (get-in [:gp :queue])
                     (count)))))
    (testing "steps are queued"
      (is (= 4 (-> (game/make-game nil)
                   (pipeline/queue-step (sut/make-phase
                                      {:steps [step1 step2 step3]}))
                   (pipeline/continue-game)
                   (get-in [:gp :pipeline])
                   (count)))))
    (is (= :phase/start-of-turn
           (-> (game/make-game nil)
               (pipeline/queue-step (sut/make-phase
                                  {:phase :phase/start-of-turn
                                   :steps [step1]}))
               (pipeline/continue-game)
               (:current-phase))))
    (is (nil? (-> (game/make-game nil)
                  (pipeline/queue-step (sut/make-phase
                                     {:phase :phase/start-of-turn}))
                  (pipeline/continue-game)
                  (:current-phase))))))
