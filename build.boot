(defn read-file   [file] (read-string (slurp file)))
(defn shadow-cljs-edn [] (read-file "./shadow-cljs.edn"))
(defn get-deps    []     (:dependencies (shadow-cljs-edn)))

(set-env!
 :dependencies (into (get-deps) '[[org.clojure/clojure "1.10.0"]
                                  [org.clojure/clojurescript "1.10.520"]])

 :resource-paths #{"src"})

(require
 '[degree9.boot-semver :refer :all])

(task-options!
 pom    {:project 'degree9/enterprise
         :description "Degree9 Enterprise Platform."
         :url "http://github.com/degree9/enterprise"
         :scm {:url "http://github.com/degree9/enterprise"}})

(deftask deploy
  "Build project for deployment to clojars."
  []
  (comp
    (version)
    (build-jar)
    (push-release)))

(deftask develop
  "Build project for local development."
  []
  (comp
    (version :develop true
             :pre-release 'snapshot)
    (watch)
    (build-jar)))
