(ns ^:figwheel-always solitaire.view.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]
              [solitaire.logic :as l]
              [solitaire.view.actions :as a]))

(enable-console-print!)

(defonce app-state (atom (l/new-game-state)))

(defn card
  [card-map card-place-name]
  (let [id (:id card-map)]
    (if (:facing-up card-map)
      [:div.card-size.card-face
       {:on-click #(a/select-or-move-card! id app-state card-place-name)
        :class (when (:selected? card-map) "selected")}
       (when (:facing-up card-map)
         [:p.pull-left.card-content
          {:class (if (#{:diamond :heart} (:suite card-map))
                    "red"
                    "black")}
          id])]
      [:div.card-size.facing-down
       {:on-click #(a/turn-card! app-state card-map card-place-name)}])))

(defn card-place
  ([]
   [:div.card-place.card-size])
  ([app-state card-place-name]
   (if-let [c (first (get @app-state card-place-name))]
     [:div.card-place (card c card-place-name)]
     [:div.card-place.card-size
      (when (= :stock card-place-name)
        {:on-click #(a/reset-stock! app-state)})])))

(defn board
  []
  [:div.container
   (comment [:h1 "Klondike Solitaire"])
   [:div.container.board
    [:div.row
     ;; top row
     [:div.col-xs-4
      [:div.pull-left (card-place app-state :stock)]
      [:div.col-xs-1 (card-place app-state :waste-heap)]]
     [:div.col-xs-offset-1.col-xs-7.pull-right
      [:div.col-xs-3 (card-place app-state :foundation1)]
      [:div.col-xs-3 (card-place app-state :foundation2)]
      [:div.col-xs-3 (card-place app-state :foundation3)]
      [:div.col-xs-3 (card-place app-state :foundation4)]]]

    ;; spacing
    [:div.row.card-size]

    ;; bottom row
    [:div.row.card-size
     [:div.col-xs-2 (card-place app-state :tableau1)]
     [:div.col-xs-2 (card-place app-state :tableau2)]
     [:div.col-xs-2 (card-place app-state :tableau3)]
     [:div.col-xs-2 (card-place app-state :tableau4)]
     [:div.col-xs-2 (card-place app-state :tableau5)]
     [:div.col-xs-2 (card-place app-state :tableau6)]]
    ;; spacing
    [:div.row.card-size]]
   [:div.row
    [:a {:href "test.html"} "Tests"]]])

(reagent/render-component [board]
                          (.getElementById js/document "app"))

(fw/start)
