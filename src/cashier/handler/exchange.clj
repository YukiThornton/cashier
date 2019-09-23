(ns cashier.handler.exchange
  (:require [ataraxy.core :as ataraxy]
            [ataraxy.response :as response] 
            [integrant.core :as ig]))

(defmethod ig/init-key ::post-exchange [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok {:status "ok"}]))
