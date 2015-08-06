(ns ^:figwheel-always solitaire.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]))

(enable-console-print!)

(comment
  (do
    (use 'figwheel-sidecar.repl-api)
    (cljs-repl)))

(def app-state (atom {:text "Hello world!"}))

(defn board []
  [:div.board
   [:h1 (:text @app-state)]])

(reagent/render-component [board]
                          (. js/document (getElementById "app")))

(fw/start)
