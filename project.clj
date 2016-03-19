(defproject gilded-gauge "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.34"]
                 [org.clojure/core.async "0.2.374"]
                 [sablono "0.6.2"]
                 [org.omcljs/om "0.9.0"]
                 [cljsjs/react-dom "0.14.3-1"]
                 [cljsjs/react-dom-server "0.14.3-0"]
                 [cljsjs/pixi "3.0.10-0"]
                 [cljsjs/matter "0.9.1-0"]
                 [org.clojure/data.json "0.2.6"]
                 [enlive "1.1.6"]]


  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-figwheel "0.5.0-6"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]

                :figwheel {:on-jsload "gilded-gauge.core/on-js-reload"}

                :compiler {:main gilded-gauge.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/gilded_gauge.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/gilded_gauge.js"
                           :main gilded-gauge.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
