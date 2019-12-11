(ns cashier.gateway.cash
  (:require [integrant.core :as ig]
            [cashier.core.entity.cash :as ent]))

(defn- assoc-if [test map key val]
  (if (true? test) (assoc map key val) map))

(defn- aggregate [m1 m2]
  (reduce-kv
   (fn [result-map m1-key m1-val]
     (assoc-if (contains? m2 m1-val) result-map m1-key (get m2 m1-val)))
   {}
   m1))

(defn- to-value&count-map [seq-of-map]
  (reduce #(assoc %1 (:value %2) (:num %2)) {} seq-of-map))

(defmethod ig/init-key ::cash-available [_ {:keys [find-cash-count]}]
  (fn []
    (let [cash-found (find-cash-count)
          value&count-map (to-value&count-map cash-found)
          type&value-map ent/cash-value
          type&count-map (aggregate type&value-map value&count-map)]
      type&count-map)))


