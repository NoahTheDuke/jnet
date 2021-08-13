(ns engine.steps.step-protocol-test
  (:require
    [clojure.test :refer [deftest is]]
    [engine.steps.step :as sut]))

(deftest step-protocol-continue-test
  (is (thrown? clojure.lang.ExceptionInfo (sut/continue-step nil nil)))
  (is (thrown? clojure.lang.ExceptionInfo (sut/continue-step {} nil)))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Step <.*> is not a valid step" (sut/continue-step nil nil)))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Step <.*> is not a valid step" (sut/continue-step {} nil))))

(deftest step-protocol-complete?-test
  (is (thrown? clojure.lang.ExceptionInfo (sut/blocking nil {})))
  (is (thrown? clojure.lang.ExceptionInfo (sut/blocking {} {})))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Step <.*> is not a valid step" (sut/blocking nil {})))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Step <.*> is not a valid step" (sut/blocking {} {}))))

(deftest step-protocol-validate-test
  (is (thrown? clojure.lang.ExceptionInfo (sut/validate nil)))
  (is (thrown? clojure.lang.ExceptionInfo (sut/validate {})))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Step <.*> is not a valid step" (sut/validate nil)))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Step <.*> is not a valid step" (sut/validate {}))))
