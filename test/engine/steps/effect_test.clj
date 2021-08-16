(ns engine.steps.effect-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.game :as game]
   [engine.pipeline :as pipeline]
   [engine.steps.step :as step]
   [engine.steps.effect :as sut]
   [engine.test-helper :refer [click-prompt]]))

(deftest defeffect-macro-test
  (let [test-effect (macroexpand `(sut/defeffect-full ~'test-effect ~'[game t a] ~'(-> game (blah t) (blah a))))]
    (testing "wraps in do"
      (is (= 'do (first test-effect))))
    (testing "contains a definition of the main function"
      (is (some #(= '(clojure.core/defn test-effect) (take 2 %)) (rest test-effect))))
    (testing "contains a definition of the unsafe function"
      (is (some #(= '(clojure.core/defn test-effect-unsafe) (take 2 %)) (rest test-effect)))))
  (comment "These test were my first thought but rather than actually define things with it I feel it's better to check the results of the macro directly"
    (sut/defeffect-full test-effect [game t a] game);Doing this in a test irks me a little but...
    (testing "Creates the main function"
      (is (not (thrown? Exception (test-effect {} nil nil)))))
    (testing "Creates the unsafe function"
      (is (not (thrown? Exception (test-effect-unsafe {} nil nil)))))))
