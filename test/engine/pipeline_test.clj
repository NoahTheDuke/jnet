(ns engine.pipeline-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :refer [new-game]]
   [engine.pipeline :as sut]
   [engine.test-helper :refer [click-prompt]]
   [engine.steps.prompt-step :refer [prompt-step]]
   [engine.steps.base-step :refer [make-base-step]]))

(deftest queue-steps-test
  (let [game (new-game nil)
        step (make-base-step)
        step2 (make-base-step)]
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
  (let [step (make-base-step)
        game (new-game nil)]
    (is (= nil (sut/get-current-step game)))
    (is (= step (->> step
                     (assoc-in game [:gp :pipeline 0])
                     (sut/get-current-step))))))

(deftest drop-current-step-test
  (is (= (new-game nil) (sut/drop-current-step (new-game nil))))
  (is (= (new-game nil)
         (-> (new-game nil)
             (assoc-in [:gp :pipeline 0] (make-base-step))
             (sut/drop-current-step))))
  (testing "type stays the same"
    (is (vector?
          (-> (new-game nil)
              (assoc-in [:gp :pipeline 0] (make-base-step))
              (sut/drop-current-step)
              (get-in [:gp :pipeline]))))))

(deftest update-pipeline-step
  (let [game (new-game nil)]
    (is (= game (sut/update-pipeline game))))
  (let [step (make-base-step)
        game (-> (new-game nil)
                 (sut/queue-step step))]
    (is (= {:pipeline [step] :queue []}
           (:gp (sut/update-pipeline game)))))
  (let [step (make-base-step)
        game (-> (new-game nil)
                 (assoc-in [:gp :pipeline] [:a :b :c])
                 (sut/queue-step step))]
    (is (= {:pipeline [step :a :b :c] :queue []}
           (:gp (sut/update-pipeline game)))))
  (let [step (make-base-step)
        step2 (make-base-step)
        game (-> (new-game nil)
                 (assoc-in [:gp :pipeline] [:a :b :c])
                 (sut/queue-step step)
                 (sut/update-pipeline)
                 (sut/queue-step step2))]
    (is (= {:pipeline [step2 step :a :b :c] :queue []}
           (:gp (sut/update-pipeline game))))))

(deftest continue-gp-test
  (testing "returns true by default"
    (let [game (new-game nil)]
      (is (= [true game] (sut/continue-game game)))))
  (testing "updates the pipeline"
    (let [step (make-base-step
                 {:continue-step
                  (fn [_step game] [false game])})
          game (-> (new-game nil)
                   (sut/queue-step step))]
      (is (= {:pipeline [step] :queue []}
             (:gp (second (sut/continue-game game)))))))
  (testing "calls 'continue-step' on current step"
    (let [step (make-base-step
                 {:continue-step
                  (fn [_step _game] [false :foo])})
          game (-> (new-game nil)
                   (sut/queue-step step))]
      (is (= [false :foo] (sut/continue-game game)))))
  (testing "drops the current step if 'continue-step' returns true"
    (let [step (make-base-step
                 {:continue-step
                  (fn [_step game] [true game])})
          game (-> (new-game nil)
                   (sut/queue-step step)
                   (sut/continue-game))]
      (is (= {:pipeline [] :queue []} (:gp (second game)))))))

(deftest handle-prompt-clicked-test
  (testing "returns false by default"
    (let [game (new-game nil)]
      (is (= [false game] (sut/handle-prompt-clicked game :corp "button")))))
  (testing "doesn't update the pipeline"
    (let [step (prompt-step
                 {:active-prompt (constantly {:text "text"})
                  :active-condition :corp})
          game (-> (new-game nil)
                   (sut/queue-step step))]
      (is (= {:pipeline [] :queue [step]}
             (->> (sut/handle-prompt-clicked game :corp "button")
                  (second)
                  (:gp))))))
  (testing "returns false if pipeline is empty"
    (let [step (prompt-step
                 {:active-prompt (constantly {:text "text"})
                  :active-condition :corp})
          game (-> (new-game nil)
                   (sut/queue-step step))]
      (is (false? (first (sut/handle-prompt-clicked game :corp "button"))))))
  (testing "calls 'on-prompt-clicked' on current step"
    (let [step (prompt-step
                 {:active-prompt (constantly {:text "text"})
                  :active-condition :corp
                  :on-prompt-clicked
                  (fn [_step _game _player _button] [:foo :bar])})
          game (-> (new-game nil)
                   (assoc-in [:gp :pipeline] [step]))]
      (is (= [:foo :bar] (sut/handle-prompt-clicked game :corp "button"))))))
