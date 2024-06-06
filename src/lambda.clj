(ns lambda
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn -handleRequest [_ is os _context]
  (let [input (slurp is)]
    (if (re-find #"error" input)
      (throw (ex-info "test" {:my "error"}))
      (spit os "test"))))
