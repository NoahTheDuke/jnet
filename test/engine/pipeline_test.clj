(ns engine.pipeline-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as sut]
   [engine.steps.step :as step]
   [engine.steps.prompt :as prompt]))

(deftest queue-steps-test
  (let [game (game/new-game nil)
        step (step/make-base-step)
        step2 (step/make-base-step)]
    (testing "Step is correctly queued"
      (is (= {:pipeline [] :queue [step]} (:gp (sut/queue-step game step))))
      (is (= {:pipeline [] :queue [step step2]}
             (-> game
                 (sut/queue-step step)
                 (sut/queue-step step2)
                 (:gp))))
      (is (= {:pipeline [1 2 3] :queue [:a :b :c step]}
             (-> game
                 (assoc-in [:gp :pipeline] [1 2 3])
                 (assoc-in [:gp :queue] [:a :b :c])
                 (sut/queue-step step)
                 (:gp)))))
    (testing "type stays the same"
      (is (vector?
            (-> game
                (assoc-in [:gp :pipeline] [1 2 3])
                (assoc-in [:gp :queue] [:a :b :c])
                (sut/queue-step step)
                (get-in [:gp :queue])))))
    (testing "Step must be valid"
      (is (thrown? clojure.lang.ExceptionInfo (sut/queue-step game {}))))))

(deftest get-current-step-test
  (let [step (step/make-base-step)
        game (game/new-game nil)]
    (is (= nil (sut/get-current-step game)))
    (is (= step (->> step
                     (assoc-in game [:gp :pipeline 0])
                     (sut/get-current-step))))))

(deftest drop-current-step-test
  (is (= (game/new-game nil) (sut/drop-current-step (game/new-game nil))))
  (is (= (game/new-game nil)
         (-> (game/new-game nil)
             (assoc-in [:gp :pipeline 0] (step/make-base-step))
             (sut/drop-current-step))))
  (testing "type stays the same"
    (is (vector?
          (-> (game/new-game nil)
              (assoc-in [:gp :pipeline 0] (step/make-base-step))
              (sut/drop-current-step)
              (get-in [:gp :pipeline]))))))

(deftest update-pipeline-step
  (let [game (game/new-game nil)]
    (is (= game (sut/update-pipeline game))))
  (let [step (step/make-base-step)
        game (-> (game/new-game nil)
                 (sut/queue-step step))]
    (is (= {:pipeline [step] :queue []}
           (:gp (sut/update-pipeline game)))))
  (let [step (step/make-base-step)
        game (-> (game/new-game nil)
                 (assoc-in [:gp :pipeline] [:a :b :c])
                 (sut/queue-step step))]
    (is (= {:pipeline [step :a :b :c] :queue []}
           (:gp (sut/update-pipeline game)))))
  (let [step (step/make-base-step)
        step2 (step/make-base-step)
        game (-> (game/new-game nil)
                 (assoc-in [:gp :pipeline] [:a :b :c])
                 (sut/queue-step step)
                 (sut/update-pipeline)
                 (sut/queue-step step2))]
    (is (= {:pipeline [step2 step :a :b :c] :queue []}
           (:gp (sut/update-pipeline game))))))

(deftest continue-gp-test
  (testing "does nothing by default"
    (let [game (game/new-game nil)]
      (is (= game (sut/continue-game game)))))
  (testing "updates the pipeline"
    (let [step (step/make-base-step
                 {:continue-step
                  (fn [_step game] game)})
          game (-> (game/new-game nil)
                   (sut/queue-step step))]
      (is (= {:pipeline [step] :queue []}
             (:gp (sut/update-pipeline game))))))
  (testing "calls 'continue-step' on current step"
    (let [step (step/make-base-step
                 {:continue-step
                  (fn [_step game] (assoc-in game [:--test-flag] :--test-value))})
          game (-> (game/new-game nil)
                   (sut/queue-step step))]
      (is (= :--test-value (get-in (sut/continue-game game) [:--test-flag])))))
  (testing "drops the current step after 'continue-step'"
    (let [step (step/make-base-step
                 {:continue-step
                  (fn [_step game] game)})
          game (-> (game/new-game nil)
                   (sut/queue-step step)
                   (sut/continue-game))]
      (is (= {:pipeline [] :queue []} (:gp game))))))

(deftest handle-prompt-clicked-test
  (testing "does nothing by default"
    (let [game (game/new-game nil)]
      (is (= game (sut/handle-prompt-clicked game :corp "button")))))
  (testing "doesn't update the pipeline"
    (let [step (prompt/base-prompt
                 {:active-prompt (constantly {:text "text"})
                  :active-condition :corp
                  :on-prompt-clicked
                  (fn [_step game _player _button] game)})
          game (-> (game/new-game nil)
                   (sut/queue-step step))]
      (is (= {:pipeline [] :queue [step]}
             (->> (sut/handle-prompt-clicked game :corp "button")
                  (:gp))))))
  (testing "does nothing if pipeline is empty"
    (let [step (prompt/base-prompt
                 {:active-prompt (constantly {:text "text"})
                  :active-condition :corp
                  :on-prompt-clicked
                  (fn [_step game _player _button] game)})
          game (-> (game/new-game nil)
                   (sut/queue-step step))]
      (is (= game (sut/handle-prompt-clicked game :corp "button")))))
  (testing "calls 'on-prompt-clicked' on current step"
    (let [step (prompt/base-prompt
                 {:active-prompt (constantly {:text "text"})
                  :active-condition :corp
                  :on-prompt-clicked
                  (fn [_step game _player _button] (assoc-in game [:--test-flag] :--test-check ))})
          game (-> (game/new-game nil)
                   (assoc-in [:gp :pipeline] [step]))]
      (is (= :--test-check (get-in (sut/handle-prompt-clicked game :corp "button") [:--test-flag]))))))
