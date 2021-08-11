(ns engine.steps.prompt-step-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.prompt-step :as sut]
   [engine.test-helper]))

(deftest make-prompt-step-test
  (let [active-condition (constantly true)
        active-prompt (constantly nil)
        waiting-prompt (constantly nil)]
    (testing "Assigns props correctly"
      (is (= {:active-condition active-condition
              :active-prompt active-prompt
              :waiting-prompt waiting-prompt
              :type :step/prompt}
             (-> {:active-condition active-condition
                  :active-prompt active-prompt
                  :waiting-prompt waiting-prompt
                  :type :step/prompt}
                 (sut/prompt-step)
                 (select-keys [:active-condition :active-prompt
                               :waiting-prompt :type])))))
    (testing "waiting-prompt has a default"
      (is (= ((:waiting-prompt (sut/prompt-step {:active-condition :corp
                                                 :active-prompt (constantly {:title "yes"})}))
              nil nil nil)
             {:text "Waiting for opponent"})))))

(deftest step-functions-test
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          waiting-prompt {:text "Waiting text"}
          step (sut/prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)
                  :waiting-prompt (constantly waiting-prompt)})
          game (game/new-game {})]
      (testing "active-condition checks the player arg"
        (is ((:active-condition step) step game :corp))
        (is (not ((:active-condition step) step game :runner))))
      (testing "active-prompt returns the prompt unconditionally"
        (is (= active-prompt ((:active-prompt step) step game :corp)))
        (is (= active-prompt ((:active-prompt step) step game :runner))))
      (testing "waiting-prompt returns the prompt unconditionally"
        (is (= waiting-prompt ((:waiting-prompt step) step game :corp)))
        (is (= waiting-prompt ((:waiting-prompt step) step game :runner))))))

(deftest set-prompt-test
  (testing "updates both players"
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          waiting-prompt {:text "Waiting text"}
          step (sut/prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)
                  :waiting-prompt (constantly waiting-prompt)})
          game (game/new-game nil)
          {:keys [corp runner] :as new-game} (sut/set-prompt step game)]
      (is (not= game new-game))
      (is (= (:header active-prompt) (get-in corp [:prompt-state :header])))
      (is (= (:text active-prompt) (get-in corp [:prompt-state :text])))
      (is (= (:text waiting-prompt) (get-in runner [:prompt-state :text]))))))

(deftest pipeline-interaction-test
  (testing "continue calls into set-prompt"
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          step (sut/prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)})
          {:keys [corp runner]} (-> (game/new-game {})
                                    (pipeline/queue-step step)
                                    (pipeline/continue-game)
                                    (second))]
      (is (= (:header active-prompt) (get-in corp [:prompt-state :header])))
      (is (= (:text active-prompt) (get-in corp [:prompt-state :text])))
      (is (= "Waiting for opponent" (get-in runner [:prompt-state :text])))))
  (testing "player prompts are restored after step is complete"
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          step (sut/prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)})
          {:keys [corp runner]} (-> (game/new-game {})
                                    (pipeline/queue-step step)
                                    (assoc-in [:gp :queue 0 :complete?] true)
                                    (pipeline/continue-game)
                                    (second))]
      (is (= "" (get-in corp [:prompt-state :header])))
      (is (= "" (get-in corp [:prompt-state :text])))
      (is (= "" (get-in runner [:prompt-state :text]))))))
