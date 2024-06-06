(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'my/lambda)
(def version (format "0.1.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (delay (b/create-basis {:project "deps.edn"})))
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis @basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis @basis}))
