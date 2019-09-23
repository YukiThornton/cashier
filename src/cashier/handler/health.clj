(ns cashier.handler.health
  (:require [ataraxy.core :as ataraxy]
            [ataraxy.response :as response] 
            [integrant.core :as ig]))

(defmethod ig/init-key ::get-health [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok {:status "ok"}]))
