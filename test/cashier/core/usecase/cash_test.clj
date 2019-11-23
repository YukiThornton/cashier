(ns cashier.core.usecase.cash-test
  (:require [cashier.core.usecase.cash :as target]
            [cashier.core.entity.cash :as ent]
            [clojure.test :refer :all]
            [integrant.core :as ig]))

(deftest calc-sum-test
  (testing "Calculates given cash amount"
    (let [f (ig/init-key :cashier.core.usecase.cash/calc-sum {})]
      (with-redefs [ent/sum (fn [cash] (when (= cash {:cash-100 5}) 1234))]
        (is (= 1234 (f {:cash-100 5})))))))

(deftest calc-change-test
  (testing "Calculates change for given charge and cash amount"
    (let [f (ig/init-key :cashier.core.usecase.cash/calc-change {})]
      (with-redefs
        [ent/sum (fn [cash] (when (= {:cash-100 1} cash) 580))
         ent/get-combination (fn [sum] (when (= 500 sum) {:cash-100 1234}))]
        (is (= {:cash-100 1234} (f 80 {:cash-100 1}))))))

  (testing "Returns nil when cash is insufficient"
    (let [f (ig/init-key :cashier.core.usecase.cash/calc-change {})]
      (with-redefs
        [ent/sum (fn [cash] (when (= {:cash-10 1} cash) 10))
         ent/get-combination (fn [sum] (assert false "Should not be called"))]
        (nil? (f 80 {:cash-10 1}))))))
