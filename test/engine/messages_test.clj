(ns engine.messages-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.messages :as sut]
   [engine.test-utils :refer :all]))

(deftest chat-log-test
  (is (= [{:text ["Hello!"]}]
         (-> (new-game)
             (sut/add-message "Hello!")
             (get-messages))))
  (is (= [{:text ["Hello!"]}
          {:text ["Goodbye!"]}]
         (-> (new-game)
             (sut/add-message "Hello!")
             (sut/add-message "Goodbye!")
             (get-messages))))
  (is (thrown? java.lang.IndexOutOfBoundsException
               (-> (new-game)
                   (sut/add-message "Hello {0}!"))))
  (is (= [{:text ["Hello" "Noah" "!"]}]
         (-> (new-game)
             (sut/add-message "Hello {0}!" ["Noah"])
             (get-messages))))
  (is (= [{:text ["Hello" {:name "Noah"} "!"]}]
         (-> (new-game)
             (sut/add-message "Hello {0}!" [{:name "Noah"
                                             :no-match "Bogart"}])
             (get-messages))))
  (is (= [{:text ["going" "," "going" "and" "gone" "!"]}]
         (-> (new-game)
             (sut/add-message "{0}, {0} and {1}!" ["going" "gone"])
             (get-messages)))))
