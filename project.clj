(defproject pertgen "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:debug-repl {:resource-paths [#=(eval (System/getenv "PATH_TO_TOOLS_JAR"))]
              :repl-options {:nrepl-middleware [debug-middleware.core/debug-middleware]}
              :dependencies [[debug-middleware #=(eval (System/getenv "DEBUG_MIDDLEWARE_VERSION"))]]}})            

    
