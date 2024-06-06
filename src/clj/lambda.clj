(ns lambda
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:require [muuntaja.core :as m]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [handler])
  (:import (java.util Base64)))


;; >> Utils

(defn b64-encode [to-encode]
  (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(defn b64-decode [to-decode]
  (String. (.decode (Base64/getDecoder) to-decode)))



;; >> Request/Response coersion

; Function URL request format: https://docs.aws.amazon.com/lambda/latest/dg/urls-invocation.html#urls-request-payload
; Ring request spec: https://github.com/ring-clojure/ring/blob/master/SPEC.md#14-request-maps
(defn ->ring-request
  "Given a Lambda function URL request, returns a Ring request map."
  [request]
  (let [http (get-in request ["requestContext" "http"])
        headers (get request "headers")]
    (merge
      (when-let [body (get request "body")]
        {:body (if (get request "isBase64Encoded")
                 (b64-decode body)
                 body)})
      {:headers headers ; NOTE: already lower-case
       :protocol (get http "protocol")
       :query-string (get request "rawQueryString")
       :remote-addr (get http "sourceIp")
       :request-method (-> (get http "method") (str/lower-case) keyword)
       :scheme (keyword (get headers "x-forwarded-proto"))
       :server-name (get-in request ["requestContext" "domainName"])
       :server-port (get headers "x-forwarded-port")
       :uri (get http "path")})))

; Source: https://sideshowcoder.com/2018/05/11/clojure-ring-api-gateway-lambda/
(defmulti wrap-body class)
(defmethod wrap-body String [body] body)
(defmethod wrap-body clojure.lang.ISeq [body] (str/join body))
(defmethod wrap-body java.io.File [body] (slurp body))
(defmethod wrap-body java.io.InputStream [body] (slurp body))

; Function URL response format https://docs.aws.amazon.com/lambda/latest/dg/urls-invocation.html#urls-response-payload
; Ring response spec: https://github.com/ring-clojure/ring/blob/master/SPEC.md#15-response-maps
(defn ring-response->
  "Given a Ring response map, returns a Lambda function URL response."
  [response]
  {:statusCode (:status response)
   :headers (:headers response)
   :isBase64Encoded true
   :body (-> response :body wrap-body b64-encode)})



;; >> Lambda handler

(def m
  (m/create
    (assoc-in
      m/default-options
      [:formats "application/json" :decoder-opts]
      ;; Because ring wants the headers as lower-cased strings
      {:decode-key-fn identity})))

(defn -handleRequest [_ is os _context]
  (let [res (->> is
                 (m/decode m "application/json")
                 ->ring-request
                 handler/app
                 ring-response->
                 (m/encode m "application/json"))]
    (io/copy res os)))
