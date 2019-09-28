(ns cashier.handler.health-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [ring.mock.request :as mock]
            [cashier.handler.health :as health]))

(deftest health-test
  (testing "GET /health returns 200"
    (let [handler (ig/init-key :cashier.handler.health/get-health {})
          response (handler (mock/request :get "/health"))]
      (is (= 200 (:status response))))))
