(defproject solitaire "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.844"]
                 [org.clojure/core.async "1.3.618"]
                 [reagent "1.0.0"]]

  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-figwheel "0.5.20"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "resources/public/js/test"
                                    "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src" "dev_src"]
                        :compiler {:main solitaire.dev
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/solitaire.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :none
                                   :source-map true}}
                       {:id "test"
                        :source-paths ["src/solitaire/core" "test"]
                        :compiler {:main solitaire.test-runner
                                   :output-to "resources/public/js/test/test.js"
                                   :output-dir "resources/public/js/test/out"
                                   :asset-path "js/test/out"}}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/min/solitaire.js"
                                   :main solitaire.view.core
                                   :optimizations :advanced
                                   :source-map "resources/public/js/min/solitaire.js.map"
                                   :pretty-print false}}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources" 
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1" 

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log" 
             })
