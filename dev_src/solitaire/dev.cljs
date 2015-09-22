(ns solitaire.dev
  (:require [figwheel.client :as fw]
            [solitaire.view.core :refer [app-state system]]
            [solitaire.view.actions :refer [init-game-loop]]))

(comment
  (do
    (use 'figwheel-sidecar.repl-api)
    (cljs-repl)))

(enable-console-print!)

(defn start-system [app-state]
  (reset! system (init-game-loop app-state)))

(defn stop-system []
  (if-let [stop-system! @system]
    (stop-system!)
    ;; this is weird behaviour, warn about it
    (print "cannot stop system as it's not started")))

(defn restart-system [app-state]
  (stop-system)
  (start-system app-state))

(fw/start {:build-id "dev"
           :on-jsload #(restart-system app-state)})

(print "Development code loaded.")
