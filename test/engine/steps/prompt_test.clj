(ns engine.steps.prompt-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.draw :as draw]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.prompt-state :as prompt-state]
   [engine.steps.prompt :as sut]
   [engine.test-utils :refer :all]))

(deftest make-prompt-test
  (let [active-condition (constantly true)
        active-prompt (constantly nil)
        waiting-text ""]
    (testing "Assigns props correctly"
      (is (= {:active-condition active-condition
              :active-prompt active-prompt
              :waiting-prompt {:text waiting-text}
              :type :step/prompt}
             (-> {:active-condition active-condition
                  :active-prompt active-prompt
                  :waiting-text waiting-text}
                 (sut/base-prompt)
                 (select-keys [:active-condition :active-prompt
                               :waiting-prompt :type])))))
    (testing "waiting-prompt has a default"
      (is (= (-> {:active-condition :corp
                  :active-prompt (constantly {:title "yes"})}
                 (sut/base-prompt)
                 (:waiting-prompt))
             {:text "Waiting for opponent"})))))

(deftest step-functions-test
  (let [active-prompt {:header "Example header"
                       :text "Example text"}
        waiting-text "Waiting text"
        waiting-prompt {:text waiting-text}
        step (sut/base-prompt
               {:active-condition :corp
                :active-prompt (constantly active-prompt)
                :waiting-text waiting-text})
        game (game/make-game {})]
    (testing "active-condition checks the player arg"
      (is ((:active-condition step) step game :corp))
      (is (not ((:active-condition step) step game :runner))))
    (testing "active-prompt returns the prompt unconditionally"
      (is (= active-prompt ((:active-prompt step) step game :corp)))
      (is (= active-prompt ((:active-prompt step) step game :runner))))
    (testing "waiting-prompt returns the prompt unconditionally"
      (is (= waiting-prompt (:waiting-prompt step))))))

(deftest set-prompt-test
  (testing "updates both players"
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          waiting-text "Waiting text"
          step (sut/base-prompt
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)
                  :waiting-text waiting-text})
          game (game/make-game nil)
          {:keys [corp runner] :as new-game} (sut/set-prompt step game)]
      (is (not= game new-game))
      (is (= (:header active-prompt) (get-in corp [:prompt-state :header])))
      (is (= (:text active-prompt) (get-in corp [:prompt-state :text])))
      (is (= waiting-text (get-in runner [:prompt-state :text]))))))

(deftest pipeline-interaction-test
  (testing "continue calls into set-prompt"
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          step (sut/base-prompt
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)})
          game (-> (game/make-game {})
                   (pipeline/queue-step step)
                   (pipeline/continue-game))]
      (is (= (:header active-prompt)
             (prompt-state/prompt-header game :corp)))
      (is (= (:text active-prompt)
             (prompt-state/prompt-text game :corp)))
      (is (= "Waiting for opponent"
             (prompt-state/prompt-text game :runner)))))
  (testing "player prompts are restored after step is complete"
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          step (sut/base-prompt
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)})
          {:keys [corp runner]} (-> (game/make-game {})
                                    (pipeline/queue-step step)
                                    (assoc-in [:gp :queue 0 :complete?] true)
                                    (pipeline/continue-game))]
      (is (= "" (get-in corp [:prompt-state :header])))
      (is (= "" (get-in corp [:prompt-state :text])))
      (is (= "" (get-in runner [:prompt-state :text]))))))
