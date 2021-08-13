(ns engine.messages-test
  (:require
   [clojure.test :refer [deftest is]]
   [engine.game :as game]
   [engine.messages :as sut]
   [engine.helper-test :refer [get-messages]]))

(deftest chat-log-test
  (is (= [{:text ["Hello!"]}]
         (-> (game/start-new-game nil)
             (sut/add-message "Hello!")
             (get-messages))))
  (is (= [{:text ["Hello!"]}
          {:text ["Goodbye!"]}]
         (-> (game/start-new-game nil)
             (sut/add-message "Hello!")
             (sut/add-message "Goodbye!")
             (get-messages))))
  (is (thrown? java.lang.IndexOutOfBoundsException
               (-> (game/start-new-game nil)
                   (sut/add-message "Hello {0}!"))))
  (is (= [{:text ["Hello" "Noah" "!"]}]
         (-> (game/start-new-game nil)
             (sut/add-message "Hello {0}!" ["Noah"])
             (get-messages))))
  (is (= [{:text ["Hello" {:name "Noah"} "!"]}]
         (-> (game/start-new-game nil)
             (sut/add-message "Hello {0}!" [{:name "Noah"
                                             :no-match "Bogart"}])
             (get-messages))))
  (is (= [{:text ["going" "," "going" "and" "gone" "!"]}]
         (-> (game/start-new-game nil)
             (sut/add-message "{0}, {0} and {1}!" ["going" "gone"])
             (get-messages)))))
