(ns cashier.handler.exchange-test
  (:require  [clojure.test :refer :all]
             [integrant.core :as ig]
             [ring.mock.request :as mock]
             [cashier.handler.exchange :as handler]))

(deftest post-exchange-test
  (testing "POST /exchange returns 200"
    (let [handler (ig/init-key :cashier.handler.exchange/post-exchange {})
          response (handler (mock/request :post "/exchange"))]
      (is (= 200 (:status response))))))
