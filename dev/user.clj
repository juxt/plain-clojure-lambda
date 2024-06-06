(ns user
  (:require [ring.adapter.jetty :as jetty]
            [handler]))

(defonce server (atom nil))

;; You should probably use integrant or something
(defn go! [& [{:keys [join port] :or {port 8000}}]]
  (swap! server
         (fn [server]
           (when server
             (println "stopping server")
             (.stop server))
           (println "server running on port" port)
           (jetty/run-jetty handler/app
                            {:port port :join? join}))))
