(ns engine.pipeline-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :refer [new-game]]
   [engine.pipeline :as sut]
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
      (is (thrown? clojure.lang.ExceptionInfo (:gp (sut/queue-step game {})))))))

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
