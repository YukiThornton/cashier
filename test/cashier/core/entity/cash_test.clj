(ns cashier.core.entity.cash-test
  (:require  [clojure.test :refer :all]
             [cashier.core.entity.cash :as target]))

(deftest sum-test
  (testing "sum returns sum value of cash"
    (is (= 30000 (target/sum {:cash-10000 3})))
    (is (= 15000 (target/sum {:cash-5000  3})))
    (is (= 3000  (target/sum {:cash-1000  3})))
    (is (= 1500  (target/sum {:cash-500   3})))
    (is (= 300   (target/sum {:cash-100   3})))
    (is (= 150   (target/sum {:cash-50    3})))
    (is (= 30    (target/sum {:cash-10    3})))
    (is (= 15    (target/sum {:cash-5     3})))
    (is (= 3     (target/sum {:cash-1     3})))
    (is (= 16666 (target/sum {:cash-10000 1
                          :cash-5000  1
                          :cash-1000  1
                          :cash-500   1
                          :cash-100   1
                          :cash-50    1
                          :cash-10    1
                          :cash-5     1
                          :cash-1     1}))))

  (testing "sum returns 0 when empty map is given"
    (is (= 0 (target/sum {}))))

  (testing "sum returns 0 when nil is given"
    (is (= 0 (target/sum nil)))))

(deftest next-smaller-cash-key-test
  (testing "next-samller-cash-key returns next smaller cash key"
    (is (= :cash-5000 (target/next-smaller-cash-key :cash-10000)))
    (is (= :cash-1000 (target/next-smaller-cash-key :cash-5000)))
    (is (= :cash-500  (target/next-smaller-cash-key :cash-1000)))
    (is (= :cash-100  (target/next-smaller-cash-key :cash-500)))
    (is (= :cash-50   (target/next-smaller-cash-key :cash-100)))
    (is (= :cash-10   (target/next-smaller-cash-key :cash-50)))
    (is (= :cash-5    (target/next-smaller-cash-key :cash-10)))
    (is (= :cash-1    (target/next-smaller-cash-key :cash-5))))

  (testing "next-smaller-cash-key returns nil when smallest key is given"
    (is (nil? (target/next-smaller-cash-key :cash-1))))

  (testing "next-small-cash-key returns nil when unknown key is given"
    (is (nil? (target/next-smaller-cash-key :cash-unknown)))))

(deftest smaller-cash-keys-test
  (testing "Returns a vector"
    (is (vector? (target/smaller-cash-keys :cash-10000)))
    (is (vector? (target/smaller-cash-keys :cash-500)))
    (is (vector? (target/smaller-cash-keys :cash-5)))
    (is (vector? (target/smaller-cash-keys :cash-1))))

  (testing "Returns an unordered vector of keys smaller than the given keys"
    (is (= (set [:cash-5000 :cash-1000 :cash-500 :cash-100
                 :cash-50 :cash-10 :cash-5 :cash-1])
           (set (target/smaller-cash-keys :cash-10000))))
    (is (= (set [:cash-100 :cash-50 :cash-10 :cash-5 :cash-1])
           (set (target/smaller-cash-keys :cash-500))))
    (is (= (set [:cash-1])
           (set (target/smaller-cash-keys :cash-5)))))

  (testing "Returns an empty vector when smallest key is given"
    (is (empty? (target/smaller-cash-keys :cash-1))))

  (testing "Returns nill when unknown key is given"
    (is (nil? (target/smaller-cash-keys :cash-unknown)))))

(deftest get-combination-test
  (testing "Returns a combination of cash for given sum"
    (is (= {:cash-100 2}
           (target/get-combination 200)))
    (is (= {:cash-100 4
            :cash-50 1
            :cash-10 4}
           (target/get-combination 490)))
    (is (= {:cash-10000 5
            :cash-1 1}
           (target/get-combination 50001)))
    (is (= {}
           (target/get-combination 0))))

  (testing "Returns nil when a negative value is passed"
    (is (nil? (target/get-combination -1))))

  (testing "Returns nil when a non-integer value is passed"
    (is (nil? (target/get-combination 0.1)))
    (is (nil? (target/get-combination -0.1)))
    (is (nil? (target/get-combination "hello")))
    (is (nil? (target/get-combination nil)))))
