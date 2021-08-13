(ns engine.steps.phase-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.test-helper :refer [block-step]]
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
           (-> (game/new-game nil)
               (sut/queue-phase-steps [#(pipeline/queue-step % step1) #(pipeline/queue-step % step2) #(pipeline/queue-step % step3)])
               (get-in [:gp :queue]))))))

(deftest make-phase-test
  (let [step1 (step/simple-step (fn [g] g))
        step2 (step/simple-step (fn [g] g))]
    (is (= 1 (-> (game/new-game nil)
                 (pipeline/queue-step (sut/make-phase))
                 (get-in [:gp :queue])
                 (count))))
    (testing "without steps, start and end happen immediately"
      (is (= 0 (-> (game/new-game nil)
                   (pipeline/queue-step (sut/make-phase))
                   (pipeline/continue-game)
                   (get-in [:gp :queue])
                   (count)))))
    (testing "steps are queued"
      (is (= 4 (-> (game/new-game nil)
                   (pipeline/queue-step (sut/make-phase
                                      {:steps [block-step #(pipeline/queue-step % step1) #(pipeline/queue-step % step2)]}))
                   (pipeline/continue-game)
                   (get-in [:gp :pipeline])
                   (count)))))
    (is (= :phase/start-of-turn
           (-> (game/new-game nil)
               (pipeline/queue-step (sut/make-phase
                                  {:phase :phase/start-of-turn
                                   :steps [block-step]}))
               (pipeline/continue-game)
               (:current-phase))))
    (is (nil? (-> (game/new-game nil)
                  (pipeline/queue-step (sut/make-phase
                                     {:phase :phase/start-of-turn}))
                  (pipeline/continue-game)
                  (:current-phase))))))
