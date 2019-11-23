(ns cashier.core.entity.cash)

(def cash-value
  {:cash-10000 10000
   :cash-5000   5000
   :cash-1000   1000
   :cash-500     500
   :cash-100     100
   :cash-50       50
   :cash-10       10
   :cash-5         5
   :cash-1         1})

(defn- sorted-map-by-desc-values [target-map]
  (into (sorted-map-by #(compare [(get target-map %2) %2]
                                 [(get target-map %1) %1]))
        target-map))

(def desc-cash-map
  (sorted-map-by-desc-values cash-value))

(def desc-cash-keys
  (into [] (keys desc-cash-map)))

(defn- calc-cash-value [cash-key amount-of-cash]
  (* (cash-key cash-value) amount-of-cash))

(defn sum [cash]
  (reduce-kv #(+ %1 (calc-cash-value %2 %3)) 0 cash))

(defn next-smaller-cash-key [key]
  (let [index (.indexOf desc-cash-keys key)]
    (when-not (neg? index)
      (get desc-cash-keys (inc index)))))

(defn smaller-cash-keys [key]
  (let [index (.indexOf desc-cash-keys key)]
    (when-not (neg? index)
      (subvec desc-cash-keys (inc index)))))

(defn- valid-sum? [sum]
  (and (integer? sum) (not (neg? sum))))

(defn- assoc-if [pred m k v]
  (if pred (assoc m k v) m))

(defn- rest-into-map [m]
  (into {} (rest m)))

(defn- assoc-cash-if-dividable [sum cash-values cash-counts]
  (let [[cash-key cash-val] (first cash-values)
        quotient (quot sum cash-val)
        remainder (- sum (* quotient cash-val))
        result (assoc-if (> quotient 0) cash-counts cash-key quotient)]
    (if　(> remainder 0)
      (recur remainder (rest-into-map cash-values) result)
      result)))

(defn get-combination [sum]
  (when (valid-sum? sum)
    (assoc-cash-if-dividable sum desc-cash-map {})))

