(ns solitaire.view.actions
  (:require [solitaire.logic :as l]))

;; todo disallow selecting stock somehow
(defn select-or-move! [app-state card-place-name]
  (print "setting :selected-place to " card-place-name)
  (swap! app-state assoc-in [:selected-place] card-place-name))

(defn turn-card! [app-state card card-place-name]
  (swap! app-state l/turn-card card card-place-name))

(defn reset-stock! [app-state]
  (swap! app-state l/reset-stock))
