(defproject cljs-uploader "0.1.0"
  :description "Example app. 
               Explains how upload files using 
               ajax and Clojurescript"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"] 
                 [domina "1.0.2"]
                 [compojure "1.1.5"]
                 [ring "1.1.8"]]
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.3"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild { 
    :builds {
      :main {
        :source-paths ["src/cljs"]
        :compiler {:output-to "resources/public/js/cljs.js"
                   :optimizations :simple
                   :pretty-print true}
        :jar true}}}
  :main cljs-uploader.server
  :ring {:handler cljs-uploader.server/app-routes})

