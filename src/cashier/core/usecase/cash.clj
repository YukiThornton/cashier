(ns cashier.core.usecase.cash
  (:require [integrant.core :as ig]
            [cashier.core.entity.cash :as ent]
            [cashier.port.cash]))

(defmethod ig/init-key ::calc-sum [_ options]
  (fn [cash]
    (ent/sum cash)))

(defn- get-cash-available [port]
  (try
    (cashier.port.cash/cash-available port ent/cash-value)
    (catch Exception e (throw (ex-info "Change is not accessible" {:system-error true})))))

(defn- get-valid-change-sum [charge cash]
  (let [result (- (ent/sum cash) charge)]
    (if (ent/valid-sum? result)
      result
      (throw (ex-info "Cash is insufficient" {:charge charge :cash cash})))))

(defmethod ig/init-key ::calc-change [_ {:keys [port]}]
  (fn [charge cash]
    (let [change-sum (get-valid-change-sum charge cash)
          change (ent/get-combination change-sum (get-cash-available port))]
      (if (nil? change)
        (throw (ex-info "Not enough change" {:charge charge :cash cash}))
        change))))

