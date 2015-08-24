(ns ^:figwheel-always solitaire.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]
              [solitaire.logic :as l]))

(enable-console-print!)

(comment
  (do
    (use 'figwheel-sidecar.repl-api)
    (cljs-repl)))

(def app-state (atom (l/new-game-state)))

(defn select-card!
  "card-id is e.g. ♥2"
  [card-id app-state]
  (swap! app-state
         assoc-in [:selected-card] card-id))

(defn card
  [card-map]
  (let [suite (:suite card-map)
        id (str (l/card-symbol card-map)
                (l/rank-as-symbol card-map))]
    [:div.card-size.card-face
     {:on-click #(select-card! id app-state)
      :class (when (:selected? card-map)
               "selected")}
     [:p.pull-left.card-content
      {:class (if (#{:diamond :heart} suite)
                "red"
                "black")}
      id]]))

(defn card-place
  ([]
   [:div.card-place.card-size])
  ([card-list]
   (if-let [c (first card-list)]
     [:div.card-place (card c)]
     (card-place))))

(defn board
  []
  [:div.container
   (comment [:h1 "Klondike Solitaire"])
   [:div.container.board
    [:div.row
     ;; top row
     [:div.col-xs-2 (card-place (:stock @app-state))]
     [:div.col-xs-2.col-xs-offset-2 (card-place)]
     [:div.col-xs-2 (card-place)]
     [:div.col-xs-2 (card-place)]
     [:div.col-xs-2 (card-place)]]

    ;; spacing
    [:div.row.card-size]

    ;; bottom row
    [:div.row.card-size
     [:div.col-xs-2 (card-place (:foundation1 @app-state))]
     [:div.col-xs-2 (card-place (:foundation2 @app-state))]
     [:div.col-xs-2 (card-place (:foundation3 @app-state))]
     [:div.col-xs-2 (card-place (:foundation4 @app-state))]
     [:div.col-xs-2 (card-place (:foundation5 @app-state))]
     [:div.col-xs-2 (card-place (:foundation6 @app-state))]]]])

(reagent/render-component [board]
                          (js/document.getElementById "app"))

(fw/start)
