(ns cashier.core.usecase.cash-test
  (:require [cashier.core.usecase.cash :as target]
            [cashier.core.entity.cash :as ent]
            [cashier.port.cash :as port]
            [clojure.test :refer :all]
            [integrant.core :as ig]
            [shrubbery.core :as shrubbery]))

(deftest calc-sum-test
  (testing "Calculates given cash amount"
    (let [f (ig/init-key :cashier.core.usecase.cash/calc-sum {})]
      (with-redefs [ent/sum (fn [cash] (when (= cash {:cash-100 5}) 1234))]
        (is (= 1234 (f {:cash-100 5})))))))

(deftest calc-change-test
  (testing "Calculates change for given charge and cash amount"
    (let [port-mock (shrubbery/mock port/Cash {:cash-available {:cash-100 10000}})
          f (ig/init-key :cashier.core.usecase.cash/calc-change {:port port-mock})]
      (with-redefs
        [ent/sum (fn [cash] (when (= {:cash-100 1} cash) 580))
         ent/valid-sum? (fn [sum] (if (= 500 sum) true (assert false)))
         ent/get-combination (fn [sum cash-available] (when (and (= 500 sum) (= {:cash-100 10000} cash-available)) {:cash-100 1234}))]
        (is (= {:cash-100 1234} (f 80 {:cash-100 1}))))))

  (testing "Throws an exception when cash is insufficient"
    (let [port-mock (shrubbery/mock port/Cash {:cash-available {}})
          f (ig/init-key :cashier.core.usecase.cash/calc-change {:port port-mock})]
      (with-redefs
        [ent/sum (fn [cash] (when (= {:cash-10 1} cash) 10))
         ent/valid-sum? (fn [sum] (if (= -70 sum) false (assert false)))]
        (try
          (f 80 {:cash-10 1})
          (assert false "No exception is thrown")
          (catch Exception e (is (= "Cash is insufficient" (ex-message e))))))))

  (testing "Throws an exception when enough change is not available"
    (let [port-mock (shrubbery/mock port/Cash {:cash-available {:cash-100 10000}})
          f (ig/init-key :cashier.core.usecase.cash/calc-change {:port port-mock})]
      (with-redefs
        [ent/sum (fn [cash] (when (= {:cash-100 1} cash) 580))
         ent/valid-sum? (fn [sum] (if (= 500 sum) true (assert false)))
         ent/get-combination (fn [sum cash-available] (when (and (= 500 sum) (= {:cash-100 10000} cash-available)) nil))]
        (try
          (f 80 {:cash-100 1})
          (assert false "No exception is thrown")
          (catch Exception e (is (= "Not enough change" (ex-message e))))))))

  (testing "Throws an exception with system-error true when cash-available throws an exception"
    (let [port-mock (shrubbery/mock port/Cash {:cash-available (shrubbery/throws RuntimeException "DB ERROR")})
          f (ig/init-key :cashier.core.usecase.cash/calc-change {:port port-mock})]
      (with-redefs
        [ent/sum (fn [cash] (when (= {:cash-100 1} cash) 580))
         ent/valid-sum? (fn [sum] (if (= 500 sum) true (assert false)))]
        (try
          (f 80 {:cash-100 1})
          (assert false "No exception is thrown")
          (catch Exception e (is (= true (:system-error (ex-data e))))))))))
