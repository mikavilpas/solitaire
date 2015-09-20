(ns solitaire.dev
  (:require [figwheel.client :as fw]
            [solitaire.view.core :refer [app-state]]
            [solitaire.view.actions :refer [init-game-loop]]))

(enable-console-print!)

;; contains a shutdown function of no arguments
(defonce system (atom (init-game-loop app-state)))

(defn start-system [app-state]
  (reset! system (init-game-loop app-state)))

(comment
  (js/document.getElementById "app"))

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
