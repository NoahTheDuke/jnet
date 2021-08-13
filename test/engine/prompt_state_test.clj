(ns engine.prompt-state-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [engine.prompt-state :as sut]))

(deftest set-prompt-test
  (testing "validation works"
    (is (thrown? java.lang.AssertionError
                 (-> (sut/make-prompt-state)
                     (sut/set-prompt nil))))
    (is (thrown? java.lang.AssertionError
                 (-> (sut/make-prompt-state)
                     (sut/set-prompt {:header "optional"})))))
  (is (= {:select-card true
          :header "header"
          :text "text"
          :buttons [:a :b :c]}
         (-> (sut/make-prompt-state)
             (sut/set-prompt {:select-card true
                              :header "header"
                              :text "text"
                              :buttons [:a :b :c]})
             (select-keys [:select-card :header :text :buttons])))))

(deftest clear-prompt-test
  (is (= {:select-card false
          :header ""
          :text ""
          :buttons []}
         (-> (sut/make-prompt-state)
             (sut/clear-prompt)
             (select-keys [:select-card :header :text :buttons])))))
