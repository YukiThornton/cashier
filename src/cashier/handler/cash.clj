(ns cashier.handler.cash
  (:require [ataraxy.core :as ataraxy]
            [ring.util.response :as res]
            [integrant.core :as ig]
            [cashier.core.entity.cash :as ent]))

(defn to-integer [str]
  (try
    (Integer/parseInt str)
    (catch Exception e nil)))

(def cash-keys (keys ent/cash-value))

(defn to-cash-map [params]
  (reduce-kv
   (fn [m k v]
     (let [int-val (to-integer v)]
       (if (and (.contains cash-keys k) (some? int-val))
         (assoc m k int-val)
         m)))
   {}
   params))

(defn- zero-or-pos-int? [val]
  (or (pos-int? val) (zero? val)))

(defn valid-cash-map? [map]
  (and (every? #(.contains cash-keys %) (keys map))
       (every? #(and (some? %) (zero-or-pos-int? %)) (vals map))))

(defn get-sum [calc-sum-f cash]
  (let [m (reduce-kv #(assoc %1 %2 (to-integer %3)) {} cash)]
    (if (valid-cash-map? m)
      {:status 200 :sum (calc-sum-f m)}
      {:status 400 :message "Invalid params"})))

(defmethod ig/init-key ::get-sum [_ {:keys [calc-sum]}]
  (fn [{[_ cash] :ataraxy/result}]
    (let [result (get-sum calc-sum cash)
          status (:status result)
          body (dissoc result :status)]
      (res/status (res/response body) status))))

(defn get-change [calc-change-f params]
  (let [charge (to-integer (:charge params))
        cash (reduce-kv #(assoc %1 %2 (to-integer %3)) {} (dissoc params :charge))]
    (if (and (some? charge) (valid-cash-map? cash))
      (let [change (calc-change-f charge cash)]
        (if (some? change)
          {:status 200 :change change}
          {:status 400 :message "Insufficient"}))
      {:status 400 :message "Invalid params"})))

(defmethod ig/init-key ::get-change [_ {:keys [calc-change]}]
  (fn [{[_ params] :ataraxy/result}]
    (let [result (get-change calc-change params)
          status (:status result)
          body (dissoc result :status)]
      (res/status (res/response body) status))))
