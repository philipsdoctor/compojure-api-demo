(defproject compojure-api-demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [metosin/compojure-api "1.1.9" :exclusions [org.clojure/clojure]]
                 [ring/ring-jetty-adapter "1.5.0"]]
  :main ^:skip-aot compojure-api-demo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
