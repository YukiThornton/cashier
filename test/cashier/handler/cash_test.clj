(ns cashier.handler.cash-test
  (:require [integrant.core :as ig]
            [cashier.handler.cash :as target]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cashier.core.entity.cash :as ent]))

(deftest to-integer-test
  (testing "Returns an integer when param can be changed into integer"
    (is (= 2 (target/to-integer "2"))))

  (testing "Returns nil when param cannot be changed into integer"
    (is (= nil (target/to-integer "2.1")))
    (is (= nil (target/to-integer "hello")))
    (is (= nil (target/to-integer nil)))))

(deftest valid-cash-map?-test
  (testing "Returns true when all given values are not nil"
    (is (= true (target/valid-cash-map? {:cash-100 10 :cash-50 100}))))

  (testing "Returns false when any of given values is nil"
    (is (= false (target/valid-cash-map? {:cash-100 nil :cash-50 100}))))

  (testing "Returns true when all given values are 0 or positive"
    (is (= true (target/valid-cash-map? {:cash-100 1})))
    (is (= true (target/valid-cash-map? {:cash-100 0}))))

  (testing "Returns false when any of given values is negative"
    (is (= false (target/valid-cash-map? {:cash-100 -1}))))

  (testing "Returns true when all given keys exist in cash-keys"
    (let [ckeys (keys ent/cash-value)
          m (zipmap ckeys (repeat  1))]
      (is (= true (target/valid-cash-map? m)))))

  (testing "Returns false when any given keys do not exist in cash-keys"
    (is (= false (target/valid-cash-map? {:invalid-key 1})))))

(deftest get-sum-test
  (testing "Returns 200 with sum on valid request"
    (let [usecase-mock (fn [cash] (when (= {:cash-100 3} cash) 999))
          result (target/get-sum usecase-mock {:cash-100 "3"})]
      (is (= 200 (:status result)))
      (is (= 999 (:sum result)))))

  (testing "Returns 400 with message on invalid request"
    (let [result (target/get-sum nil {:invalid-cash "1"})]
      (is (= 400 (:status result)))
      (is (= nil (:sum result)))
      (is (some? (:message result))))))

(deftest get-change-test
  (testing "Returns 200 with change on valid request"
    (let [usecase-mock
          (fn [charge cash]
            (when (and (= 500 charge) (= {:cash-1000 1} cash))
              {:cash-500 1}))
          result (target/get-change usecase-mock {:charge "500" :cash-1000 "1"})]

      (is (= 200 (:status result)))
      (is (= {:cash-500 1} (:change result)))))

  (testing "Returns 400 with message when param charge is missing"
    (let [usecase-mock (fn [charge cash] nil)
          result (target/get-change usecase-mock {:cash-1000 "1"})]
      (is (= 400 (:status result)))
      (is (= nil (:change result)))
      (is (some? (:message result)))))

  (testing "Returns 400 with message when cash params are invalid"
    (let [usecase-mock (fn [charge cash] nil)
          result (target/get-change usecase-mock {:charge "5000" :invalid-cash "1"})]
      (is (= 400 (:status result)))
      (is (= nil (:change result)))
      (is (some? (:message result)))))

  (testing "Returns 400 with message when cash is insufficient"
    (let [usecase-mock
          (fn [charge cash]
            (when (and (= 5000 charge) (= {:cash-1000 1} cash)) (throw (ex-info "MESSAGE" {}))))
          result (target/get-change usecase-mock {:charge "5000" :cash-1000 "1"})]
      (is (= 400 (:status result)))
      (is (= nil (:change result)))
      (is (some? (:message result)))))

  (testing "Returns 400 with message when not enough change is available"
    (let [usecase-mock
          (fn [charge cash]
            (when (and (= 5000 charge) (= {:cash-1000 6} cash)) (throw (ex-info "MESSAGE" {}))))
          result (target/get-change usecase-mock {:charge "5000" :cash-1000 "6"})]
      (is (= 400 (:status result)))
      (is (= nil (:change result)))
      (is (some? (:message result)))))

  (testing "Returns 500 with message when an exception with system-error true is thrown"
    (let [usecase-mock
          (fn [charge cash]
            (when (and (= 500 charge) (= {:cash-1000 1} cash)) (throw (ex-info "MESSAGE" {:system-error true}))))
          result (target/get-change usecase-mock {:charge "500" :cash-1000 "1"})]
      (is (= 500 (:status result)))
      (is (= nil (:change result)))
      (is (some? (:message result))))))
