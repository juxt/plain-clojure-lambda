(ns handler
  (:require [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.util.response :as response]))

(def index
  "<!DOCTYPE html>
   <html>
     <head>
       <title>My Lambda App</title>
     </head>
     <body>
       <div id=\"root\"></div>
       <script src=\"/public/js/compiled/app.js\" type=\"text/javascript\"></script>
       <script type=\"text/javascript\">app.init()</script>
     </body>
   </html>")

;; Probably a silly example considering lambda is suppose to be stateless
(def counter (atom 0))
(defn get-counter [_]
  (response/response {:counter @counter}))
(defn inc-counter [_]
  (response/response {:counter (swap! counter inc)}))

(def app
  (ring/ring-handler
    (ring/router
      [["/" (constantly
              (-> (response/response index)
                  (response/content-type "text/html")))]
       ["/api/counter" {:get #'get-counter
                        :post #'inc-counter}]
       ["/public/*" (ring/create-resource-handler)]]
      {:data {:muuntaja m/instance
              :middleware [muuntaja/format-middleware
                           rrc/coerce-request-middleware
                           rrc/coerce-response-middleware]}})
    (ring/routes (ring/create-default-handler))))
