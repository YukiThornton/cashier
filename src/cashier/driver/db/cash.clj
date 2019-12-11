(ns cashier.driver.db.cash
  (:require [integrant.core :as ig]
            [clojure.java.jdbc :as jdbc]))

(def default-currency-code "JPY")

(defmethod ig/init-key ::find-cash-count [_ {{spec :spec} :db}]
  (fn []
    (->> default-currency-code
         (format "select cash.value, cash_count.num from cash_count inner join cash on cash.id = cash_count.cash_id where cash.currency_code='%s'")
         (jdbc/query spec))))
