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
      ;; (swap! app-state l/move-card card-id @app-state)
      (swap! app-state assoc-in [:selected-card-id] card-id))))

(defn turn-card! [app-state card card-place-name]
  (swap! app-state l/turn-card card card-place-name))

(defn card
  [card-map card-place-name]
  (let [id (:id card-map)]
    (if (:facing-up card-map)
      [:div.card-size.card-face
       {:on-click #(select-or-move-card! id app-state)
        :class (when (:selected? card-map)
                 "selected")}
       (when (:facing-up card-map)
         [:p.pull-left.card-content
          {:class (if (#{:diamond :heart} (:suite card-map))
                    "red"
                    "black")}
          id])]
      [:div.card-size.facing-down
       {:on-click #(turn-card! app-state card-map card-place-name)}])))

(defn card-place
  ([]
   [:div.card-place.card-size])
  ([app-state card-place-name]
   (if-let [c (first (get app-state card-place-name))]
     [:div.card-place (card c card-place-name)]
     (card-place))))

(defn board
  []
  [:div.container
   (comment [:h1 "Klondike Solitaire"])
   [:div.container.board
    [:div.row
     ;; top row
     [:div.col-xs-4
      [:div.pull-left (card-place @app-state :stock)]
      [:div.col-xs-1 (card-place)]]
     [:div.col-xs-offset-1.col-xs-7.pull-right
      [:div.col-xs-3 (card-place)]
      [:div.col-xs-3 (card-place)]
      [:div.col-xs-3 (card-place)]
      [:div.col-xs-3 (card-place)]]]

    ;; spacing
    [:div.row.card-size]

    ;; bottom row
    [:div.row.card-size
     [:div.col-xs-2 (card-place @app-state :tableau1)]
     [:div.col-xs-2 (card-place @app-state :tableau2)]
     [:div.col-xs-2 (card-place @app-state :tableau3)]
     [:div.col-xs-2 (card-place @app-state :tableau4)]
     [:div.col-xs-2 (card-place @app-state :tableau5)]
     [:div.col-xs-2 (card-place @app-state :tableau6)]]
    ;; spacing
    [:div.row.card-size]]
   [:div.row
    [:a {:href "test.html"} "Tests"]]])

(reagent/render-component [board]
                          (.getElementById js/document "app"))

(fw/start)
