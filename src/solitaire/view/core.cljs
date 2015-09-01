(ns ^:figwheel-always solitaire.view.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]
              [solitaire.logic :as l]
              [solitaire.view.actions :as a]))

(enable-console-print!)

(def app-state (atom (l/new-game-state)))

(defn selectable [card-place-name]
  {:on-click #(a/select-or-move! app-state card-place-name)
   :class (when (= (:selected-place @app-state)
                   card-place-name)
            "selected")})

(defn card
  [card-map card-place-name]
  (if-not (:facing-up card-map)
    [:div.card-size.facing-down
     {:on-click #(a/turn-card! app-state card-map card-place-name)}]
    [:div.card-size.card-face
     [:div.selected-overlay
      (selectable card-place-name)
      (when (:facing-up card-map)
        [:p.card-content
         {:class (if (#{:diamond :heart} (:suite card-map))
                   "red"
                   "black")}
         (:id card-map)])]]))

(defn card-place
  ([app-state card-place-name & {:keys [fanned?]
                                 :or {fanned? false}}]
   (let [cards (get @app-state card-place-name)]
     (if (not (empty? cards))
       [:div.card-place
        [:div.selected-overlay (selectable card-place-name)
         (if fanned?
           [:div.overlapping-cards
            (for [c cards] (card c card-place-name))]
           (card (first cards) card-place-name))]]
       [:div.card-place.card-size
        (merge (selectable card-place-name)
               (when (= :stock card-place-name)
                 {:on-click #(a/reset-stock! app-state)}))]))))

(defn board
  []
  [:div.container
   (comment [:h1 "Klondike Solitaire"])
   [:div.container.board
    [:div.row
     ;; top row
     [:div.col-xs-4
      [:div.pull-left.col-xs-5 (card-place app-state :stock)]
      [:div.col-xs-6 (card-place app-state :waste-heap)]]
     [:div.col-xs-offset-1.col-xs-7.pull-right
      [:div.col-xs-3 (card-place app-state :foundation1)]
      [:div.col-xs-3 (card-place app-state :foundation2)]
      [:div.col-xs-3 (card-place app-state :foundation3)]
      [:div.col-xs-3 (card-place app-state :foundation4)]]]

    ;; spacing
    [:div.row.card-size]

    ;; bottom row
    [:div.row.card-size
     [:div.col-xs-2 (card-place app-state :tableau1 :fanned? true)]
     [:div.col-xs-2 (card-place app-state :tableau2 :fanned? true)]
     [:div.col-xs-2 (card-place app-state :tableau3 :fanned? true)]
     [:div.col-xs-2 (card-place app-state :tableau4 :fanned? true)]
     [:div.col-xs-2 (card-place app-state :tableau5 :fanned? true)]
     [:div.col-xs-2 (card-place app-state :tableau6 :fanned? true)]]
    ;; spacing
    [:div.row.card-size]]
   [:div.row
    [:a {:href "test.html"} "Tests"]]])

(reagent/render-component [board]
                          (.getElementById js/document "app"))

(fw/start)
