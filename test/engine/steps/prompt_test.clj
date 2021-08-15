(ns engine.steps.prompt-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.draw :as draw]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.prompt-state :as prompt-state]
   [engine.steps.step :as step]
   [engine.steps.prompt :as sut]
   [engine.test-helper :refer [click-prompt]]))

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
                  :waiting-text waiting-text
                  :on-prompt-clicked (fn [_step game _player _button] game)}
                 (sut/base-prompt-step)
                 (select-keys [:active-condition :active-prompt
                               :waiting-prompt :type])))))
    (testing "waiting-prompt has a default"
      (is (= (-> {:active-condition :corp
                  :active-prompt (constantly {:title "yes"})
                  :on-prompt-clicked (fn [_step game _player _button] game)}
                 (sut/base-prompt-step)
                 (:waiting-prompt))
             {:text "Waiting for opponent"})))))

(deftest step-functions-test
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          waiting-text "Waiting text"
          waiting-prompt {:text waiting-text}
          step (sut/base-prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)
                  :waiting-text waiting-text
                  :on-prompt-clicked (fn [_step game _player _button] game)})
          game (game/new-game {})]
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
          step (sut/base-prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)
                  :waiting-text waiting-text
                  :on-prompt-clicked (fn [_step game _player _button] game)})
          game (game/new-game nil)
          {:keys [corp runner] :as new-game} (step/blocking step game)]
      (is (not= game new-game))
      (is (= (:header active-prompt) (get-in corp [:prompt-state :header])))
      (is (= (:text active-prompt) (get-in corp [:prompt-state :text])))
      (is (= waiting-text (get-in runner [:prompt-state :text]))))))

(deftest pipeline-interaction-test
  (testing "continue calls into set-prompt"
    (let [active-prompt {:header "Example header"
                         :text "Example text"}
          step (sut/base-prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)
                  :on-prompt-clicked (fn [_step game _player _button] game)})
          game (-> (game/new-game {})
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
          step (sut/base-prompt-step
                 {:active-condition :corp
                  :active-prompt (constantly active-prompt)
                  :on-prompt-clicked (fn [_step game _player _button] game)})
          {:keys [corp runner]} (-> (game/new-game {})
                                    (pipeline/queue-step step)
                                    (assoc-in [:gp :queue 0 :response] [:runner true])
                                    (pipeline/continue-game))]
      (is (= "" (get-in corp [:prompt-state :header])))
      (is (= "" (get-in corp [:prompt-state :text])))
      (is (= "" (get-in runner [:prompt-state :text]))))))

(deftest prompt-with-handlers-test
  (let [step (sut/handler-prompt-step
               {:active-condition :corp
                :active-text "How many to draw?"
                :waiting-text "Corp to draw"
                :choices {"Draw 1" (fn [game]
                                     (draw/draw game :corp 1))
                          "Draw 2" (fn [game]
                                     (draw/draw game :corp 2))}})]
    (is (= "How many to draw?"
           (-> (game/new-game nil)
               (pipeline/queue-step step)
               (pipeline/continue-game)
               (prompt-state/prompt-text :corp))))
    (is (= "Corp to draw"
           (-> (game/new-game nil)
               (pipeline/queue-step step)
               (pipeline/continue-game)
               (prompt-state/prompt-text :runner))))
    (is (= 2
           (-> (game/new-game {:corp {:deck [:a :b :c]}})
               (pipeline/queue-step step)
               (pipeline/continue-game)
               (click-prompt :corp "Draw 2")
               (get-in [:corp :hand])
               (count))))))
