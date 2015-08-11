(ns ^:figwheel-always solitaire.core
    (:require [reagent.core :as reagent :refer [atom]]
              [figwheel.client :as fw]))

(enable-console-print!)

(comment
  (do
    (use 'figwheel-sidecar.repl-api)
    (cljs-repl)))

;; I don't know solitaire terms so I just copy these from wikipedia
(def app-state
  (atom {:stock {}
         :waste-heap {}
         ;; A 2 3 4 5 6 7 8 9 10 J Q K
         :foundations []
         ;; K Q J 10 9 8 7 6 5 4 3 2 A
         :tableau []}))

(defn card-place []
  [:div.card.card-place])

(defn board []
  [:div.container
   [:h1 "Klondike Solitaire"]
   [:div.container.board
    [:div.row
     ;; top row
     [:div.col-xs-2 (card-place)]
     [:div.col-xs-2.col-xs-offset-2 (card-place)]
     [:div.col-xs-2 (card-place)]
     [:div.col-xs-2 (card-place)]
     [:div.col-xs-2 (card-place)]

     ]
    ]])

(reagent/render-component [board]
                          (js/document.getElementById "app"))

(fw/start)

