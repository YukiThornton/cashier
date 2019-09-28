(ns cashier.handler.exchange
  (:require [ataraxy.core :as ataraxy]
            [ring.util.response :as res]
            [integrant.core :as ig]))

(defmethod ig/init-key ::post-exchange [_ options]
  (fn [{[_ money] :ataraxy/result}]
    (res/response {:money money})))
