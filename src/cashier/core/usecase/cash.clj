(ns cashier.core.usecase.cash
  (:require [integrant.core :as ig]
            [cashier.core.entity.cash :as ent]))

(defmethod ig/init-key ::calc-sum [_ options]
  (fn [cash]
    (ent/sum cash)))

(defmethod ig/init-key ::calc-change [_ options]
  (fn [charge cash]
    (let [cash-sum (ent/sum cash)
          change-sum (- cash-sum charge)]
      (when (not (neg-int? change-sum))
        (ent/get-combination change-sum)))))
