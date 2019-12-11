(ns cashier.port.cash
  (:require [integrant.core :as ig]))

(defprotocol Cash
  (cash-available [this]))

(defmethod ig/init-key ::cash [_ {:keys [cash-available]}]
  (reify Cash
    (cash-available [this] (cash-available))))
