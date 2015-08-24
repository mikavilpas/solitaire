(ns ^:figwheel-always solitaire.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]
              [solitaire.logic :as l]))

(enable-console-print!)

(comment
  (do
    (use 'figwheel-sidecar.repl-api)
    (cljs-repl)))

;; I don't know solitaire terms so I just copy these from wikipedia
(def app-state
  (atom l/new-game-state))

(defn card [card-map]
  (let [suite (:suite card-map)]
    [:div.card-size.card-face
     [:p.pull-left
      {:class (if (#{:diamond :heart} suite)
                "red"
                "black")}
      (str (l/card-symbol card-map)
           (l/rank-as-number card-map))]]))

(defn empty-card-place []
  [:div.card-size.card-place])

(defn card-place [card-list]
  [:div.card-place
   (when (not-empty card-list)
     (card (first card-list)))])

(defn board []
  [:div.container
   (comment [:h1 "Klondike Solitaire"])
   [:div.container.board
    [:div.row
     ;; top row
     [:div.col-xs-2 (card-place (:stock @app-state))]
     [:div.col-xs-2.col-xs-offset-2 (empty-card-place)]
     [:div.col-xs-2 (empty-card-place)]
     [:div.col-xs-2 (empty-card-place)]
     [:div.col-xs-2 (empty-card-place)]]]])

(reagent/render-component [board]
                          (js/document.getElementById "app"))

(fw/start)
