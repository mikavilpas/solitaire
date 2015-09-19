(ns ^:figwheel-always solitaire.view.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]
              [solitaire.core.logic :as l]
              [cljs.core.async :refer [put!]]
              [solitaire.view.actions :refer [game-chan init-game-loop]]))

;; this is the state that the game logic handles and "modifies" by
;; returning a new state
(defonce app-state (atom (l/new-game-state)))

;; ui specific state for graphical effects and such
(def ui-state (atom {:hinted-card-places #{}}))

(defn game-event [event-name & arguments]
  (put! game-chan (into [event-name] arguments))
  ;; stop the browser from complaining about calling stopPropagation
  ;; instead of returning false
  nil)

(defn selectable [card-place-name]
  ;; stock can never be selected
  (let [properties
        {:class (cond ((:hinted-card-places @ui-state) card-place-name)
                      "hinted"
                      (= card-place-name
                         (:selected-place @app-state))
                      "selected")}]
    (if-not (= :stock card-place-name)
      (merge {:on-click #(do (game-event :select-or-move
                                         app-state
                                         card-place-name)
                             ;; don't let the click event bubble to
                             ;; the board. this would cause the card
                             ;; to be deselected immediately.
                             (.stopPropagation %))}
             properties)
      properties)))

(defn card
  [card-map card-place-name]
  (if-not (:facing-up card-map)
    [:div.card-size.facing-down
     {:on-click #(game-event :turn-card app-state card-map card-place-name)}]

    [:div.card-size.card-face {:key (:id card-map)}
     [:div.selected-overlay
      (selectable card-place-name)
      (when (:facing-up card-map)
        [:p.card-content
         {:class (if (#{:diamond :heart} (:suite card-map))
                   "red"
                   "black")}
         (:id card-map)])]]))

(defn card-place [card-place-name & {:keys [fanned?]
                                     :or {fanned? false}}]
  (let [cards (get @app-state card-place-name)]
    (if (empty? cards)
      [:div.selected-overlay (selectable card-place-name)
       [:div.card-place.card-size
        (when (= :stock card-place-name)
          {:on-click #(game-event :reset-stock app-state)})]]
      [:div.card-place
       [:div.selected-overlay (selectable card-place-name)
        (if fanned?
          ;; the first card is normal,
          ;; the rest are overlapping
          [:div [card (first cards) card-place-name]
           [:div.overlapping-cards
            (doall (for [c (rest cards)]
                     ^{:key (:id c)}
                     [card c card-place-name]))]]
          [card (last cards) card-place-name])]])))

(defn board []
  [:div
   [:h1 "Klondike solitaire"]
   [:div.board.container-fluid
    {:on-click #(game-event :deselect app-state)}
    [:div.row
     ;; top row
     [:div.col-xs-4
      [:div.col-xs-6 [card-place :stock]]
      [:div.col-xs-6 [card-place :waste-heap]]]
     [:div.col-xs-offset-1.col-xs-7.pull-right
      [:div.col-xs-3 [card-place :foundation1]]
      [:div.col-xs-3 [card-place :foundation2]]
      [:div.col-xs-3 [card-place :foundation3]]
      [:div.col-xs-3 [card-place :foundation4]]]]

    [:div.row.half-card-size]

    ;; bottom row
    [:div.row.card-size.seven-cols
     [:div.col-md-1 [card-place :tableau1 :fanned? true]]
     [:div.col-md-1 [card-place :tableau2 :fanned? true]]
     [:div.col-md-1 [card-place :tableau3 :fanned? true]]
     [:div.col-md-1 [card-place :tableau4 :fanned? true]]
     [:div.col-md-1 [card-place :tableau5 :fanned? true]]
     [:div.col-md-1 [card-place :tableau6 :fanned? true]]
     [:div.col-md-1 [card-place :tableau7 :fanned? true]]]
    ;; spacing
    [:div.row.card-size]]
   [:div.container
    [:div.row
     [:div.col-xs-2
      [:button.btn.btn-lg {:type "button"
                           :on-click #(game-event :new-game app-state)}
       "New game"]]
     [:div.col-xs-2 [:button.btn.btn-lg {:type "button"
                                         :on-click #(game-event :undo app-state)}
                     "Undo"]]
     [:div.col-xs-2 [:button.btn.btn-lg {:type "button"
                                         :on-click #(game-event :show-hint app-state ui-state)}
                     "Hint"]]]
    [:div.row [:div.pull-left
               [:h3 [:a {:href "test.html"} "Tests"]]]]]])









;; development time convenience follows, this could be moved to another file

;; contains a shutdown function of no arguments
(defonce system (atom (init-game-loop app-state)))

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

(reagent/render-component [board]
                          (js/document.getElementById "app"))
