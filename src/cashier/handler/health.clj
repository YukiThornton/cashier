(ns cashier.handler.health
  (:require [ataraxy.core :as ataraxy]
            [ring.util.response :as res]
            [integrant.core :as ig]))

(defmethod ig/init-key ::get-health [_ options]
  (fn [{[_] :ataraxy/result}]
    (res/response {:status "ok"})))
