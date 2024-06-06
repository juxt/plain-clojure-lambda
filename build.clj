(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'my/lambda)
(def version (format "0.1.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (delay (b/create-basis {:project "deps.edn"})))
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(defn clean [_]
  (println "Deleting target directory")
  (b/delete {:path "target"})
  (println "Deleting compiled cljs")
  (b/delete {:path "resources/public/js/compiled"}))

(defn compile-cljs []
  (println "Compiling cljs")
  (let [{:keys [exit]} (b/process {:command-args ["npx" "shadow-cljs" "release" "app"]})]
    (when-not (= 0 exit)
      (throw (ex-info "Failed to compile cljs" {:exit exit})))))

(defn uber [_]
  (compile-cljs)
  (println "Compiling clj")
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis @basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (println "Building uberjar")
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis @basis}))
