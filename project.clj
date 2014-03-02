(defproject cli-socnet "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.6.0"]
                 [com.ocpsoft/ocpsoft-pretty-time "1.0.6"]]
  :profiles {:dev {:dependencies [[speclj "2.6.1"]]}
             :uberjar {:aot :all}}
  :plugins [[speclj "2.6.1"]]
  :test-paths ["spec"]
  :main cli-socnet.cli)
