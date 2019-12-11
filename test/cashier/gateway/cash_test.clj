(ns cashier.gateway.cash-test
  (:require [cashier.gateway.cash :as sut]
            [clojure.test :as t]
            [integrant.core :as ig]))

(t/deftest test-cash-available
  (t/testing "Returns available cash count"
    (let [find-cash-count-mock
          (fn [] '({:value 1, :num 999}
                   {:value 5, :num 888}
                   {:value 10, :num 777}
                   {:value 50, :num 666}
                   {:value 100, :num 555}
                   {:value 500, :num 444}
                   {:value 1000, :num 333}
                   {:value 5000, :num 222}
                   {:value 10000, :num 111}))
          f (ig/init-key :cashier.gateway.cash/cash-available {:find-cash-count find-cash-count-mock})
          cash-def {:cash-10000 10000
                    :cash-5000   5000
                    :cash-1000   1000
                    :cash-500     500
                    :cash-100     100
                    :cash-50       50
                    :cash-10       10
                    :cash-5         5
                    :cash-1         1}
          result (f cash-def)]
      (t/is (= {:cash-1 999
                :cash-5 888
                :cash-10 777
                :cash-50 666
                :cash-100 555
                :cash-500 444
                :cash-1000 333
                :cash-5000 222
                :cash-10000 111}
               result))))

  (t/testing "Returns available cash count even keys not matched"
    (let [find-cash-count-mock
          (fn [] '({:value 50, :num 666}
                   {:value 100, :num 555}))
          f (ig/init-key :cashier.gateway.cash/cash-available {:find-cash-count find-cash-count-mock})
          cash-def {:cash-10000 10000
                    :cash-5000   5000
                    :cash-1000   1000
                    :cash-500     500
                    :cash-100     100
                    :cash-50       50
                    :cash-10       10
                    :cash-5         5
                    :cash-1         1}
          result (f cash-def)]
      (t/is (= {:cash-50 666
                :cash-100 555}
               result))))

  (t/testing "Does not return when value does not exist on cash definition"
    (let [find-cash-count-mock (fn [] '({:value 999 :count 888}))
          f (ig/init-key :cashier.gateway.cash/cash-available {:find-cash-count find-cash-count-mock})
          cash-def {:cash-10000 10000
                    :cash-5000   5000
                    :cash-1000   1000
                    :cash-500     500
                    :cash-100     100
                    :cash-50       50
                    :cash-10       10
                    :cash-5         5
                    :cash-1         1}
          result (f cash-def)]
      (t/is (= {} result)))))
