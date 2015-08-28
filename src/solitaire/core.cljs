(ns ^:figwheel-always solitaire.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]
              [solitaire.logic :as l]))

(enable-console-print!)

(def app-state (atom (l/new-game-state)))

(defn select-or-move-card!
  "card-id is e.g. ♥2, the card that was clicked"
  [card-id app-state]
  (let [different-card-selected (and (:selected-card-id @app-state)
                                     (not (= (:selected-card-id @app-state)
                                             card-id)))]
    (if different-card-selected
      (swap! app-state l/move-card card-id @app-state)
      (swap! app-state assoc-in [:selected-card-id] card-id))))

(defn card
  [card-map]
  (let [suite (:suite card-map)
        id (:id card-map)]
    [:div.card-size.card-face
     {:on-click #(select-or-move-card! id app-state)
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
     [:div.col-xs-2 (card-place (:foundation6 @app-state))]]]
   [:div.row
    [:a {:href "test.html"} "Tests"]]])

(reagent/render-component [board]
                          (.getElementById js/document "app"))

(fw/start)
