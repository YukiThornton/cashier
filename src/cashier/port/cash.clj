(ns cashier.port.cash
  (:require [integrant.core :as ig]))

(defprotocol Cash
  (cash-available [this m]))

(defmethod ig/init-key ::cash [_ {:keys [cash-available]}]
  (reify Cash
    (cash-available [this m] (cash-available m))))
